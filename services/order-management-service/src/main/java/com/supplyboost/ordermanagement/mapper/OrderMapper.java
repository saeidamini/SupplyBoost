package com.supplyboost.ordermanagement.mapper;

import com.supplyboost.ordermanagement.dto.AddressDto;
import com.supplyboost.ordermanagement.dto.OrderItemDto;
import com.supplyboost.ordermanagement.dto.OrderResponse;
import com.supplyboost.ordermanagement.model.Order;
import com.supplyboost.ordermanagement.model.OrderItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(target = "line1", source = "shippingAddressLine1")
  @Mapping(target = "line2", source = "shippingAddressLine2")
  @Mapping(target = "city", source = "shippingCity")
  @Mapping(target = "state", source = "shippingState")
  @Mapping(target = "postalCode", source = "shippingPostalCode")
  @Mapping(target = "country", source = "shippingCountry")
  AddressDto toShippingAddress(Order order);

  @Mapping(target = "line1", source = "billingAddressLine1")
  @Mapping(target = "line2", source = "billingAddressLine2")
  @Mapping(target = "city", source = "billingCity")
  @Mapping(target = "state", source = "billingState")
  @Mapping(target = "postalCode", source = "billingPostalCode")
  @Mapping(target = "country", source = "billingCountry")
  AddressDto toBillingAddress(Order order);

  OrderItemDto toOrderItemDto(OrderItem orderItem);

  List<OrderItemDto> toOrderItemDtos(List<OrderItem> orderItems);

  @Mapping(target = "shippingAddress", expression = "java(toShippingAddress(order))")
  @Mapping(target = "billingAddress", expression = "java(toBillingAddress(order))")
  OrderResponse toOrderResponse(Order order);

  List<OrderResponse> toOrderResponses(List<Order> orders);
}
