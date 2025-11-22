package com.supplyboost.shoppingcart.dto;

import com.supplyboost.shoppingcart.model.CartItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

  private String cartId;
  private Long userId;
  private List<CartItem> items;
  private BigDecimal totalAmount;
  private Integer totalItems;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
