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
public class PaymentProcessedEvent {

  private Long paymentId;
  private String paymentNumber;
  private Long orderId;
  private String orderNumber;
  private BigDecimal amount;
  private String currency;
  private String paymentMethod;
  private String status;
  private String customerEmail;
  private String customerName;
  private String failureReason;
  private LocalDateTime eventTime;
}
