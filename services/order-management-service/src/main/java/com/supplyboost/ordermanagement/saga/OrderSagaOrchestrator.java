package com.supplyboost.ordermanagement.saga;

import com.supplyboost.ordermanagement.event.PaymentProcessedEvent;
import com.supplyboost.ordermanagement.event.ShipmentCreatedEvent;
import com.supplyboost.ordermanagement.model.Order;
import com.supplyboost.ordermanagement.model.OrderStatus;
import com.supplyboost.ordermanagement.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

  private final OrderRepository orderRepository;
  private final RestTemplate restTemplate;

  @Value("${services.shipping.url:http://localhost:8087}")
  private String shippingServiceUrl;

  @Transactional
  @KafkaListener(topics = "payment.processed", groupId = "order-service-group")
  public void handlePaymentProcessed(PaymentProcessedEvent event) {
    log.info("Received payment processed event for order: {}", event.getOrderNumber());

    Order order =
        orderRepository
            .findById(event.getOrderId())
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Order not found: " + event.getOrderId()));

    if ("SUCCEEDED".equals(event.getStatus())) {
      // Payment successful - update order and create shipment
      order.setStatus(OrderStatus.PAYMENT_CONFIRMED);
      order.setPaymentId(event.getPaymentNumber());
      order.setPaymentStatus(event.getStatus());
      order.setPaymentMethod(event.getPaymentMethod());
      orderRepository.save(order);

      log.info("Payment confirmed for order: {}, creating shipment", order.getOrderNumber());

      // Create shipment
      createShipment(order);

    } else {
      // Payment failed - cancel order and compensate
      log.warn(
          "Payment failed for order: {} - Reason: {}",
          order.getOrderNumber(),
          event.getFailureReason());

      order.setStatus(OrderStatus.PAYMENT_FAILED);
      order.setPaymentStatus(event.getStatus());
      orderRepository.save(order);

      // TODO: Release inventory reservation (compensating transaction)
      log.info("Compensating transaction: releasing inventory for order {}", order.getId());
    }
  }

  @Transactional
  @KafkaListener(topics = "shipment.created", groupId = "order-service-group")
  public void handleShipmentCreated(ShipmentCreatedEvent event) {
    log.info("Received shipment created event for order: {}", event.getOrderNumber());

    Order order =
        orderRepository
            .findById(event.getOrderId())
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Order not found: " + event.getOrderId()));

    order.setStatus(OrderStatus.SHIPPED);
    order.setShipmentId(event.getShipmentNumber());
    order.setTrackingNumber(event.getTrackingNumber());
    orderRepository.save(order);

    log.info(
        "Order {} shipped with tracking number: {}",
        order.getOrderNumber(),
        event.getTrackingNumber());
  }

  private void createShipment(Order order) {
    try {
      String url = shippingServiceUrl + "/api/v1/shipments";

      Map<String, Object> shipmentRequest = new HashMap<>();
      shipmentRequest.put("orderId", order.getId());
      shipmentRequest.put("orderNumber", order.getOrderNumber());
      shipmentRequest.put("userId", order.getUserId());
      shipmentRequest.put("recipientName", order.getCustomerName());
      shipmentRequest.put("recipientEmail", order.getCustomerEmail());
      shipmentRequest.put("recipientPhone", order.getCustomerPhone());
      shipmentRequest.put("addressLine1", order.getShippingAddressLine1());
      shipmentRequest.put("addressLine2", order.getShippingAddressLine2());
      shipmentRequest.put("city", order.getShippingCity());
      shipmentRequest.put("state", order.getShippingState());
      shipmentRequest.put("postalCode", order.getShippingPostalCode());
      shipmentRequest.put("country", order.getShippingCountry());

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<Map<String, Object>> request =
          new HttpEntity<>(shipmentRequest, headers);

      restTemplate.postForObject(url, request, Object.class);
      log.info("Shipment creation request sent for order: {}", order.getOrderNumber());

    } catch (Exception e) {
      log.error("Failed to create shipment for order: {}", order.getOrderNumber(), e);
      // Update order status to indicate shipment creation failed
      order.setStatus(OrderStatus.READY_TO_SHIP);
      orderRepository.save(order);
    }
  }
}
