package com.supplyboost.shoppingcart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("shopping_cart")
public class ShoppingCart implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id private String id; // userId or sessionId

  private Long userId;

  @Builder.Default private List<CartItem> items = new ArrayList<>();

  private BigDecimal totalAmount;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @TimeToLive
  @JsonIgnore
  private Long ttl = 7 * 24 * 60 * 60L; // 7 days in seconds

  public void addItem(CartItem newItem) {
    boolean found = false;
    for (CartItem item : items) {
      if (item.getProductId().equals(newItem.getProductId())) {
        item.setQuantity(item.getQuantity() + newItem.getQuantity());
        item.calculateSubtotal();
        found = true;
        break;
      }
    }
    if (!found) {
      newItem.calculateSubtotal();
      items.add(newItem);
    }
    calculateTotal();
    this.updatedAt = LocalDateTime.now();
  }

  public void updateItemQuantity(Long productId, Integer quantity) {
    for (CartItem item : items) {
      if (item.getProductId().equals(productId)) {
        if (quantity <= 0) {
          items.remove(item);
        } else {
          item.setQuantity(quantity);
          item.calculateSubtotal();
        }
        break;
      }
    }
    calculateTotal();
    this.updatedAt = LocalDateTime.now();
  }

  public void removeItem(Long productId) {
    items.removeIf(item -> item.getProductId().equals(productId));
    calculateTotal();
    this.updatedAt = LocalDateTime.now();
  }

  public void clear() {
    items.clear();
    totalAmount = BigDecimal.ZERO;
    this.updatedAt = LocalDateTime.now();
  }

  public void calculateTotal() {
    this.totalAmount =
        items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public Integer getTotalItems() {
    return items.stream().mapToInt(CartItem::getQuantity).sum();
  }
}
