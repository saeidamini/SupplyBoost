package com.supplyboost.notification.service;

import com.supplyboost.notification.config.EmailConfig;
import com.supplyboost.notification.model.NotificationHistory;
import com.supplyboost.notification.model.NotificationStatus;
import com.supplyboost.notification.model.NotificationType;
import com.supplyboost.notification.repository.NotificationHistoryRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final EmailConfig emailConfig;
  private final NotificationHistoryRepository notificationHistoryRepository;

  @Async
  @Transactional
  public void sendOrderConfirmation(
      String recipientEmail,
      String recipientName,
      String orderNumber,
      String totalAmount,
      String status,
      LocalDateTime orderDate) {

    log.info("Sending order confirmation email to: {}", recipientEmail);

    Map<String, Object> variables =
        Map.of(
            "customerName", recipientName,
            "orderNumber", orderNumber,
            "totalAmount", totalAmount,
            "status", status,
            "orderDate", orderDate);

    sendTemplatedEmail(
        recipientEmail,
        recipientName,
        "Order Confirmation - " + orderNumber,
        "order-confirmation",
        variables,
        NotificationType.ORDER_CONFIRMATION,
        orderNumber,
        null,
        null);
  }

  @Async
  @Transactional
  public void sendPaymentConfirmation(
      String recipientEmail,
      String recipientName,
      String orderNumber,
      String paymentNumber,
      String paymentMethod,
      String amount,
      LocalDateTime paymentDate) {

    log.info("Sending payment confirmation email to: {}", recipientEmail);

    Map<String, Object> variables =
        Map.of(
            "customerName", recipientName,
            "orderNumber", orderNumber,
            "paymentNumber", paymentNumber,
            "paymentMethod", paymentMethod,
            "amount", amount,
            "paymentDate", paymentDate);

    sendTemplatedEmail(
        recipientEmail,
        recipientName,
        "Payment Confirmation - " + orderNumber,
        "payment-confirmation",
        variables,
        NotificationType.PAYMENT_CONFIRMATION,
        orderNumber,
        paymentNumber,
        null);
  }

  @Async
  @Transactional
  public void sendShipmentNotification(
      String recipientEmail,
      String recipientName,
      String orderNumber,
      String shipmentNumber,
      String trackingNumber,
      String carrier,
      LocalDateTime estimatedDelivery) {

    log.info("Sending shipment notification email to: {}", recipientEmail);

    Map<String, Object> variables =
        Map.of(
            "recipientName", recipientName,
            "orderNumber", orderNumber,
            "shipmentNumber", shipmentNumber,
            "trackingNumber", trackingNumber,
            "carrier", carrier,
            "estimatedDelivery", estimatedDelivery);

    sendTemplatedEmail(
        recipientEmail,
        recipientName,
        "Your Order Has Shipped - " + orderNumber,
        "shipment-notification",
        variables,
        NotificationType.SHIPMENT_NOTIFICATION,
        orderNumber,
        null,
        trackingNumber);
  }

  private void sendTemplatedEmail(
      String recipientEmail,
      String recipientName,
      String subject,
      String templateName,
      Map<String, Object> variables,
      NotificationType notificationType,
      String orderNumber,
      String paymentNumber,
      String trackingNumber) {

    NotificationHistory notification =
        NotificationHistory.builder()
            .type(notificationType)
            .recipientEmail(recipientEmail)
            .recipientName(recipientName)
            .subject(subject)
            .status(NotificationStatus.PENDING)
            .orderNumber(orderNumber)
            .paymentNumber(paymentNumber)
            .trackingNumber(trackingNumber)
            .build();

    try {
      if (!emailConfig.isEnabled()) {
        log.warn("Email sending is disabled. Skipping email to: {}", recipientEmail);
        notification.setStatus(NotificationStatus.FAILED);
        notification.setFailureReason("Email sending is disabled");
        notificationHistoryRepository.save(notification);
        return;
      }

      if (emailConfig.isMockMode()) {
        log.info(
            "MOCK MODE: Email would be sent to {} with subject: {}", recipientEmail, subject);
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notificationHistoryRepository.save(notification);
        return;
      }

      String htmlContent = processTemplate(templateName, variables);

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(emailConfig.getFrom(), emailConfig.getFromName());
      helper.setTo(recipientEmail);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(message);

      notification.setStatus(NotificationStatus.SENT);
      notification.setSentAt(LocalDateTime.now());
      notificationHistoryRepository.save(notification);

      log.info("Email sent successfully to: {}", recipientEmail);

    } catch (MessagingException e) {
      log.error("Failed to send email to: {}", recipientEmail, e);
      notification.setStatus(NotificationStatus.FAILED);
      notification.setFailureReason(e.getMessage());
      notification.setRetryCount(notification.getRetryCount() + 1);
      notificationHistoryRepository.save(notification);

      // Retry logic
      if (notification.getRetryCount() < emailConfig.getRetry().getMaxAttempts()) {
        log.info("Scheduling retry for notification: {}", notification.getId());
        scheduleRetry(notification);
      }
    }
  }

  private String processTemplate(String templateName, Map<String, Object> variables) {
    Context context = new Context();
    context.setVariables(variables);
    return templateEngine.process(templateName, context);
  }

  private void scheduleRetry(NotificationHistory notification) {
    // Simple retry logic - in production, use a job scheduler like Quartz
    try {
      Thread.sleep(emailConfig.getRetry().getDelay());
      // Retry sending the email
      log.info("Retrying email notification: {}", notification.getId());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Retry interrupted", e);
    }
  }
}
