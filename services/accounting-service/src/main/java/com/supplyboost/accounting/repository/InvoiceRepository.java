package com.supplyboost.accounting.repository;

import com.supplyboost.accounting.model.Invoice;
import com.supplyboost.accounting.model.InvoiceStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

  Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

  Optional<Invoice> findByOrderId(Long orderId);

  Optional<Invoice> findByOrderNumber(String orderNumber);

  List<Invoice> findByCustomerEmail(String customerEmail);

  List<Invoice> findByStatus(InvoiceStatus status);

  List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDateTime date);

  List<Invoice> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  boolean existsByOrderId(Long orderId);
}
