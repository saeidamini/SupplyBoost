package com.supplyboost.ordermanagement.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreatedEvent {

  private Long shipmentId;
  private String shipmentNumber;
  private String trackingNumber;
  private Long orderId;
  private String orderNumber;
  private Long userId;
  private String status;
  private String carrier;
  private LocalDateTime estimatedDelivery;
  private LocalDateTime eventTime;
}
