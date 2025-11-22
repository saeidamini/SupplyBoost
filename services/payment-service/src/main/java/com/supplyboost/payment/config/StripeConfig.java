package com.supplyboost.payment.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StripeConfig {

  @Value("${stripe.api.key:}")
  private String stripeApiKey;

  @Value("${payment.mode:mock}")
  private String paymentMode;

  @PostConstruct
  public void init() {
    if ("stripe".equalsIgnoreCase(paymentMode)) {
      if (stripeApiKey != null && !stripeApiKey.isEmpty()) {
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe API configured successfully");
      } else {
        log.warn(
            "Stripe mode enabled but no API key provided. Payment processing will fail.");
      }
    } else {
      log.info("Payment service running in MOCK mode");
    }
  }
}
