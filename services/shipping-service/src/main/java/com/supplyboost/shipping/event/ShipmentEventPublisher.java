package com.supplyboost.shipping.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  private static final String SHIPMENT_CREATED_TOPIC = "shipment.created";
  private static final String SHIPMENT_STATUS_UPDATED_TOPIC = "shipment.status.updated";

  public void publishShipmentCreated(ShipmentEvent event) {
    try {
      kafkaTemplate.send(SHIPMENT_CREATED_TOPIC, event.getShipmentNumber(), event);
      log.info("Published shipment created event for: {}", event.getShipmentNumber());
    } catch (Exception e) {
      log.error("Failed to publish shipment created event", e);
    }
  }

  public void publishShipmentStatusUpdated(ShipmentEvent event) {
    try {
      kafkaTemplate.send(SHIPMENT_STATUS_UPDATED_TOPIC, event.getShipmentNumber(), event);
      log.info(
          "Published shipment status updated event for: {} to status: {}",
          event.getShipmentNumber(),
          event.getStatus());
    } catch (Exception e) {
      log.error("Failed to publish shipment status updated event", e);
    }
  }
}
