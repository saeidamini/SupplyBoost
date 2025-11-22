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
@Table(name = "invoices")
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "invoice_number", unique = true, nullable = false)
  private String invoiceNumber;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(name = "order_number", nullable = false)
  private String orderNumber;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "customer_name", nullable = false)
  private String customerName;

  @Column(name = "customer_email", nullable = false)
  private String customerEmail;

  @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
  private BigDecimal subtotal;

  @Column(name = "tax_amount", precision = 10, scale = 2, nullable = false)
  private BigDecimal taxAmount;

  @Column(name = "tax_rate", precision = 5, scale = 4)
  private BigDecimal taxRate;

  @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private InvoiceStatus status;

  @Column(name = "shipment_id")
  private Long shipmentId;

  @Column(name = "shipment_number")
  private String shipmentNumber;

  @Column(name = "payment_id")
  private String paymentId;

  @Column(name = "pdf_file_path")
  private String pdfFilePath;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (status == null) {
      status = InvoiceStatus.DRAFT;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
