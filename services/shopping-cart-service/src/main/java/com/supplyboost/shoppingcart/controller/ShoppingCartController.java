package com.supplyboost.shoppingcart.controller;

import com.supplyboost.shoppingcart.dto.AddToCartRequest;
import com.supplyboost.shoppingcart.dto.CartResponse;
import com.supplyboost.shoppingcart.dto.UpdateCartItemRequest;
import com.supplyboost.shoppingcart.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart", description = "Shopping cart management APIs")
public class ShoppingCartController {

  private final ShoppingCartService shoppingCartService;

  @GetMapping("/{cartId}")
  @Operation(summary = "Get shopping cart", description = "Retrieve cart by ID")
  public ResponseEntity<CartResponse> getCart(@PathVariable String cartId) {
    log.info("Getting cart: {}", cartId);
    CartResponse cart = shoppingCartService.getCart(cartId);
    return ResponseEntity.ok(cart);
  }

  @PostMapping("/{cartId}/items")
  @Operation(
      summary = "Add item to cart",
      description = "Add a product to the shopping cart")
  public ResponseEntity<CartResponse> addToCart(
      @PathVariable String cartId, @Valid @RequestBody AddToCartRequest request) {
    log.info("Adding product {} to cart {}", request.getProductId(), cartId);
    CartResponse cart = shoppingCartService.addToCart(cartId, request);
    return ResponseEntity.ok(cart);
  }

  @PutMapping("/{cartId}/items/{productId}")
  @Operation(
      summary = "Update cart item",
      description = "Update quantity of a product in cart")
  public ResponseEntity<CartResponse> updateCartItem(
      @PathVariable String cartId,
      @PathVariable Long productId,
      @Valid @RequestBody UpdateCartItemRequest request) {
    log.info(
        "Updating cart {} item {} to quantity {}",
        cartId,
        productId,
        request.getQuantity());
    CartResponse cart =
        shoppingCartService.updateCartItem(
            cartId, productId, request.getQuantity());
    return ResponseEntity.ok(cart);
  }

  @DeleteMapping("/{cartId}/items/{productId}")
  @Operation(
      summary = "Remove item from cart",
      description = "Remove a product from the shopping cart")
  public ResponseEntity<Void> removeFromCart(
      @PathVariable String cartId, @PathVariable Long productId) {
    log.info("Removing product {} from cart {}", productId, cartId);
    shoppingCartService.removeFromCart(cartId, productId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{cartId}")
  @Operation(summary = "Clear cart", description = "Remove all items from cart")
  public ResponseEntity<Void> clearCart(@PathVariable String cartId) {
    log.info("Clearing cart {}", cartId);
    shoppingCartService.clearCart(cartId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{cartId}/delete")
  @Operation(
      summary = "Delete cart",
      description = "Permanently delete the shopping cart")
  public ResponseEntity<Void> deleteCart(@PathVariable String cartId) {
    log.info("Deleting cart {}", cartId);
    shoppingCartService.deleteCart(cartId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
