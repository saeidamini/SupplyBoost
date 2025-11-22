package com.supplyboost.payment.dto;

import com.supplyboost.payment.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

  private Long id;
  private String paymentId;
  private Long orderId;
  private String orderNumber;
  private Long userId;
  private BigDecimal amount;
  private String currency;
  private PaymentStatus status;
  private String paymentMethod;
  private String paymentProvider;
  private String stripeClientSecret;
  private String customerEmail;
  private String customerName;
  private String description;
  private String failureReason;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime processedAt;
}
