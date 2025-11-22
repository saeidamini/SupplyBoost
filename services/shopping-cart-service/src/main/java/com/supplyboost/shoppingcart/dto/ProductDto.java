package com.supplyboost.shoppingcart.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

  private Long id;
  private String sku;
  private String name;
  private BigDecimal price;
  private Integer stockQuantity;
  private String imageUrl;
  private Boolean active;
}
