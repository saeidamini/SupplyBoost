package com.supplyboost.shipping.event;

import com.supplyboost.shipping.model.ShipmentStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentEvent {

  private Long shipmentId;
  private String shipmentNumber;
  private String trackingNumber;
  private Long orderId;
  private String orderNumber;
  private Long userId;
  private ShipmentStatus status;
  private String carrier;
  private LocalDateTime estimatedDelivery;
  private LocalDateTime eventTime;
}
