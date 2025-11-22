package com.supplyboost.shoppingcart.service;

import com.supplyboost.shoppingcart.client.ProductCatalogClient;
import com.supplyboost.shoppingcart.dto.AddToCartRequest;
import com.supplyboost.shoppingcart.dto.CartResponse;
import com.supplyboost.shoppingcart.dto.ProductDto;
import com.supplyboost.shoppingcart.exception.InsufficientStockException;
import com.supplyboost.shoppingcart.exception.ProductNotFoundException;
import com.supplyboost.shoppingcart.mapper.CartMapper;
import com.supplyboost.shoppingcart.model.CartItem;
import com.supplyboost.shoppingcart.model.ShoppingCart;
import com.supplyboost.shoppingcart.repository.ShoppingCartRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartService {

  private final ShoppingCartRepository cartRepository;
  private final ProductCatalogClient productCatalogClient;
  private final CartMapper cartMapper;

  public CartResponse getCart(String cartId) {
    ShoppingCart cart =
        cartRepository.findById(cartId).orElseGet(() -> createNewCart(cartId));
    return cartMapper.toCartResponse(cart);
  }

  public CartResponse addToCart(String cartId, AddToCartRequest request) {
    // Fetch product details from catalog service
    ProductDto product = productCatalogClient.getProduct(request.getProductId());
    if (product == null || !product.getActive()) {
      throw new ProductNotFoundException(
          "Product not found or inactive: " + request.getProductId());
    }

    // Check stock availability
    if (product.getStockQuantity() < request.getQuantity()) {
      throw new InsufficientStockException(
          "Insufficient stock for product: " + product.getName());
    }

    // Get or create cart
    ShoppingCart cart =
        cartRepository.findById(cartId).orElseGet(() -> createNewCart(cartId));

    // Create cart item
    CartItem cartItem =
        CartItem.builder()
            .productId(product.getId())
            .productName(product.getName())
            .productSku(product.getSku())
            .unitPrice(product.getPrice())
            .quantity(request.getQuantity())
            .imageUrl(product.getImageUrl())
            .build();

    // Add to cart
    cart.addItem(cartItem);

    // Save cart
    ShoppingCart savedCart = cartRepository.save(cart);
    log.info(
        "Added product {} to cart {} with quantity {}",
        product.getName(),
        cartId,
        request.getQuantity());

    return cartMapper.toCartResponse(savedCart);
  }

  public CartResponse updateCartItem(
      String cartId, Long productId, Integer quantity) {
    ShoppingCart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(
                () -> new ProductNotFoundException("Cart not found: " + cartId));

    if (quantity > 0) {
      // Validate stock availability
      ProductDto product = productCatalogClient.getProduct(productId);
      if (product != null && product.getStockQuantity() < quantity) {
        throw new InsufficientStockException(
            "Insufficient stock for product: " + product.getName());
      }
    }

    cart.updateItemQuantity(productId, quantity);
    ShoppingCart savedCart = cartRepository.save(cart);
    log.info(
        "Updated cart {} item {} to quantity {}", cartId, productId, quantity);

    return cartMapper.toCartResponse(savedCart);
  }

  public void removeFromCart(String cartId, Long productId) {
    ShoppingCart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(
                () -> new ProductNotFoundException("Cart not found: " + cartId));

    cart.removeItem(productId);
    cartRepository.save(cart);
    log.info("Removed product {} from cart {}", productId, cartId);
  }

  public void clearCart(String cartId) {
    ShoppingCart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(
                () -> new ProductNotFoundException("Cart not found: " + cartId));

    cart.clear();
    cartRepository.save(cart);
    log.info("Cleared cart {}", cartId);
  }

  public void deleteCart(String cartId) {
    cartRepository.deleteById(cartId);
    log.info("Deleted cart {}", cartId);
  }

  private ShoppingCart createNewCart(String cartId) {
    return ShoppingCart.builder()
        .id(cartId)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
