package com.supplyboost.ordermanagement.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

  private String cartId;
  private Long userId;
  private List<CartItemDto> items;
  private BigDecimal totalAmount;
  private Integer totalItems;
}
