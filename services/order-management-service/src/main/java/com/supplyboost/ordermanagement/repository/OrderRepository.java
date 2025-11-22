package com.supplyboost.ordermanagement.repository;

import com.supplyboost.ordermanagement.model.Order;
import com.supplyboost.ordermanagement.model.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByOrderNumber(String orderNumber);

  List<Order> findByUserId(Long userId);

  List<Order> findByStatus(OrderStatus status);

  List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
}
