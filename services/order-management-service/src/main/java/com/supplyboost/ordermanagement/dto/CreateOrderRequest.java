package com.supplyboost.ordermanagement.dto;

import jakarta.validation.Valid;
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
public class CreateOrderRequest {

  @NotNull(message = "User ID is required")
  private Long userId;

  @NotBlank(message = "Cart ID is required")
  private String cartId;

  @NotNull(message = "Shipping address is required")
  @Valid
  private AddressDto shippingAddress;

  @NotNull(message = "Billing address is required")
  @Valid
  private AddressDto billingAddress;

  @NotBlank(message = "Customer email is required")
  @Email(message = "Invalid email format")
  private String customerEmail;

  private String customerPhone;

  @NotBlank(message = "Customer name is required")
  private String customerName;

  private String notes;
}
