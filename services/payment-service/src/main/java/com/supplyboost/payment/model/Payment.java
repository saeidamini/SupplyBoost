package com.supplyboost.payment.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "payment_id", unique = true, nullable = false)
  private String paymentId;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(name = "order_number", nullable = false)
  private String orderNumber;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Column(name = "currency", nullable = false)
  @Builder.Default
  private String currency = "USD";

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private PaymentStatus status;

  @Column(name = "payment_method")
  private String paymentMethod;

  @Column(name = "payment_provider")
  @Builder.Default
  private String paymentProvider = "STRIPE";

  // Stripe-specific fields
  @Column(name = "stripe_payment_intent_id")
  private String stripePaymentIntentId;

  @Column(name = "stripe_client_secret")
  private String stripeClientSecret;

  @Column(name = "stripe_charge_id")
  private String stripeChargeId;

  // Customer information
  @Column(name = "customer_email", nullable = false)
  private String customerEmail;

  @Column(name = "customer_name")
  private String customerName;

  // Metadata
  @Column(name = "description")
  private String description;

  @Column(name = "failure_reason")
  private String failureReason;

  @Column(name = "refund_id")
  private String refundId;

  @Column(name = "refunded_amount", precision = 10, scale = 2)
  private BigDecimal refundedAmount;

  // Audit Fields
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "processed_at")
  private LocalDateTime processedAt;
}
