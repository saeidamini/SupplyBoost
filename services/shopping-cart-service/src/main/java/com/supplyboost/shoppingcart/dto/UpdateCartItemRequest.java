package com.supplyboost.shoppingcart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequest {

  @NotNull(message = "Quantity is required")
  @Min(value = 0, message = "Quantity must be at least 0")
  private Integer quantity;
}
