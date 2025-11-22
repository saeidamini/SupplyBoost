package com.supplyboost.shipping.mapper;

import com.supplyboost.shipping.dto.ShipmentResponse;
import com.supplyboost.shipping.model.Shipment;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {

  ShipmentResponse toShipmentResponse(Shipment shipment);

  List<ShipmentResponse> toShipmentResponses(List<Shipment> shipments);
}
