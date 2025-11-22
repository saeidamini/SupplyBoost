package com.supplyboost.payment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

  @NotNull(message = "Order ID is required")
  private Long orderId;

  @NotBlank(message = "Order number is required")
  private String orderNumber;

  @NotNull(message = "User ID is required")
  private Long userId;

  @NotNull(message = "Amount is required")
  @Positive(message = "Amount must be positive")
  private BigDecimal amount;

  @Builder.Default private String currency = "USD";

  @NotBlank(message = "Customer email is required")
  @Email(message = "Invalid email format")
  private String customerEmail;

  private String customerName;

  private String description;

  private String paymentMethod;
}
