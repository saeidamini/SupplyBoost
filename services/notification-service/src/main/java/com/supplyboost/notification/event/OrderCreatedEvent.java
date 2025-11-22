package com.supplyboost.notification.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
  private String customerName;
  private String customerEmail;
  private String customerPhone;
  private BigDecimal totalAmount;
  private String status;
  private LocalDateTime eventTime;
}
