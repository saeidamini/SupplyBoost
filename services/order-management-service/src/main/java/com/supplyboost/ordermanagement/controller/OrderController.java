package com.supplyboost.ordermanagement.controller;

import com.supplyboost.ordermanagement.dto.CreateOrderRequest;
import com.supplyboost.ordermanagement.dto.OrderResponse;
import com.supplyboost.ordermanagement.model.OrderStatus;
import com.supplyboost.ordermanagement.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Order management APIs")
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  @Operation(summary = "Create order", description = "Create a new order from cart")
  public ResponseEntity<OrderResponse> createOrder(
      @Valid @RequestBody CreateOrderRequest request) {
    log.info("Creating order for user: {} from cart: {}", request.getUserId(), request.getCartId());
    OrderResponse order = orderService.createOrder(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(order);
  }

  @GetMapping("/{orderId}")
  @Operation(summary = "Get order", description = "Retrieve order by ID")
  public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
    log.info("Getting order: {}", orderId);
    OrderResponse order = orderService.getOrder(orderId);
    return ResponseEntity.ok(order);
  }

  @GetMapping("/number/{orderNumber}")
  @Operation(summary = "Get order by number", description = "Retrieve order by order number")
  public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
    log.info("Getting order by number: {}", orderNumber);
    OrderResponse order = orderService.getOrderByNumber(orderNumber);
    return ResponseEntity.ok(order);
  }

  @GetMapping("/user/{userId}")
  @Operation(summary = "Get user orders", description = "Retrieve all orders for a user")
  public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
    log.info("Getting orders for user: {}", userId);
    List<OrderResponse> orders = orderService.getUserOrders(userId);
    return ResponseEntity.ok(orders);
  }

  @PutMapping("/{orderId}/status")
  @Operation(summary = "Update order status", description = "Update the status of an order")
  public ResponseEntity<OrderResponse> updateOrderStatus(
      @PathVariable Long orderId,
      @RequestParam OrderStatus status,
      @RequestParam(required = false) String reason) {
    log.info("Updating order {} status to {}", orderId, status);
    OrderResponse order = orderService.updateOrderStatus(orderId, status, reason);
    return ResponseEntity.ok(order);
  }

  @PutMapping("/{orderId}/payment")
  @Operation(summary = "Update payment info", description = "Update order payment information")
  public ResponseEntity<Void> updatePaymentInfo(
      @PathVariable Long orderId,
      @RequestParam String paymentId,
      @RequestParam String paymentStatus,
      @RequestParam String paymentMethod) {
    log.info("Updating payment info for order {}", orderId);
    orderService.updatePaymentInfo(orderId, paymentId, paymentStatus, paymentMethod);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{orderId}/shipment")
  @Operation(summary = "Update shipment info", description = "Update order shipment information")
  public ResponseEntity<Void> updateShipmentInfo(
      @PathVariable Long orderId,
      @RequestParam String shipmentId,
      @RequestParam String trackingNumber) {
    log.info("Updating shipment info for order {}", orderId);
    orderService.updateShipmentInfo(orderId, shipmentId, trackingNumber);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{orderId}")
  @Operation(summary = "Cancel order", description = "Cancel an order")
  public ResponseEntity<Void> cancelOrder(
      @PathVariable Long orderId, @RequestParam(required = false) String reason) {
    log.info("Cancelling order {}", orderId);
    orderService.cancelOrder(orderId, reason);
    return ResponseEntity.noContent().build();
  }
}
