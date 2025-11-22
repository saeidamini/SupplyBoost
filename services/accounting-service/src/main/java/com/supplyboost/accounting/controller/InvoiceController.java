package com.supplyboost.accounting.controller;

import com.supplyboost.accounting.model.Invoice;
import com.supplyboost.accounting.service.InvoiceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

  private final InvoiceService invoiceService;

  @GetMapping("/order/{orderNumber}")
  public ResponseEntity<Invoice> getInvoiceByOrderNumber(@PathVariable String orderNumber) {
    log.info("Getting invoice for order: {}", orderNumber);
    Invoice invoice = invoiceService.getInvoiceByOrderNumber(orderNumber);
    return ResponseEntity.ok(invoice);
  }

  @GetMapping("/customer/{email}")
  public ResponseEntity<List<Invoice>> getInvoicesByCustomerEmail(@PathVariable String email) {
    log.info("Getting invoices for customer: {}", email);
    List<Invoice> invoices = invoiceService.getInvoicesByCustomerEmail(email);
    return ResponseEntity.ok(invoices);
  }

  @PostMapping("/{invoiceId}/issue")
  public ResponseEntity<Invoice> issueInvoice(@PathVariable Long invoiceId) {
    log.info("Issuing invoice: {}", invoiceId);
    Invoice invoice = invoiceService.issueInvoice(invoiceId);
    return ResponseEntity.ok(invoice);
  }

  @PostMapping("/{invoiceId}/mark-paid")
  public ResponseEntity<Invoice> markAsPaid(@PathVariable Long invoiceId) {
    log.info("Marking invoice as paid: {}", invoiceId);
    Invoice invoice = invoiceService.markAsPaid(invoiceId);
    return ResponseEntity.ok(invoice);
  }
}
