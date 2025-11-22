package com.supplyboost.shipping.service;

import com.supplyboost.shipping.dto.CreateShipmentRequest;
import com.supplyboost.shipping.dto.ShipmentResponse;
import com.supplyboost.shipping.event.ShipmentEvent;
import com.supplyboost.shipping.event.ShipmentEventPublisher;
import com.supplyboost.shipping.exception.ShipmentNotFoundException;
import com.supplyboost.shipping.mapper.ShipmentMapper;
import com.supplyboost.shipping.model.Shipment;
import com.supplyboost.shipping.model.ShipmentStatus;
import com.supplyboost.shipping.repository.ShipmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentService {

  private final ShipmentRepository shipmentRepository;
  private final ShipmentMapper shipmentMapper;
  private final ShipmentEventPublisher eventPublisher;

  @Transactional
  public ShipmentResponse createShipment(CreateShipmentRequest request) {
    Shipment shipment =
        Shipment.builder()
            .shipmentId(generateShipmentId())
            .trackingNumber(generateTrackingNumber())
            .orderId(request.getOrderId())
            .orderNumber(request.getOrderNumber())
            .userId(request.getUserId())
            .status(ShipmentStatus.PENDING)
            .carrier(request.getCarrier() != null ? request.getCarrier() : "STANDARD")
            .serviceType(
                request.getServiceType() != null ? request.getServiceType() : "GROUND")
            .recipientName(request.getRecipientName())
            .recipientEmail(request.getRecipientEmail())
            .recipientPhone(request.getRecipientPhone())
            .addressLine1(request.getAddressLine1())
            .addressLine2(request.getAddressLine2())
            .city(request.getCity())
            .state(request.getState())
            .postalCode(request.getPostalCode())
            .country(request.getCountry())
            .notes(request.getNotes())
            .estimatedDelivery(calculateEstimatedDelivery())
            .build();

    Shipment savedShipment = shipmentRepository.save(shipment);
    log.info(
        "Shipment created: {} for order: {} with tracking: {}",
        savedShipment.getShipmentId(),
        savedShipment.getOrderNumber(),
        savedShipment.getTrackingNumber());

    // Publish shipment created event
    publishShipmentEvent(savedShipment);

    return shipmentMapper.toShipmentResponse(savedShipment);
  }

  @Transactional(readOnly = true)
  public ShipmentResponse getShipment(String shipmentId) {
    Shipment shipment =
        shipmentRepository
            .findByShipmentId(shipmentId)
            .orElseThrow(
                () ->
                    new ShipmentNotFoundException("Shipment not found: " + shipmentId));
    return shipmentMapper.toShipmentResponse(shipment);
  }

  @Transactional(readOnly = true)
  public ShipmentResponse getShipmentByTracking(String trackingNumber) {
    Shipment shipment =
        shipmentRepository
            .findByTrackingNumber(trackingNumber)
            .orElseThrow(
                () ->
                    new ShipmentNotFoundException(
                        "Shipment not found with tracking: " + trackingNumber));
    return shipmentMapper.toShipmentResponse(shipment);
  }

  @Transactional(readOnly = true)
  public ShipmentResponse getShipmentByOrderId(Long orderId) {
    Shipment shipment =
        shipmentRepository
            .findByOrderId(orderId)
            .orElseThrow(
                () ->
                    new ShipmentNotFoundException(
                        "Shipment not found for order: " + orderId));
    return shipmentMapper.toShipmentResponse(shipment);
  }

  @Transactional(readOnly = true)
  public List<ShipmentResponse> getUserShipments(Long userId) {
    List<Shipment> shipments = shipmentRepository.findByUserId(userId);
    return shipmentMapper.toShipmentResponses(shipments);
  }

  @Transactional
  public ShipmentResponse updateShipmentStatus(
      String shipmentId, ShipmentStatus newStatus) {
    Shipment shipment =
        shipmentRepository
            .findByShipmentId(shipmentId)
            .orElseThrow(
                () ->
                    new ShipmentNotFoundException("Shipment not found: " + shipmentId));

    ShipmentStatus oldStatus = shipment.getStatus();
    shipment.setStatus(newStatus);

    if (newStatus == ShipmentStatus.SHIPPED) {
      shipment.setShippedAt(LocalDateTime.now());
    } else if (newStatus == ShipmentStatus.DELIVERED) {
      shipment.setActualDelivery(LocalDateTime.now());
    }

    Shipment updatedShipment = shipmentRepository.save(shipment);
    log.info(
        "Shipment {} status updated from {} to {}",
        shipmentId,
        oldStatus,
        newStatus);

    // Publish status updated event
    publishShipmentStatusEvent(updatedShipment);

    return shipmentMapper.toShipmentResponse(updatedShipment);
  }

  private String generateShipmentId() {
    return "SHIP-"
        + System.currentTimeMillis()
        + "-"
        + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  private String generateTrackingNumber() {
    // Generate a realistic-looking tracking number
    Random random = new Random();
    return "1Z"
        + String.format("%06d", random.nextInt(1000000))
        + String.format("%08d", random.nextInt(100000000));
  }

  private LocalDateTime calculateEstimatedDelivery() {
    // Mock: Add 3-5 business days
    return LocalDateTime.now().plusDays(3 + new Random().nextInt(3));
  }

  private void publishShipmentEvent(Shipment shipment) {
    ShipmentEvent event =
        ShipmentEvent.builder()
            .shipmentId(shipment.getId())
            .shipmentNumber(shipment.getShipmentId())
            .trackingNumber(shipment.getTrackingNumber())
            .orderId(shipment.getOrderId())
            .orderNumber(shipment.getOrderNumber())
            .userId(shipment.getUserId())
            .status(shipment.getStatus())
            .carrier(shipment.getCarrier())
            .estimatedDelivery(shipment.getEstimatedDelivery())
            .eventTime(LocalDateTime.now())
            .build();

    eventPublisher.publishShipmentCreated(event);
  }

  private void publishShipmentStatusEvent(Shipment shipment) {
    ShipmentEvent event =
        ShipmentEvent.builder()
            .shipmentId(shipment.getId())
            .shipmentNumber(shipment.getShipmentId())
            .trackingNumber(shipment.getTrackingNumber())
            .orderId(shipment.getOrderId())
            .orderNumber(shipment.getOrderNumber())
            .userId(shipment.getUserId())
            .status(shipment.getStatus())
            .carrier(shipment.getCarrier())
            .estimatedDelivery(shipment.getEstimatedDelivery())
            .eventTime(LocalDateTime.now())
            .build();

    eventPublisher.publishShipmentStatusUpdated(event);
  }
}
