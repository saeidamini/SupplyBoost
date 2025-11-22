package com.supplyboost.accounting.repository;

import com.supplyboost.accounting.model.RevenueRecognition;
import com.supplyboost.accounting.model.RevenueStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RevenueRecognitionRepository extends JpaRepository<RevenueRecognition, Long> {

  List<RevenueRecognition> findByInvoiceId(Long invoiceId);

  List<RevenueRecognition> findByOrderId(Long orderId);

  List<RevenueRecognition> findByStatus(RevenueStatus status);

  List<RevenueRecognition> findByRecognitionDateBetween(LocalDateTime start, LocalDateTime end);

  @Query(
      "SELECT SUM(r.amount) FROM RevenueRecognition r WHERE r.status = :status AND r.recognitionDate BETWEEN :start AND :end")
  java.math.BigDecimal sumRevenueByStatusAndDateRange(
      RevenueStatus status, LocalDateTime start, LocalDateTime end);
}
