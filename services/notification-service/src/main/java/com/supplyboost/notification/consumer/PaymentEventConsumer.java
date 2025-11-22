package com.supplyboost.notification.consumer;

import com.supplyboost.notification.event.PaymentProcessedEvent;
import com.supplyboost.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

  private final EmailService emailService;

  @KafkaListener(topics = "payment.processed", groupId = "notification-service-group")
  public void handlePaymentProcessed(PaymentProcessedEvent event) {
    log.info("Received payment processed event for payment: {}", event.getPaymentNumber());

    try {
      // Only send confirmation if payment was successful
      if ("SUCCEEDED".equals(event.getStatus())) {
        emailService.sendPaymentConfirmation(
            event.getCustomerEmail(),
            event.getCustomerName(),
            event.getOrderNumber(),
            event.getPaymentNumber(),
            event.getPaymentMethod(),
            event.getAmount().toString(),
            event.getEventTime());

        log.info(
            "Payment confirmation email sent successfully for payment: {}",
            event.getPaymentNumber());
      } else {
        log.warn(
            "Payment failed for order {}: {}. No confirmation email sent.",
            event.getOrderNumber(),
            event.getFailureReason());
      }

    } catch (Exception e) {
      log.error(
          "Failed to send payment confirmation email for payment: {}", event.getPaymentNumber(), e);
    }
  }
}
