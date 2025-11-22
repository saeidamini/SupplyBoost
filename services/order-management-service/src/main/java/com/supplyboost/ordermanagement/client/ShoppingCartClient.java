package com.supplyboost.ordermanagement.client;

import com.supplyboost.ordermanagement.dto.CartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShoppingCartClient {

  private final RestTemplate restTemplate;

  @Value("${services.shopping-cart.url:http://localhost:8083}")
  private String shoppingCartUrl;

  public CartDto getCart(String cartId) {
    try {
      String url = shoppingCartUrl + "/api/v1/cart/" + cartId;
      return restTemplate.getForObject(url, CartDto.class);
    } catch (Exception e) {
      log.error("Failed to fetch cart {} from shopping cart service", cartId, e);
      return null;
    }
  }

  public void clearCart(String cartId) {
    try {
      String url = shoppingCartUrl + "/api/v1/cart/" + cartId;
      restTemplate.delete(url);
      log.info("Cart {} cleared successfully", cartId);
    } catch (Exception e) {
      log.error("Failed to clear cart {}", cartId, e);
    }
  }
}
