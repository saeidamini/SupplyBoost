package com.supplyboost.notification.consumer;

import com.supplyboost.notification.event.ShipmentCreatedEvent;
import com.supplyboost.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentEventConsumer {

  private final EmailService emailService;

  @KafkaListener(topics = "shipment.created", groupId = "notification-service-group")
  public void handleShipmentCreated(ShipmentCreatedEvent event) {
    log.info("Received shipment created event for shipment: {}", event.getShipmentNumber());

    try {
      emailService.sendShipmentNotification(
          event.getRecipientEmail(),
          event.getRecipientName(),
          event.getOrderNumber(),
          event.getShipmentNumber(),
          event.getTrackingNumber(),
          event.getCarrier(),
          event.getEstimatedDelivery());

      log.info(
          "Shipment notification email sent successfully for shipment: {}",
          event.getShipmentNumber());

    } catch (Exception e) {
      log.error(
          "Failed to send shipment notification email for shipment: {}",
          event.getShipmentNumber(),
          e);
    }
  }
}
