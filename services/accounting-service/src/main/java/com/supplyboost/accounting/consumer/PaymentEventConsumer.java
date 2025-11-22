package com.supplyboost.accounting.consumer;

import com.supplyboost.accounting.event.PaymentProcessedEvent;
import com.supplyboost.accounting.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

  private final InvoiceService invoiceService;

  @KafkaListener(topics = "payment.processed", groupId = "accounting-service-group")
  public void handlePaymentProcessed(PaymentProcessedEvent event) {
    log.info("Received payment processed event for payment: {}", event.getPaymentNumber());

    try {
      // Only create invoice if payment was successful
      if ("SUCCEEDED".equals(event.getStatus())) {
        invoiceService.createInvoice(
            event.getOrderId(),
            event.getOrderNumber(),
            null, // userId not provided in event
            event.getCustomerName(),
            event.getCustomerEmail(),
            event.getAmount(),
            event.getPaymentNumber());

        log.info("Invoice created for payment: {}", event.getPaymentNumber());
      } else {
        log.warn("Payment failed for order {}, no invoice created", event.getOrderNumber());
      }

    } catch (Exception e) {
      log.error("Failed to create invoice for payment: {}", event.getPaymentNumber(), e);
    }
  }
}
