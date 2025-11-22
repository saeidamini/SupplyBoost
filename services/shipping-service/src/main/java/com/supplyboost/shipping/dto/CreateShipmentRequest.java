package com.supplyboost.shipping.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipmentRequest {

  @NotNull(message = "Order ID is required")
  private Long orderId;

  @NotBlank(message = "Order number is required")
  private String orderNumber;

  @NotNull(message = "User ID is required")
  private Long userId;

  @NotBlank(message = "Recipient name is required")
  private String recipientName;

  @Email(message = "Invalid email format")
  private String recipientEmail;

  private String recipientPhone;

  @NotBlank(message = "Address line 1 is required")
  private String addressLine1;

  private String addressLine2;

  @NotBlank(message = "City is required")
  private String city;

  @NotBlank(message = "State is required")
  private String state;

  @NotBlank(message = "Postal code is required")
  private String postalCode;

  @NotBlank(message = "Country is required")
  private String country;

  private String carrier;
  private String serviceType;
  private String notes;
}
