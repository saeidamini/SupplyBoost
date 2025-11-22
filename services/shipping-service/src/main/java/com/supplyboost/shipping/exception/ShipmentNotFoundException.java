package com.supplyboost.shipping.exception;

public class ShipmentNotFoundException extends RuntimeException {
  public ShipmentNotFoundException(String message) {
    super(message);
  }
}
