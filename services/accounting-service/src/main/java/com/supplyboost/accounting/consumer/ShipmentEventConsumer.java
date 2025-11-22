package com.supplyboost.accounting.consumer;

import com.supplyboost.accounting.event.ShipmentCreatedEvent;
import com.supplyboost.accounting.model.Invoice;
import com.supplyboost.accounting.service.InvoiceService;
import com.supplyboost.accounting.service.RevenueRecognitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentEventConsumer {

  private final InvoiceService invoiceService;
  private final RevenueRecognitionService revenueRecognitionService;

  @KafkaListener(topics = "shipment.created", groupId = "accounting-service-group")
  public void handleShipmentCreated(ShipmentCreatedEvent event) {
    log.info("Received shipment created event for shipment: {}", event.getShipmentNumber());

    try {
      // Associate shipment with invoice
      invoiceService.associateShipment(
          event.getOrderId(), event.getShipmentId(), event.getShipmentNumber());

      // Get invoice and issue it
      Invoice invoice = invoiceService.getInvoiceByOrderNumber(event.getOrderNumber());
      invoice = invoiceService.issueInvoice(invoice.getId());

      // Recognize revenue (revenue is recognized when goods are shipped)
      revenueRecognitionService.recognizeRevenue(
          invoice, "Revenue recognized on shipment: " + event.getShipmentNumber());

      // Recognize tax collected
      revenueRecognitionService.recognizeTax(invoice);

      // Mark invoice as paid (assuming payment already processed)
      invoiceService.markAsPaid(invoice.getId());

      log.info(
          "Invoice issued and revenue recognized for shipment: {}", event.getShipmentNumber());

    } catch (Exception e) {
      log.error(
          "Failed to process accounting for shipment: {}", event.getShipmentNumber(), e);
    }
  }
}
