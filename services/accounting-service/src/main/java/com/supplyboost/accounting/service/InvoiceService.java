package com.supplyboost.accounting.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.supplyboost.accounting.config.AccountingConfig;
import com.supplyboost.accounting.model.Invoice;
import com.supplyboost.accounting.model.InvoiceStatus;
import com.supplyboost.accounting.repository.InvoiceRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

  private final InvoiceRepository invoiceRepository;
  private final AccountingConfig accountingConfig;

  @Transactional
  public Invoice createInvoice(
      Long orderId,
      String orderNumber,
      Long userId,
      String customerName,
      String customerEmail,
      BigDecimal subtotal,
      String paymentId) {

    // Check if invoice already exists for this order
    if (invoiceRepository.existsByOrderId(orderId)) {
      log.warn("Invoice already exists for order: {}", orderNumber);
      return invoiceRepository.findByOrderId(orderId).orElseThrow();
    }

    // Calculate tax and total
    BigDecimal taxAmount = subtotal.multiply(accountingConfig.getTaxRate());
    BigDecimal totalAmount = subtotal.add(taxAmount);

    Invoice invoice =
        Invoice.builder()
            .invoiceNumber(generateInvoiceNumber())
            .orderId(orderId)
            .orderNumber(orderNumber)
            .userId(userId)
            .customerName(customerName)
            .customerEmail(customerEmail)
            .subtotal(subtotal)
            .taxAmount(taxAmount)
            .taxRate(accountingConfig.getTaxRate())
            .totalAmount(totalAmount)
            .status(InvoiceStatus.DRAFT)
            .paymentId(paymentId)
            .dueDate(LocalDateTime.now().plusDays(30))
            .build();

    invoice = invoiceRepository.save(invoice);
    log.info("Invoice created: {} for order: {}", invoice.getInvoiceNumber(), orderNumber);

    return invoice;
  }

  @Transactional
  public Invoice issueInvoice(Long invoiceId) {
    Invoice invoice =
        invoiceRepository
            .findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

    invoice.setStatus(InvoiceStatus.ISSUED);

    // Generate PDF
    String pdfPath = generateInvoicePdf(invoice);
    invoice.setPdfFilePath(pdfPath);

    invoice = invoiceRepository.save(invoice);
    log.info("Invoice issued: {}", invoice.getInvoiceNumber());

    return invoice;
  }

  @Transactional
  public Invoice markAsPaid(Long invoiceId) {
    Invoice invoice =
        invoiceRepository
            .findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

    invoice.setStatus(InvoiceStatus.PAID);
    invoice.setPaidAt(LocalDateTime.now());

    invoice = invoiceRepository.save(invoice);
    log.info("Invoice marked as paid: {}", invoice.getInvoiceNumber());

    return invoice;
  }

  @Transactional
  public void associateShipment(Long orderId, Long shipmentId, String shipmentNumber) {
    Invoice invoice =
        invoiceRepository
            .findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Invoice not found for order: " + orderId));

    invoice.setShipmentId(shipmentId);
    invoice.setShipmentNumber(shipmentNumber);

    invoiceRepository.save(invoice);
    log.info(
        "Shipment {} associated with invoice: {}", shipmentNumber, invoice.getInvoiceNumber());
  }

  public Invoice getInvoiceByOrderNumber(String orderNumber) {
    return invoiceRepository
        .findByOrderNumber(orderNumber)
        .orElseThrow(() -> new RuntimeException("Invoice not found for order: " + orderNumber));
  }

  public List<Invoice> getInvoicesByCustomerEmail(String customerEmail) {
    return invoiceRepository.findByCustomerEmail(customerEmail);
  }

  private String generateInvoiceNumber() {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    Random random = new Random();
    int randomNum = 10000 + random.nextInt(90000);
    return "INV-" + timestamp + "-" + randomNum;
  }

  private String generateInvoicePdf(Invoice invoice) {
    try {
      // Create directory if it doesn't exist
      File directory = new File(accountingConfig.getPdfStoragePath());
      if (!directory.exists()) {
        directory.mkdirs();
      }

      String fileName = invoice.getInvoiceNumber() + ".pdf";
      String filePath = accountingConfig.getPdfStoragePath() + "/" + fileName;

      PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
      PdfDocument pdfDoc = new PdfDocument(writer);
      Document document = new Document(pdfDoc);

      // Add company header
      document.add(
          new Paragraph(accountingConfig.getCompanyName())
              .setFontSize(20)
              .setBold()
              .setTextAlignment(TextAlignment.CENTER));
      document.add(
          new Paragraph(accountingConfig.getCompanyAddress())
              .setFontSize(10)
              .setTextAlignment(TextAlignment.CENTER));
      document.add(
          new Paragraph(
                  "Email: "
                      + accountingConfig.getCompanyEmail()
                      + " | Phone: "
                      + accountingConfig.getCompanyPhone())
              .setFontSize(10)
              .setTextAlignment(TextAlignment.CENTER));

      document.add(new Paragraph("\n"));

      // Invoice details
      document.add(
          new Paragraph("INVOICE")
              .setFontSize(16)
              .setBold()
              .setTextAlignment(TextAlignment.CENTER));
      document.add(new Paragraph("\n"));

      document.add(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber()));
      document.add(
          new Paragraph(
              "Invoice Date: "
                  + invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
      document.add(
          new Paragraph(
              "Due Date: "
                  + invoice.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
      document.add(new Paragraph("Order Number: " + invoice.getOrderNumber()));

      document.add(new Paragraph("\n"));

      // Bill to
      document.add(new Paragraph("Bill To:").setBold());
      document.add(new Paragraph(invoice.getCustomerName()));
      document.add(new Paragraph(invoice.getCustomerEmail()));

      document.add(new Paragraph("\n"));

      // Invoice items table
      float[] columnWidths = {4, 1, 1, 1};
      Table table = new Table(columnWidths);
      table.setWidth(500);

      // Table header
      table.addCell("Description");
      table.addCell("Quantity");
      table.addCell("Price");
      table.addCell("Amount");

      // Table row
      table.addCell("Order: " + invoice.getOrderNumber());
      table.addCell("1");
      table.addCell("$" + invoice.getSubtotal());
      table.addCell("$" + invoice.getSubtotal());

      document.add(table);
      document.add(new Paragraph("\n"));

      // Totals
      document.add(
          new Paragraph("Subtotal: $" + invoice.getSubtotal())
              .setTextAlignment(TextAlignment.RIGHT));
      document.add(
          new Paragraph(
                  "Tax ("
                      + invoice.getTaxRate().multiply(new BigDecimal("100"))
                      + "%): $"
                      + invoice.getTaxAmount())
              .setTextAlignment(TextAlignment.RIGHT));
      document.add(
          new Paragraph("Total: $" + invoice.getTotalAmount())
              .setBold()
              .setTextAlignment(TextAlignment.RIGHT));

      document.add(new Paragraph("\n\n"));
      document.add(
          new Paragraph("Thank you for your business!")
              .setTextAlignment(TextAlignment.CENTER)
              .setItalic());

      document.close();

      log.info("PDF generated successfully: {}", filePath);
      return filePath;

    } catch (Exception e) {
      log.error("Failed to generate PDF for invoice: {}", invoice.getInvoiceNumber(), e);
      throw new RuntimeException("Failed to generate PDF", e);
    }
  }
}
