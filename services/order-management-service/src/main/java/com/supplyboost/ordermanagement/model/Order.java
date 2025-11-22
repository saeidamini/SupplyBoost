package com.supplyboost.ordermanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_number", unique = true, nullable = false)
  private String orderNumber;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "cart_id")
  private String cartId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private OrderStatus status;

  @OneToMany(
      mappedBy = "order",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();

  @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "currency", nullable = false)
  @Builder.Default
  private String currency = "USD";

  // Shipping Information
  @Column(name = "shipping_address_line1")
  private String shippingAddressLine1;

  @Column(name = "shipping_address_line2")
  private String shippingAddressLine2;

  @Column(name = "shipping_city")
  private String shippingCity;

  @Column(name = "shipping_state")
  private String shippingState;

  @Column(name = "shipping_postal_code")
  private String shippingPostalCode;

  @Column(name = "shipping_country")
  private String shippingCountry;

  // Billing Information
  @Column(name = "billing_address_line1")
  private String billingAddressLine1;

  @Column(name = "billing_address_line2")
  private String billingAddressLine2;

  @Column(name = "billing_city")
  private String billingCity;

  @Column(name = "billing_state")
  private String billingState;

  @Column(name = "billing_postal_code")
  private String billingPostalCode;

  @Column(name = "billing_country")
  private String billingCountry;

  // Contact Information
  @Column(name = "customer_email", nullable = false)
  private String customerEmail;

  @Column(name = "customer_phone")
  private String customerPhone;

  @Column(name = "customer_name")
  private String customerName;

  // Payment Information
  @Column(name = "payment_id")
  private String paymentId;

  @Column(name = "payment_status")
  private String paymentStatus;

  @Column(name = "payment_method")
  private String paymentMethod;

  // Shipping Information
  @Column(name = "shipment_id")
  private String shipmentId;

  @Column(name = "tracking_number")
  private String trackingNumber;

  @Column(name = "shipped_at")
  private LocalDateTime shippedAt;

  @Column(name = "delivered_at")
  private LocalDateTime deliveredAt;

  // Audit Fields
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "notes")
  private String notes;

  // Helper methods
  public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
  }

  public void removeItem(OrderItem item) {
    items.remove(item);
    item.setOrder(null);
  }

  public void calculateTotal() {
    this.totalAmount =
        items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public Integer getTotalItems() {
    return items.stream().mapToInt(OrderItem::getQuantity).sum();
  }
}
