package com.supplyboost.ordermanagement.service;

import com.supplyboost.ordermanagement.client.ShoppingCartClient;
import com.supplyboost.ordermanagement.dto.*;
import com.supplyboost.ordermanagement.event.OrderCreatedEvent;
import com.supplyboost.ordermanagement.event.OrderEventPublisher;
import com.supplyboost.ordermanagement.event.OrderItemEvent;
import com.supplyboost.ordermanagement.event.OrderStatusChangedEvent;
import com.supplyboost.ordermanagement.exception.CartNotFoundException;
import com.supplyboost.ordermanagement.exception.EmptyCartException;
import com.supplyboost.ordermanagement.exception.OrderNotFoundException;
import com.supplyboost.ordermanagement.mapper.OrderMapper;
import com.supplyboost.ordermanagement.model.Order;
import com.supplyboost.ordermanagement.model.OrderItem;
import com.supplyboost.ordermanagement.model.OrderStatus;
import com.supplyboost.ordermanagement.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final ShoppingCartClient shoppingCartClient;
  private final OrderMapper orderMapper;
  private final OrderEventPublisher eventPublisher;

  @Transactional
  public OrderResponse createOrder(CreateOrderRequest request) {
    // Fetch cart from shopping cart service
    CartDto cart = shoppingCartClient.getCart(request.getCartId());
    if (cart == null) {
      throw new CartNotFoundException("Cart not found: " + request.getCartId());
    }

    if (cart.getItems() == null || cart.getItems().isEmpty()) {
      throw new EmptyCartException("Cannot create order from empty cart");
    }

    // Create order
    Order order =
        Order.builder()
            .orderNumber(generateOrderNumber())
            .userId(request.getUserId())
            .cartId(request.getCartId())
            .status(OrderStatus.CREATED)
            .customerEmail(request.getCustomerEmail())
            .customerPhone(request.getCustomerPhone())
            .customerName(request.getCustomerName())
            .shippingAddressLine1(request.getShippingAddress().getLine1())
            .shippingAddressLine2(request.getShippingAddress().getLine2())
            .shippingCity(request.getShippingAddress().getCity())
            .shippingState(request.getShippingAddress().getState())
            .shippingPostalCode(request.getShippingAddress().getPostalCode())
            .shippingCountry(request.getShippingAddress().getCountry())
            .billingAddressLine1(request.getBillingAddress().getLine1())
            .billingAddressLine2(request.getBillingAddress().getLine2())
            .billingCity(request.getBillingAddress().getCity())
            .billingState(request.getBillingAddress().getState())
            .billingPostalCode(request.getBillingAddress().getPostalCode())
            .billingCountry(request.getBillingAddress().getCountry())
            .notes(request.getNotes())
            .build();

    // Convert cart items to order items
    for (CartItemDto cartItem : cart.getItems()) {
      OrderItem orderItem =
          OrderItem.builder()
              .productId(cartItem.getProductId())
              .productName(cartItem.getProductName())
              .productSku(cartItem.getProductSku())
              .unitPrice(cartItem.getUnitPrice())
              .quantity(cartItem.getQuantity())
              .subtotal(cartItem.getSubtotal())
              .imageUrl(cartItem.getImageUrl())
              .build();
      order.addItem(orderItem);
    }

    // Calculate total
    order.calculateTotal();

    // Save order
    Order savedOrder = orderRepository.save(order);
    log.info(
        "Order created successfully: {} for user: {}",
        savedOrder.getOrderNumber(),
        savedOrder.getUserId());

    // Publish order created event
    publishOrderCreatedEvent(savedOrder);

    // Clear cart after order creation
    shoppingCartClient.clearCart(request.getCartId());

    return orderMapper.toOrderResponse(savedOrder);
  }

  @Transactional(readOnly = true)
  public OrderResponse getOrder(Long orderId) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new OrderNotFoundException("Order not found: " + orderId));
    return orderMapper.toOrderResponse(order);
  }

  @Transactional(readOnly = true)
  public OrderResponse getOrderByNumber(String orderNumber) {
    Order order =
        orderRepository
            .findByOrderNumber(orderNumber)
            .orElseThrow(
                () ->
                    new OrderNotFoundException("Order not found: " + orderNumber));
    return orderMapper.toOrderResponse(order);
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> getUserOrders(Long userId) {
    List<Order> orders = orderRepository.findByUserId(userId);
    return orderMapper.toOrderResponses(orders);
  }

  @Transactional
  public OrderResponse updateOrderStatus(
      Long orderId, OrderStatus newStatus, String reason) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new OrderNotFoundException("Order not found: " + orderId));

    OrderStatus oldStatus = order.getStatus();
    order.setStatus(newStatus);
    Order updatedOrder = orderRepository.save(order);

    log.info(
        "Order {} status updated from {} to {}",
        order.getOrderNumber(),
        oldStatus,
        newStatus);

    // Publish status changed event
    publishOrderStatusChangedEvent(order, oldStatus, newStatus, reason);

    return orderMapper.toOrderResponse(updatedOrder);
  }

  @Transactional
  public void updatePaymentInfo(
      Long orderId, String paymentId, String paymentStatus, String paymentMethod) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new OrderNotFoundException("Order not found: " + orderId));

    order.setPaymentId(paymentId);
    order.setPaymentStatus(paymentStatus);
    order.setPaymentMethod(paymentMethod);
    orderRepository.save(order);

    log.info(
        "Order {} payment info updated: paymentId={}, status={}",
        order.getOrderNumber(),
        paymentId,
        paymentStatus);
  }

  @Transactional
  public void updateShipmentInfo(
      Long orderId, String shipmentId, String trackingNumber) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new OrderNotFoundException("Order not found: " + orderId));

    order.setShipmentId(shipmentId);
    order.setTrackingNumber(trackingNumber);
    order.setShippedAt(LocalDateTime.now());
    order.setStatus(OrderStatus.SHIPPED);
    orderRepository.save(order);

    log.info(
        "Order {} shipment info updated: shipmentId={}, trackingNumber={}",
        order.getOrderNumber(),
        shipmentId,
        trackingNumber);
  }

  @Transactional
  public void cancelOrder(Long orderId, String reason) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new OrderNotFoundException("Order not found: " + orderId));

    OrderStatus oldStatus = order.getStatus();
    order.setStatus(OrderStatus.CANCELLED);
    orderRepository.save(order);

    log.info("Order {} cancelled. Reason: {}", order.getOrderNumber(), reason);

    // Publish status changed event
    publishOrderStatusChangedEvent(order, oldStatus, OrderStatus.CANCELLED, reason);
  }

  private String generateOrderNumber() {
    return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  private void publishOrderCreatedEvent(Order order) {
    List<OrderItemEvent> itemEvents =
        order.getItems().stream()
            .map(
                item ->
                    OrderItemEvent.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
            .collect(Collectors.toList());

    OrderCreatedEvent event =
        OrderCreatedEvent.builder()
            .orderId(order.getId())
            .orderNumber(order.getOrderNumber())
            .userId(order.getUserId())
            .items(itemEvents)
            .totalAmount(order.getTotalAmount())
            .customerEmail(order.getCustomerEmail())
            .customerName(order.getCustomerName())
            .createdAt(order.getCreatedAt())
            .build();

    eventPublisher.publishOrderCreated(event);
  }

  private void publishOrderStatusChangedEvent(
      Order order, OrderStatus oldStatus, OrderStatus newStatus, String reason) {
    OrderStatusChangedEvent event =
        OrderStatusChangedEvent.builder()
            .orderId(order.getId())
            .orderNumber(order.getOrderNumber())
            .oldStatus(oldStatus)
            .newStatus(newStatus)
            .reason(reason)
            .changedAt(LocalDateTime.now())
            .build();

    eventPublisher.publishOrderStatusChanged(event);
  }
}
