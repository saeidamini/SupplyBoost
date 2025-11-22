package com.supplyboost.payment.controller;

import com.supplyboost.payment.dto.ConfirmPaymentRequest;
import com.supplyboost.payment.dto.CreatePaymentRequest;
import com.supplyboost.payment.dto.PaymentResponse;
import com.supplyboost.payment.dto.RefundRequest;
import com.supplyboost.payment.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment processing APIs")
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  @Operation(summary = "Create payment", description = "Create a new payment for an order")
  public ResponseEntity<PaymentResponse> createPayment(
      @Valid @RequestBody CreatePaymentRequest request) {
    log.info(
        "Creating payment for order: {} with amount: {}",
        request.getOrderNumber(),
        request.getAmount());
    PaymentResponse payment = paymentService.createPayment(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(payment);
  }

  @PostMapping("/{paymentId}/confirm")
  @Operation(summary = "Confirm payment", description = "Confirm payment processing")
  public ResponseEntity<PaymentResponse> confirmPayment(
      @PathVariable String paymentId,
      @Valid @RequestBody ConfirmPaymentRequest request) {
    log.info("Confirming payment: {}", paymentId);
    PaymentResponse payment =
        paymentService.confirmPayment(paymentId, request.getPaymentMethod());
    return ResponseEntity.ok(payment);
  }

  @GetMapping("/{paymentId}")
  @Operation(summary = "Get payment", description = "Retrieve payment by ID")
  public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
    log.info("Getting payment: {}", paymentId);
    PaymentResponse payment = paymentService.getPayment(paymentId);
    return ResponseEntity.ok(payment);
  }

  @GetMapping("/order/{orderId}")
  @Operation(
      summary = "Get payment by order",
      description = "Retrieve payment by order ID")
  public ResponseEntity<PaymentResponse> getPaymentByOrderId(
      @PathVariable Long orderId) {
    log.info("Getting payment for order: {}", orderId);
    PaymentResponse payment = paymentService.getPaymentByOrderId(orderId);
    return ResponseEntity.ok(payment);
  }

  @GetMapping("/user/{userId}")
  @Operation(
      summary = "Get user payments",
      description = "Retrieve all payments for a user")
  public ResponseEntity<List<PaymentResponse>> getUserPayments(
      @PathVariable Long userId) {
    log.info("Getting payments for user: {}", userId);
    List<PaymentResponse> payments = paymentService.getUserPayments(userId);
    return ResponseEntity.ok(payments);
  }

  @PostMapping("/{paymentId}/refund")
  @Operation(summary = "Refund payment", description = "Refund a payment")
  public ResponseEntity<PaymentResponse> refundPayment(
      @PathVariable String paymentId, @Valid @RequestBody RefundRequest request) {
    log.info("Refunding payment: {}", paymentId);
    PaymentResponse payment = paymentService.refundPayment(paymentId, request);
    return ResponseEntity.ok(payment);
  }
}
