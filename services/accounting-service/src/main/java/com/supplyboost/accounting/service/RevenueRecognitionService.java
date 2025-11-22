package com.supplyboost.accounting.service;

import com.supplyboost.accounting.model.Invoice;
import com.supplyboost.accounting.model.RevenueRecognition;
import com.supplyboost.accounting.model.RevenueRecognitionType;
import com.supplyboost.accounting.model.RevenueStatus;
import com.supplyboost.accounting.repository.RevenueRecognitionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RevenueRecognitionService {

  private final RevenueRecognitionRepository revenueRecognitionRepository;

  @Transactional
  public RevenueRecognition recognizeRevenue(Invoice invoice, String notes) {
    // Revenue is recognized when goods are shipped
    RevenueRecognition recognition =
        RevenueRecognition.builder()
            .invoiceId(invoice.getId())
            .invoiceNumber(invoice.getInvoiceNumber())
            .orderId(invoice.getOrderId())
            .orderNumber(invoice.getOrderNumber())
            .amount(invoice.getSubtotal()) // Recognize subtotal (excluding tax)
            .recognitionDate(LocalDateTime.now())
            .type(RevenueRecognitionType.PRODUCT_SALE)
            .status(RevenueStatus.RECOGNIZED)
            .notes(notes)
            .build();

    recognition = revenueRecognitionRepository.save(recognition);

    log.info(
        "Revenue recognized: ${} for invoice: {}",
        recognition.getAmount(),
        invoice.getInvoiceNumber());

    return recognition;
  }

  @Transactional
  public void recognizeTax(Invoice invoice) {
    // Recognize tax collected separately
    RevenueRecognition taxRecognition =
        RevenueRecognition.builder()
            .invoiceId(invoice.getId())
            .invoiceNumber(invoice.getInvoiceNumber())
            .orderId(invoice.getOrderId())
            .orderNumber(invoice.getOrderNumber())
            .amount(invoice.getTaxAmount())
            .recognitionDate(LocalDateTime.now())
            .type(RevenueRecognitionType.TAX_COLLECTED)
            .status(RevenueStatus.RECOGNIZED)
            .notes("Tax collected on product sale")
            .build();

    revenueRecognitionRepository.save(taxRecognition);

    log.info(
        "Tax revenue recognized: ${} for invoice: {}",
        taxRecognition.getAmount(),
        invoice.getInvoiceNumber());
  }

  public BigDecimal getTotalRecognizedRevenue(LocalDateTime start, LocalDateTime end) {
    BigDecimal total =
        revenueRecognitionRepository.sumRevenueByStatusAndDateRange(
            RevenueStatus.RECOGNIZED, start, end);
    return total != null ? total : BigDecimal.ZERO;
  }
}
