package org.smartmade.supplyboost.core.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Tests")
class ProductTest {

    @Test
    @DisplayName("Should create product with valid parameters")
    void shouldCreateProductWithValidParameters() {
        // Given
        String id = "P001";
        String name = "Laptop";
        String description = "High-performance laptop";
        BigDecimal price = new BigDecimal("999.99");
        String category = "Electronics";

        // When
        Product product = new Product(id, name, description, price, category);

        // Then
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when product ID is null or empty")
    void shouldThrowExceptionWhenProductIdIsNullOrEmpty(String invalidId) {
        // Given
        String name = "Laptop";
        String description = "High-performance laptop";
        BigDecimal price = new BigDecimal("999.99");
        String category = "Electronics";

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Product(invalidId, name, description, price, category)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when product name is null or empty")
    void shouldThrowExceptionWhenProductNameIsNullOrEmpty(String invalidName) {
        // Given
        String id = "P001";
        String description = "High-performance laptop";
        BigDecimal price = new BigDecimal("999.99");
        String category = "Electronics";

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Product(id, invalidName, description, price, category)
        );
    }

    @Test
    @DisplayName("Should throw exception when price is null")
    void shouldThrowExceptionWhenPriceIsNull() {
        // Given
        String id = "P001";
        String name = "Laptop";
        String description = "High-performance laptop";
        String category = "Electronics";

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Product(id, name, description, null, category)
        );
    }

    @Test
    @DisplayName("Should throw exception when price is negative")
    void shouldThrowExceptionWhenPriceIsNegative() {
        // Given
        String id = "P001";
        String name = "Laptop";
        String description = "High-performance laptop";
        BigDecimal negativePrice = new BigDecimal("-100.00");
        String category = "Electronics";

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Product(id, name, description, negativePrice, category)
        );
    }

    @Test
    @DisplayName("Should update price successfully")
    void shouldUpdatePriceSuccessfully() {
        // Given
        Product product = new Product("P001", "Laptop", "High-performance laptop",
                new BigDecimal("999.99"), "Electronics");
        BigDecimal newPrice = new BigDecimal("899.99");

        // When
        product.setPrice(newPrice);

        // Then
        assertEquals(newPrice, product.getPrice());
    }

    @Test
    @DisplayName("Should throw exception when updating to negative price")
    void shouldThrowExceptionWhenUpdatingToNegativePrice() {
        // Given
        Product product = new Product("P001", "Laptop", "High-performance laptop",
                new BigDecimal("999.99"), "Electronics");
        BigDecimal negativePrice = new BigDecimal("-100.00");

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            product.setPrice(negativePrice)
        );
    }

    @Test
    @DisplayName("Should update description successfully")
    void shouldUpdateDescriptionSuccessfully() {
        // Given
        Product product = new Product("P001", "Laptop", "High-performance laptop",
                new BigDecimal("999.99"), "Electronics");
        String newDescription = "Updated description";

        // When
        product.setDescription(newDescription);

        // Then
        assertEquals(newDescription, product.getDescription());
    }

    @Test
    @DisplayName("Should consider products equal when they have same ID")
    void shouldConsiderProductsEqualWhenTheyHaveSameId() {
        // Given
        Product product1 = new Product("P001", "Laptop", "High-performance laptop",
                new BigDecimal("999.99"), "Electronics");
        Product product2 = new Product("P001", "Different Name", "Different description",
                new BigDecimal("500.00"), "Different Category");

        // When & Then
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    @DisplayName("Should consider products not equal when they have different IDs")
    void shouldConsiderProductsNotEqualWhenTheyHaveDifferentIds() {
        // Given
        Product product1 = new Product("P001", "Laptop", "High-performance laptop",
                new BigDecimal("999.99"), "Electronics");
        Product product2 = new Product("P002", "Laptop", "High-performance laptop",
                new BigDecimal("999.99"), "Electronics");

        // When & Then
        assertNotEquals(product1, product2);
    }

    @Test
    @DisplayName("Should return valid string representation")
    void shouldReturnValidStringRepresentation() {
        // Given
        Product product = new Product("P001", "Laptop", "High-performance laptop",
                new BigDecimal("999.99"), "Electronics");

        // When
        String result = product.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("P001"));
        assertTrue(result.contains("Laptop"));
        assertTrue(result.contains("999.99"));
    }
}
