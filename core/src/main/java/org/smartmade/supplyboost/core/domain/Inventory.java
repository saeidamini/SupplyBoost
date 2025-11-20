package org.smartmade.supplyboost.core.domain;

import java.time.LocalDateTime;

/**
 * Represents inventory for a product
 */
public class Inventory {
    private String productId;
    private int quantity;
    private String location;
    private LocalDateTime lastUpdated;

    public Inventory(String productId, int quantity, String location) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be null or empty");
        }

        this.productId = productId;
        this.quantity = quantity;
        this.location = location;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Adds quantity to inventory
     * @param amount amount to add
     * @throws IllegalArgumentException if amount is negative
     */
    public void addStock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add negative amount");
        }
        this.quantity += amount;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Removes quantity from inventory
     * @param amount amount to remove
     * @throws IllegalArgumentException if amount is negative or exceeds available quantity
     */
    public void removeStock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot remove negative amount");
        }
        if (amount > this.quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + this.quantity + ", Requested: " + amount);
        }
        this.quantity -= amount;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Checks if inventory is low (below threshold)
     * @param threshold the threshold to check against
     * @return true if quantity is below threshold
     */
    public boolean isLowStock(int threshold) {
        return this.quantity < threshold;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", location='" + location + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
