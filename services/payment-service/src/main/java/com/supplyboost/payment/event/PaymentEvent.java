package com.supplyboost.payment.event;

import com.supplyboost.payment.model.PaymentStatus;
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
public class PaymentEvent {

  private Long paymentId;
  private String paymentNumber;
  private Long orderId;
  private String orderNumber;
  private Long userId;
  private BigDecimal amount;
  private String currency;
  private PaymentStatus status;
  private String paymentMethod;
  private String failureReason;
  private LocalDateTime eventTime;
}
