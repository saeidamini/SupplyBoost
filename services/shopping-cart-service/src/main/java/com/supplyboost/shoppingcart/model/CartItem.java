package com.supplyboost.shoppingcart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long productId;
  private String productName;
  private String productSku;
  private BigDecimal unitPrice;
  private Integer quantity;
  private BigDecimal subtotal;
  private String imageUrl;

  public void calculateSubtotal() {
    if (unitPrice != null && quantity != null) {
      this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
  }
}
