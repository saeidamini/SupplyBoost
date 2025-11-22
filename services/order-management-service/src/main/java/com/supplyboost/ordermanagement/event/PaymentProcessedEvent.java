package com.supplyboost.ordermanagement.event;

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
  private Long userId;
  private BigDecimal amount;
  private String currency;
  private String status; // SUCCEEDED, FAILED
  private String paymentMethod;
  private String failureReason;
  private LocalDateTime eventTime;
}
