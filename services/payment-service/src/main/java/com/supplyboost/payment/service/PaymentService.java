package com.supplyboost.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.supplyboost.payment.dto.CreatePaymentRequest;
import com.supplyboost.payment.dto.PaymentResponse;
import com.supplyboost.payment.dto.RefundRequest;
import com.supplyboost.payment.event.PaymentEvent;
import com.supplyboost.payment.event.PaymentEventPublisher;
import com.supplyboost.payment.exception.PaymentNotFoundException;
import com.supplyboost.payment.exception.PaymentProcessingException;
import com.supplyboost.payment.mapper.PaymentMapper;
import com.supplyboost.payment.model.Payment;
import com.supplyboost.payment.model.PaymentStatus;
import com.supplyboost.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;
  private final PaymentEventPublisher eventPublisher;

  @Value("${payment.mode:mock}")
  private String paymentMode;

  @Value("${stripe.api.key:}")
  private String stripeApiKey;

  @Transactional
  public PaymentResponse createPayment(CreatePaymentRequest request) {
    // Create payment entity
    Payment payment =
        Payment.builder()
            .paymentId(generatePaymentId())
            .orderId(request.getOrderId())
            .orderNumber(request.getOrderNumber())
            .userId(request.getUserId())
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .status(PaymentStatus.PENDING)
            .paymentMethod(request.getPaymentMethod())
            .customerEmail(request.getCustomerEmail())
            .customerName(request.getCustomerName())
            .description(request.getDescription())
            .build();

    if ("stripe".equalsIgnoreCase(paymentMode)) {
      // Create Stripe payment intent
      try {
        PaymentIntent paymentIntent = createStripePaymentIntent(payment);
        payment.setStripePaymentIntentId(paymentIntent.getId());
        payment.setStripeClientSecret(paymentIntent.getClientSecret());
      } catch (StripeException e) {
        log.error("Failed to create Stripe payment intent", e);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason("Stripe error: " + e.getMessage());
      }
    } else {
      // Mock mode - auto-approve for testing
      payment.setPaymentProvider("MOCK");
    }

    Payment savedPayment = paymentRepository.save(payment);
    log.info(
        "Payment created: {} for order: {} with amount: {}",
        savedPayment.getPaymentId(),
        savedPayment.getOrderNumber(),
        savedPayment.getAmount());

    return paymentMapper.toPaymentResponse(savedPayment);
  }

  @Transactional
  public PaymentResponse confirmPayment(String paymentId, String paymentMethod) {
    Payment payment =
        paymentRepository
            .findByPaymentId(paymentId)
            .orElseThrow(
                () -> new PaymentNotFoundException("Payment not found: " + paymentId));

    if ("stripe".equalsIgnoreCase(paymentMode)) {
      // Confirm Stripe payment
      try {
        PaymentIntent paymentIntent =
            PaymentIntent.retrieve(payment.getStripePaymentIntentId());
        if ("succeeded".equals(paymentIntent.getStatus())) {
          payment.setStatus(PaymentStatus.SUCCEEDED);
          payment.setPaymentMethod(paymentMethod);
          payment.setProcessedAt(LocalDateTime.now());
        } else {
          payment.setStatus(PaymentStatus.PROCESSING);
        }
      } catch (StripeException e) {
        log.error("Failed to confirm Stripe payment", e);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason("Stripe confirmation error: " + e.getMessage());
      }
    } else {
      // Mock mode - auto-approve
      payment.setStatus(PaymentStatus.SUCCEEDED);
      payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "mock_card");
      payment.setProcessedAt(LocalDateTime.now());
    }

    Payment updatedPayment = paymentRepository.save(payment);
    log.info(
        "Payment confirmed: {} with status: {}",
        updatedPayment.getPaymentId(),
        updatedPayment.getStatus());

    // Publish payment event
    publishPaymentEvent(updatedPayment);

    return paymentMapper.toPaymentResponse(updatedPayment);
  }

  @Transactional(readOnly = true)
  public PaymentResponse getPayment(String paymentId) {
    Payment payment =
        paymentRepository
            .findByPaymentId(paymentId)
            .orElseThrow(
                () -> new PaymentNotFoundException("Payment not found: " + paymentId));
    return paymentMapper.toPaymentResponse(payment);
  }

  @Transactional(readOnly = true)
  public PaymentResponse getPaymentByOrderId(Long orderId) {
    Payment payment =
        paymentRepository
            .findByOrderId(orderId)
            .orElseThrow(
                () ->
                    new PaymentNotFoundException(
                        "Payment not found for order: " + orderId));
    return paymentMapper.toPaymentResponse(payment);
  }

  @Transactional(readOnly = true)
  public List<PaymentResponse> getUserPayments(Long userId) {
    List<Payment> payments = paymentRepository.findByUserId(userId);
    return paymentMapper.toPaymentResponses(payments);
  }

  @Transactional
  public PaymentResponse refundPayment(String paymentId, RefundRequest request) {
    Payment payment =
        paymentRepository
            .findByPaymentId(paymentId)
            .orElseThrow(
                () -> new PaymentNotFoundException("Payment not found: " + paymentId));

    if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
      throw new PaymentProcessingException(
          "Can only refund succeeded payments. Current status: "
              + payment.getStatus());
    }

    BigDecimal refundAmount =
        request.getAmount() != null ? request.getAmount() : payment.getAmount();

    if ("stripe".equalsIgnoreCase(paymentMode)) {
      // Process Stripe refund
      try {
        Refund refund = createStripeRefund(payment, refundAmount);
        payment.setRefundId(refund.getId());
        payment.setRefundedAmount(refundAmount);
        payment.setStatus(PaymentStatus.REFUNDED);
      } catch (StripeException e) {
        log.error("Failed to process Stripe refund", e);
        throw new PaymentProcessingException("Refund failed: " + e.getMessage());
      }
    } else {
      // Mock mode - auto-approve refund
      payment.setRefundId("MOCK-REFUND-" + UUID.randomUUID().toString());
      payment.setRefundedAmount(refundAmount);
      payment.setStatus(PaymentStatus.REFUNDED);
    }

    Payment updatedPayment = paymentRepository.save(payment);
    log.info("Payment refunded: {} with amount: {}", paymentId, refundAmount);

    // Publish refund event
    publishPaymentEvent(updatedPayment);

    return paymentMapper.toPaymentResponse(updatedPayment);
  }

  private PaymentIntent createStripePaymentIntent(Payment payment)
      throws StripeException {
    PaymentIntentCreateParams params =
        PaymentIntentCreateParams.builder()
            .setAmount(payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
            .setCurrency(payment.getCurrency().toLowerCase())
            .setDescription(payment.getDescription())
            .putMetadata("orderId", payment.getOrderId().toString())
            .putMetadata("orderNumber", payment.getOrderNumber())
            .build();

    return PaymentIntent.create(params);
  }

  private Refund createStripeRefund(Payment payment, BigDecimal amount)
      throws StripeException {
    RefundCreateParams params =
        RefundCreateParams.builder()
            .setPaymentIntent(payment.getStripePaymentIntentId())
            .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
            .build();

    return Refund.create(params);
  }

  private void publishPaymentEvent(Payment payment) {
    PaymentEvent event =
        PaymentEvent.builder()
            .paymentId(payment.getId())
            .paymentNumber(payment.getPaymentId())
            .orderId(payment.getOrderId())
            .orderNumber(payment.getOrderNumber())
            .userId(payment.getUserId())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .status(payment.getStatus())
            .paymentMethod(payment.getPaymentMethod())
            .failureReason(payment.getFailureReason())
            .eventTime(LocalDateTime.now())
            .build();

    eventPublisher.publishPaymentProcessed(event);
  }

  private String generatePaymentId() {
    return "PAY-"
        + System.currentTimeMillis()
        + "-"
        + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }
}
