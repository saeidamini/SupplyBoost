package com.supplyboost.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "notification.email")
public class EmailConfig {

  private String from;
  private String fromName;
  private boolean enabled = true;
  private boolean mockMode = true;
  private boolean async = true;
  private RetryConfig retry = new RetryConfig();

  @Data
  public static class RetryConfig {
    private int maxAttempts = 3;
    private long delay = 2000;
  }
}
