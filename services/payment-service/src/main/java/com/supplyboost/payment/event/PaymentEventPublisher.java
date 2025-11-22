package com.supplyboost.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  private static final String PAYMENT_PROCESSED_TOPIC = "payment.processed";

  public void publishPaymentProcessed(PaymentEvent event) {
    try {
      kafkaTemplate.send(PAYMENT_PROCESSED_TOPIC, event.getPaymentNumber(), event);
      log.info(
          "Published payment processed event for payment: {} with status: {}",
          event.getPaymentNumber(),
          event.getStatus());
    } catch (Exception e) {
      log.error("Failed to publish payment processed event", e);
    }
  }
}
