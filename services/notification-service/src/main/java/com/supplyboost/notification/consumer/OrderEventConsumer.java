package com.supplyboost.notification.consumer;

import com.supplyboost.notification.event.OrderCreatedEvent;
import com.supplyboost.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

  private final EmailService emailService;

  @KafkaListener(topics = "order.created", groupId = "notification-service-group")
  public void handleOrderCreated(OrderCreatedEvent event) {
    log.info("Received order created event for order: {}", event.getOrderNumber());

    try {
      emailService.sendOrderConfirmation(
          event.getCustomerEmail(),
          event.getCustomerName(),
          event.getOrderNumber(),
          event.getTotalAmount().toString(),
          event.getStatus(),
          event.getEventTime());

      log.info(
          "Order confirmation email sent successfully for order: {}", event.getOrderNumber());

    } catch (Exception e) {
      log.error("Failed to send order confirmation email for order: {}", event.getOrderNumber(), e);
    }
  }
}
