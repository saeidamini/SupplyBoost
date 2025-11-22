package com.supplyboost.ordermanagement.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

  private Long id;
  private Long productId;
  private String productName;
  private String productSku;
  private BigDecimal unitPrice;
  private Integer quantity;
  private BigDecimal subtotal;
  private String imageUrl;
}
