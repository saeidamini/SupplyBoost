package com.supplyboost.ordermanagement.model;

public enum OrderStatus {
  CREATED, // Order created but not yet paid
  PAYMENT_PENDING, // Payment processing
  PAYMENT_CONFIRMED, // Payment successful
  PAYMENT_FAILED, // Payment failed
  INVENTORY_RESERVED, // Inventory reserved
  INVENTORY_RESERVATION_FAILED, // Inventory reservation failed
  READY_TO_SHIP, // Ready for shipment
  SHIPPED, // Order shipped
  DELIVERED, // Order delivered
  CANCELLED, // Order cancelled
  REFUNDED // Order refunded
}
