package com.supplyboost.shipping.dto;

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
public class ShipmentResponse {

  private Long id;
  private String shipmentId;
  private String trackingNumber;
  private Long orderId;
  private String orderNumber;
  private Long userId;
  private ShipmentStatus status;
  private String carrier;
  private String serviceType;
  private LocalDateTime estimatedDelivery;
  private LocalDateTime actualDelivery;
  private String recipientName;
  private String recipientEmail;
  private String recipientPhone;
  private String addressLine1;
  private String addressLine2;
  private String city;
  private String state;
  private String postalCode;
  private String country;
  private String notes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime shippedAt;
}
