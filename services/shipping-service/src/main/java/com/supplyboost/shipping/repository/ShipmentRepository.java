package com.supplyboost.shipping.repository;

import com.supplyboost.shipping.model.Shipment;
import com.supplyboost.shipping.model.ShipmentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

  Optional<Shipment> findByShipmentId(String shipmentId);

  Optional<Shipment> findByTrackingNumber(String trackingNumber);

  Optional<Shipment> findByOrderId(Long orderId);

  List<Shipment> findByUserId(Long userId);

  List<Shipment> findByStatus(ShipmentStatus status);
}
