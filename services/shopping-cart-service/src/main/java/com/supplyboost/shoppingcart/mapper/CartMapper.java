package com.supplyboost.shoppingcart.mapper;

import com.supplyboost.shoppingcart.dto.CartResponse;
import com.supplyboost.shoppingcart.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

  @Mapping(source = "id", target = "cartId")
  CartResponse toCartResponse(ShoppingCart cart);
}
