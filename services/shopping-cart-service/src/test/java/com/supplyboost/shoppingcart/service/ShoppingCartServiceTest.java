package com.supplyboost.shoppingcart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.supplyboost.shoppingcart.client.ProductCatalogClient;
import com.supplyboost.shoppingcart.dto.AddToCartRequest;
import com.supplyboost.shoppingcart.dto.CartResponse;
import com.supplyboost.shoppingcart.dto.ProductDto;
import com.supplyboost.shoppingcart.exception.InsufficientStockException;
import com.supplyboost.shoppingcart.exception.ProductNotFoundException;
import com.supplyboost.shoppingcart.mapper.CartMapper;
import com.supplyboost.shoppingcart.model.ShoppingCart;
import com.supplyboost.shoppingcart.repository.ShoppingCartRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

  @Mock private ShoppingCartRepository cartRepository;

  @Mock private ProductCatalogClient productCatalogClient;

  @Mock private CartMapper cartMapper;

  @InjectMocks private ShoppingCartService shoppingCartService;

  private ProductDto testProduct;
  private AddToCartRequest addToCartRequest;
  private ShoppingCart testCart;

  @BeforeEach
  void setUp() {
    testProduct =
        ProductDto.builder()
            .id(1L)
            .sku("PROD-001")
            .name("Test Product")
            .price(new BigDecimal("29.99"))
            .stockQuantity(10)
            .active(true)
            .build();

    addToCartRequest = AddToCartRequest.builder().productId(1L).quantity(2).build();

    testCart = ShoppingCart.builder().id("cart-123").build();
  }

  @Test
  void addToCart_ShouldAddProductSuccessfully() {
    // Arrange
    when(productCatalogClient.getProduct(anyLong())).thenReturn(testProduct);
    when(cartRepository.findById(anyString()))
        .thenReturn(Optional.of(testCart));
    when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);
    when(cartMapper.toCartResponse(any(ShoppingCart.class)))
        .thenReturn(new CartResponse());

    // Act
    CartResponse result = shoppingCartService.addToCart("cart-123", addToCartRequest);

    // Assert
    assertNotNull(result);
    verify(cartRepository).save(any(ShoppingCart.class));
  }

  @Test
  void addToCart_ShouldThrowException_WhenProductNotFound() {
    // Arrange
    when(productCatalogClient.getProduct(anyLong())).thenReturn(null);

    // Act & Assert
    assertThrows(
        ProductNotFoundException.class,
        () -> shoppingCartService.addToCart("cart-123", addToCartRequest));
  }

  @Test
  void addToCart_ShouldThrowException_WhenInsufficientStock() {
    // Arrange
    testProduct.setStockQuantity(1);
    addToCartRequest.setQuantity(5);
    when(productCatalogClient.getProduct(anyLong())).thenReturn(testProduct);

    // Act & Assert
    assertThrows(
        InsufficientStockException.class,
        () -> shoppingCartService.addToCart("cart-123", addToCartRequest));
  }

  @Test
  void getCart_ShouldReturnExistingCart() {
    // Arrange
    when(cartRepository.findById(anyString())).thenReturn(Optional.of(testCart));
    when(cartMapper.toCartResponse(any(ShoppingCart.class)))
        .thenReturn(new CartResponse());

    // Act
    CartResponse result = shoppingCartService.getCart("cart-123");

    // Assert
    assertNotNull(result);
    verify(cartRepository).findById("cart-123");
  }

  @Test
  void clearCart_ShouldClearAllItems() {
    // Arrange
    when(cartRepository.findById(anyString())).thenReturn(Optional.of(testCart));
    when(cartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

    // Act
    shoppingCartService.clearCart("cart-123");

    // Assert
    verify(cartRepository).save(any(ShoppingCart.class));
  }
}
