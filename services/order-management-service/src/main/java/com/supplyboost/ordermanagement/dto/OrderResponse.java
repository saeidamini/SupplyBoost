package com.supplyboost.ordermanagement.dto;

import com.supplyboost.ordermanagement.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

  private Long id;
  private String orderNumber;
  private Long userId;
  private OrderStatus status;
  private List<OrderItemDto> items;
  private BigDecimal totalAmount;
  private String currency;
  private AddressDto shippingAddress;
  private AddressDto billingAddress;
  private String customerEmail;
  private String customerPhone;
  private String customerName;
  private String paymentId;
  private String paymentStatus;
  private String paymentMethod;
  private String shipmentId;
  private String trackingNumber;
  private LocalDateTime shippedAt;
  private LocalDateTime deliveredAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String notes;
  private Integer totalItems;
}
