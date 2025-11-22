package com.supplyboost.shipping.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "shipments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "shipment_id", unique = true, nullable = false)
  private String shipmentId;

  @Column(name = "tracking_number", unique = true, nullable = false)
  private String trackingNumber;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(name = "order_number", nullable = false)
  private String orderNumber;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ShipmentStatus status;

  @Column(name = "carrier")
  @Builder.Default
  private String carrier = "STANDARD";

  @Column(name = "service_type")
  @Builder.Default
  private String serviceType = "GROUND";

  @Column(name = "estimated_delivery")
  private LocalDateTime estimatedDelivery;

  @Column(name = "actual_delivery")
  private LocalDateTime actualDelivery;

  // Shipping Address
  @Column(name = "recipient_name", nullable = false)
  private String recipientName;

  @Column(name = "recipient_email")
  private String recipientEmail;

  @Column(name = "recipient_phone")
  private String recipientPhone;

  @Column(name = "address_line1", nullable = false)
  private String addressLine1;

  @Column(name = "address_line2")
  private String addressLine2;

  @Column(name = "city", nullable = false)
  private String city;

  @Column(name = "state", nullable = false)
  private String state;

  @Column(name = "postal_code", nullable = false)
  private String postalCode;

  @Column(name = "country", nullable = false)
  private String country;

  @Column(name = "notes")
  private String notes;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "shipped_at")
  private LocalDateTime shippedAt;
}
