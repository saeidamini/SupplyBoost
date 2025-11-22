package com.supplyboost.payment.model;

public enum PaymentStatus {
  PENDING, // Payment initiated
  PROCESSING, // Payment being processed
  SUCCEEDED, // Payment successful
  FAILED, // Payment failed
  CANCELLED, // Payment cancelled
  REFUNDED // Payment refunded
}
