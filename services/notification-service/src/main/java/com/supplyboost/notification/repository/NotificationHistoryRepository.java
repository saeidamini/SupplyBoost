package com.supplyboost.notification.repository;

import com.supplyboost.notification.model.NotificationHistory;
import com.supplyboost.notification.model.NotificationStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {

  List<NotificationHistory> findByRecipientEmail(String recipientEmail);

  List<NotificationHistory> findByOrderNumber(String orderNumber);

  List<NotificationHistory> findByStatus(NotificationStatus status);

  List<NotificationHistory> findByStatusAndRetryCountLessThan(
      NotificationStatus status, Integer maxRetries);

  List<NotificationHistory> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
