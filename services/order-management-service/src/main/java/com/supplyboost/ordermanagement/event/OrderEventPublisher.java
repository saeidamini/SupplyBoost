package com.supplyboost.ordermanagement.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  private static final String ORDER_CREATED_TOPIC = "order.created";
  private static final String ORDER_STATUS_CHANGED_TOPIC = "order.status.changed";

  public void publishOrderCreated(OrderCreatedEvent event) {
    try {
      kafkaTemplate.send(ORDER_CREATED_TOPIC, event.getOrderNumber(), event);
      log.info("Published order created event for order: {}", event.getOrderNumber());
    } catch (Exception e) {
      log.error("Failed to publish order created event", e);
    }
  }

  public void publishOrderStatusChanged(OrderStatusChangedEvent event) {
    try {
      kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, event.getOrderNumber(), event);
      log.info(
          "Published order status changed event for order: {} from {} to {}",
          event.getOrderNumber(),
          event.getOldStatus(),
          event.getNewStatus());
    } catch (Exception e) {
      log.error("Failed to publish order status changed event", e);
    }
  }
}
