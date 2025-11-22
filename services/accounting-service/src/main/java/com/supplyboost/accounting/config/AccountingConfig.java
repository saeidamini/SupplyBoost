package com.supplyboost.accounting.config;

import java.math.BigDecimal;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "accounting.invoice")
public class AccountingConfig {

  private String pdfStoragePath = "/tmp/invoices";
  private String companyName = "SupplyBoost Inc.";
  private String companyAddress = "123 Business St, City, State 12345";
  private String companyEmail = "billing@supplyboost.com";
  private String companyPhone = "+1-555-0100";
  private BigDecimal taxRate = new BigDecimal("0.10");
}
