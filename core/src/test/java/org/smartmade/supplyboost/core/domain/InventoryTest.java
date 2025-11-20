package org.smartmade.supplyboost.core.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Domain Tests")
class InventoryTest {

    @Test
    @DisplayName("Should create inventory with valid parameters")
    void shouldCreateInventoryWithValidParameters() {
        // Given
        String productId = "P001";
        int quantity = 100;
        String location = "Warehouse A";

        // When
        Inventory inventory = new Inventory(productId, quantity, location);

        // Then
        assertNotNull(inventory);
        assertEquals(productId, inventory.getProductId());
        assertEquals(quantity, inventory.getQuantity());
        assertEquals(location, inventory.getLocation());
        assertNotNull(inventory.getLastUpdated());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when product ID is null or empty")
    void shouldThrowExceptionWhenProductIdIsNullOrEmpty(String invalidProductId) {
        // Given
        int quantity = 100;
        String location = "Warehouse A";

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Inventory(invalidProductId, quantity, location)
        );
    }

    @Test
    @DisplayName("Should throw exception when quantity is negative")
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // Given
        String productId = "P001";
        int negativeQuantity = -10;
        String location = "Warehouse A";

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Inventory(productId, negativeQuantity, location)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when location is null or empty")
    void shouldThrowExceptionWhenLocationIsNullOrEmpty(String invalidLocation) {
        // Given
        String productId = "P001";
        int quantity = 100;

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Inventory(productId, quantity, invalidLocation)
        );
    }

    @Test
    @DisplayName("Should add stock successfully")
    void shouldAddStockSuccessfully() {
        // Given
        Inventory inventory = new Inventory("P001", 100, "Warehouse A");
        int amountToAdd = 50;

        // When
        inventory.addStock(amountToAdd);

        // Then
        assertEquals(150, inventory.getQuantity());
    }

    @Test
    @DisplayName("Should throw exception when adding negative stock")
    void shouldThrowExceptionWhenAddingNegativeStock() {
        // Given
        Inventory inventory = new Inventory("P001", 100, "Warehouse A");

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            inventory.addStock(-10)
        );
    }

    @Test
    @DisplayName("Should remove stock successfully")
    void shouldRemoveStockSuccessfully() {
        // Given
        Inventory inventory = new Inventory("P001", 100, "Warehouse A");
        int amountToRemove = 30;

        // When
        inventory.removeStock(amountToRemove);

        // Then
        assertEquals(70, inventory.getQuantity());
    }

    @Test
    @DisplayName("Should throw exception when removing more stock than available")
    void shouldThrowExceptionWhenRemovingMoreStockThanAvailable() {
        // Given
        Inventory inventory = new Inventory("P001", 100, "Warehouse A");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            inventory.removeStock(150)
        );
        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    @DisplayName("Should throw exception when removing negative stock")
    void shouldThrowExceptionWhenRemovingNegativeStock() {
        // Given
        Inventory inventory = new Inventory("P001", 100, "Warehouse A");

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            inventory.removeStock(-10)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 20, 50})
    @DisplayName("Should correctly identify low stock")
    void shouldCorrectlyIdentifyLowStock(int threshold) {
        // Given
        Inventory inventory = new Inventory("P001", 10, "Warehouse A");

        // When
        boolean isLow = inventory.isLowStock(threshold);

        // Then
        if (threshold > 10) {
            assertTrue(isLow, "Stock should be low when quantity (10) is below threshold (" + threshold + ")");
        } else {
            assertFalse(isLow, "Stock should not be low when quantity (10) is >= threshold (" + threshold + ")");
        }
    }

    @Test
    @DisplayName("Should not be low stock when quantity equals threshold")
    void shouldNotBeLowStockWhenQuantityEqualsThreshold() {
        // Given
        Inventory inventory = new Inventory("P001", 20, "Warehouse A");

        // When
        boolean isLow = inventory.isLowStock(20);

        // Then
        assertFalse(isLow);
    }

    @Test
    @DisplayName("Should update last updated timestamp when adding stock")
    void shouldUpdateLastUpdatedTimestampWhenAddingStock() throws InterruptedException {
        // Given
        Inventory inventory = new Inventory("P001", 100, "Warehouse A");
        var initialTimestamp = inventory.getLastUpdated();
        Thread.sleep(10); // Small delay to ensure timestamp difference

        // When
        inventory.addStock(10);

        // Then
        assertTrue(inventory.getLastUpdated().isAfter(initialTimestamp));
    }

    @Test
    @DisplayName("Should update last updated timestamp when removing stock")
    void shouldUpdateLastUpdatedTimestampWhenRemovingStock() throws InterruptedException {
        // Given
        Inventory inventory = new Inventory("P001", 100, "Warehouse A");
        var initialTimestamp = inventory.getLastUpdated();
        Thread.sleep(10); // Small delay to ensure timestamp difference

        // When
        inventory.removeStock(10);

        // Then
        assertTrue(inventory.getLastUpdated().isAfter(initialTimestamp));
    }

    @Test
    @DisplayName("Should return valid string representation")
    void shouldReturnValidStringRepresentation() {
        // Given
        Inventory inventory = new Inventory("P001", 100, "Warehouse A");

        // When
        String result = inventory.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("P001"));
        assertTrue(result.contains("100"));
        assertTrue(result.contains("Warehouse A"));
    }
}
