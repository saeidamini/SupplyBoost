package com.supplyboost.ordermanagement.event;

import com.supplyboost.ordermanagement.model.OrderStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusChangedEvent {

  private Long orderId;
  private String orderNumber;
  private OrderStatus oldStatus;
  private OrderStatus newStatus;
  private String reason;
  private LocalDateTime changedAt;
}
