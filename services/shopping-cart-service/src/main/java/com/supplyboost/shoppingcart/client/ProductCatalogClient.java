package com.supplyboost.shoppingcart.client;

import com.supplyboost.shoppingcart.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCatalogClient {

  private final RestTemplate restTemplate;

  @Value("${services.product-catalog.url:http://localhost:8082}")
  private String productCatalogUrl;

  public ProductDto getProduct(Long productId) {
    try {
      String url = productCatalogUrl + "/api/v1/products/" + productId;
      return restTemplate.getForObject(url, ProductDto.class);
    } catch (Exception e) {
      log.error("Failed to fetch product {} from catalog service", productId, e);
      return null;
    }
  }
}
