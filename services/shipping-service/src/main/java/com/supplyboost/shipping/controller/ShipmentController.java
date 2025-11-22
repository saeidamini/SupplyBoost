package com.supplyboost.shipping.controller;

import com.supplyboost.shipping.dto.CreateShipmentRequest;
import com.supplyboost.shipping.dto.ShipmentResponse;
import com.supplyboost.shipping.model.ShipmentStatus;
import com.supplyboost.shipping.service.ShipmentService;
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
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipping", description = "Shipping and logistics APIs")
public class ShipmentController {

  private final ShipmentService shipmentService;

  @PostMapping
  @Operation(summary = "Create shipment", description = "Create a new shipment for an order")
  public ResponseEntity<ShipmentResponse> createShipment(
      @Valid @RequestBody CreateShipmentRequest request) {
    log.info("Creating shipment for order: {}", request.getOrderNumber());
    ShipmentResponse shipment = shipmentService.createShipment(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
  }

  @GetMapping("/{shipmentId}")
  @Operation(summary = "Get shipment", description = "Retrieve shipment by ID")
  public ResponseEntity<ShipmentResponse> getShipment(@PathVariable String shipmentId) {
    log.info("Getting shipment: {}", shipmentId);
    ShipmentResponse shipment = shipmentService.getShipment(shipmentId);
    return ResponseEntity.ok(shipment);
  }

  @GetMapping("/tracking/{trackingNumber}")
  @Operation(
      summary = "Track shipment",
      description = "Track shipment by tracking number")
  public ResponseEntity<ShipmentResponse> getShipmentByTracking(
      @PathVariable String trackingNumber) {
    log.info("Tracking shipment: {}", trackingNumber);
    ShipmentResponse shipment = shipmentService.getShipmentByTracking(trackingNumber);
    return ResponseEntity.ok(shipment);
  }

  @GetMapping("/order/{orderId}")
  @Operation(
      summary = "Get shipment by order",
      description = "Retrieve shipment by order ID")
  public ResponseEntity<ShipmentResponse> getShipmentByOrderId(@PathVariable Long orderId) {
    log.info("Getting shipment for order: {}", orderId);
    ShipmentResponse shipment = shipmentService.getShipmentByOrderId(orderId);
    return ResponseEntity.ok(shipment);
  }

  @GetMapping("/user/{userId}")
  @Operation(
      summary = "Get user shipments",
      description = "Retrieve all shipments for a user")
  public ResponseEntity<List<ShipmentResponse>> getUserShipments(@PathVariable Long userId) {
    log.info("Getting shipments for user: {}", userId);
    List<ShipmentResponse> shipments = shipmentService.getUserShipments(userId);
    return ResponseEntity.ok(shipments);
  }

  @PutMapping("/{shipmentId}/status")
  @Operation(
      summary = "Update shipment status",
      description = "Update the status of a shipment")
  public ResponseEntity<ShipmentResponse> updateShipmentStatus(
      @PathVariable String shipmentId, @RequestParam ShipmentStatus status) {
    log.info("Updating shipment {} status to {}", shipmentId, status);
    ShipmentResponse shipment = shipmentService.updateShipmentStatus(shipmentId, status);
    return ResponseEntity.ok(shipment);
  }
}
