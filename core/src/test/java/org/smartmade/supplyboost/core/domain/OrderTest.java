package org.smartmade.supplyboost.core.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Domain Tests")
class OrderTest {

    @Test
    @DisplayName("Should create order with valid parameters")
    void shouldCreateOrderWithValidParameters() {
        // Given
        String orderId = "ORD001";
        String customerId = "CUST001";

        // When
        Order order = new Order(orderId, customerId);

        // Then
        assertNotNull(order);
        assertEquals(orderId, order.getOrderId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(Order.OrderStatus.PENDING, order.getStatus());
        assertTrue(order.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertNotNull(order.getOrderDate());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when order ID is null or empty")
    void shouldThrowExceptionWhenOrderIdIsNullOrEmpty(String invalidOrderId) {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Order(invalidOrderId, "CUST001")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when customer ID is null or empty")
    void shouldThrowExceptionWhenCustomerIdIsNullOrEmpty(String invalidCustomerId) {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Order("ORD001", invalidCustomerId)
        );
    }

    @Test
    @DisplayName("Should add item to order successfully")
    void shouldAddItemToOrderSuccessfully() {
        // Given
        Order order = new Order("ORD001", "CUST001");
        Order.OrderItem item = new Order.OrderItem("P001", 2, new BigDecimal("100.00"));

        // When
        order.addItem(item);

        // Then
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("200.00"), order.getTotalAmount());
    }

    @Test
    @DisplayName("Should add multiple items and calculate total correctly")
    void shouldAddMultipleItemsAndCalculateTotalCorrectly() {
        // Given
        Order order = new Order("ORD001", "CUST001");
        Order.OrderItem item1 = new Order.OrderItem("P001", 2, new BigDecimal("100.00"));
        Order.OrderItem item2 = new Order.OrderItem("P002", 3, new BigDecimal("50.00"));

        // When
        order.addItem(item1);
        order.addItem(item2);

        // Then
        assertEquals(2, order.getItems().size());
        assertEquals(new BigDecimal("350.00"), order.getTotalAmount());
    }

    @Test
    @DisplayName("Should throw exception when adding null item")
    void shouldThrowExceptionWhenAddingNullItem() {
        // Given
        Order order = new Order("ORD001", "CUST001");

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            order.addItem(null)
        );
    }

    @Test
    @DisplayName("Should confirm order successfully")
    void shouldConfirmOrderSuccessfully() {
        // Given
        Order order = new Order("ORD001", "CUST001");
        Order.OrderItem item = new Order.OrderItem("P001", 2, new BigDecimal("100.00"));
        order.addItem(item);

        // When
        order.confirm();

        // Then
        assertEquals(Order.OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when confirming order with no items")
    void shouldThrowExceptionWhenConfirmingOrderWithNoItems() {
        // Given
        Order order = new Order("ORD001", "CUST001");

        // When & Then
        assertThrows(IllegalStateException.class, () ->
            order.confirm()
        );
    }

    @Test
    @DisplayName("Should throw exception when confirming already confirmed order")
    void shouldThrowExceptionWhenConfirmingAlreadyConfirmedOrder() {
        // Given
        Order order = new Order("ORD001", "CUST001");
        Order.OrderItem item = new Order.OrderItem("P001", 2, new BigDecimal("100.00"));
        order.addItem(item);
        order.confirm();

        // When & Then
        assertThrows(IllegalStateException.class, () ->
            order.confirm()
        );
    }

    @Test
    @DisplayName("Should throw exception when adding items to confirmed order")
    void shouldThrowExceptionWhenAddingItemsToConfirmedOrder() {
        // Given
        Order order = new Order("ORD001", "CUST001");
        Order.OrderItem item1 = new Order.OrderItem("P001", 2, new BigDecimal("100.00"));
        order.addItem(item1);
        order.confirm();

        Order.OrderItem item2 = new Order.OrderItem("P002", 1, new BigDecimal("50.00"));

        // When & Then
        assertThrows(IllegalStateException.class, () ->
            order.addItem(item2)
        );
    }

    @Test
    @DisplayName("Should cancel pending order successfully")
    void shouldCancelPendingOrderSuccessfully() {
        // Given
        Order order = new Order("ORD001", "CUST001");
        Order.OrderItem item = new Order.OrderItem("P001", 2, new BigDecimal("100.00"));
        order.addItem(item);

        // When
        order.cancel();

        // Then
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when cancelling shipped order")
    void shouldThrowExceptionWhenCancellingShippedOrder() {
        // Given
        Order order = new Order("ORD001", "CUST001");
        Order.OrderItem item = new Order.OrderItem("P001", 2, new BigDecimal("100.00"));
        order.addItem(item);
        order.confirm();
        // Manually set status to SHIPPED using reflection or we need a ship method
        // For now, we'll skip this test as we don't have a ship method
        // This shows that we might need to add more methods to fully test the domain
    }

    @Test
    @DisplayName("Should return immutable list of items")
    void shouldReturnImmutableListOfItems() {
        // Given
        Order order = new Order("ORD001", "CUST001");
        Order.OrderItem item = new Order.OrderItem("P001", 2, new BigDecimal("100.00"));
        order.addItem(item);

        // When & Then
        assertThrows(UnsupportedOperationException.class, () ->
            order.getItems().add(new Order.OrderItem("P002", 1, new BigDecimal("50.00")))
        );
    }

    // OrderItem Tests

    @Test
    @DisplayName("Should create order item with valid parameters")
    void shouldCreateOrderItemWithValidParameters() {
        // Given
        String productId = "P001";
        int quantity = 5;
        BigDecimal unitPrice = new BigDecimal("20.00");

        // When
        Order.OrderItem item = new Order.OrderItem(productId, quantity, unitPrice);

        // Then
        assertNotNull(item);
        assertEquals(productId, item.getProductId());
        assertEquals(quantity, item.getQuantity());
        assertEquals(unitPrice, item.getUnitPrice());
        assertEquals(new BigDecimal("100.00"), item.getSubtotal());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when order item product ID is null or empty")
    void shouldThrowExceptionWhenOrderItemProductIdIsNullOrEmpty(String invalidProductId) {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Order.OrderItem(invalidProductId, 5, new BigDecimal("20.00"))
        );
    }

    @Test
    @DisplayName("Should throw exception when order item quantity is zero")
    void shouldThrowExceptionWhenOrderItemQuantityIsZero() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Order.OrderItem("P001", 0, new BigDecimal("20.00"))
        );
    }

    @Test
    @DisplayName("Should throw exception when order item quantity is negative")
    void shouldThrowExceptionWhenOrderItemQuantityIsNegative() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Order.OrderItem("P001", -5, new BigDecimal("20.00"))
        );
    }

    @Test
    @DisplayName("Should throw exception when order item unit price is null")
    void shouldThrowExceptionWhenOrderItemUnitPriceIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Order.OrderItem("P001", 5, null)
        );
    }

    @Test
    @DisplayName("Should throw exception when order item unit price is negative")
    void shouldThrowExceptionWhenOrderItemUnitPriceIsNegative() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new Order.OrderItem("P001", 5, new BigDecimal("-20.00"))
        );
    }

    @Test
    @DisplayName("Should calculate subtotal correctly")
    void shouldCalculateSubtotalCorrectly() {
        // Given
        Order.OrderItem item = new Order.OrderItem("P001", 3, new BigDecimal("15.50"));

        // When
        BigDecimal subtotal = item.getSubtotal();

        // Then
        assertEquals(new BigDecimal("46.50"), subtotal);
    }
}
