package com.supplyboost.payment.repository;

import com.supplyboost.payment.model.Payment;
import com.supplyboost.payment.model.PaymentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByPaymentId(String paymentId);

  Optional<Payment> findByOrderId(Long orderId);

  Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

  List<Payment> findByUserId(Long userId);

  List<Payment> findByStatus(PaymentStatus status);
}
