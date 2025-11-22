package com.supplyboost.ordermanagement.event;

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
public class OrderCreatedEvent {

  private Long orderId;
  private String orderNumber;
  private Long userId;
  private List<OrderItemEvent> items;
  private BigDecimal totalAmount;
  private String customerEmail;
  private String customerName;
  private LocalDateTime createdAt;
}
