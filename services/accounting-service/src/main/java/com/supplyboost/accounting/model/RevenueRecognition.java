package com.supplyboost.accounting.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "revenue_recognition")
public class RevenueRecognition {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "invoice_id", nullable = false)
  private Long invoiceId;

  @Column(name = "invoice_number", nullable = false)
  private String invoiceNumber;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(name = "order_number", nullable = false)
  private String orderNumber;

  @Column(name = "amount", precision = 10, scale = 2, nullable = false)
  private BigDecimal amount;

  @Column(name = "recognition_date", nullable = false)
  private LocalDateTime recognitionDate;

  @Column(name = "recognition_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private RevenueRecognitionType type;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private RevenueStatus status;

  @Column(name = "notes")
  private String notes;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    if (status == null) {
      status = RevenueStatus.PENDING;
    }
  }
}
