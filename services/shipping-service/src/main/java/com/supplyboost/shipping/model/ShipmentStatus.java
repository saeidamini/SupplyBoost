package com.supplyboost.shipping.model;

public enum ShipmentStatus {
  PENDING,        // Shipment created, awaiting processing
  PROCESSING,     // Being prepared for shipping
  SHIPPED,        // Shipped and in transit
  IN_TRANSIT,     // In transit to destination
  OUT_FOR_DELIVERY, // Out for delivery
  DELIVERED,      // Delivered successfully
  FAILED,         // Delivery failed
  RETURNED        // Returned to sender
}
