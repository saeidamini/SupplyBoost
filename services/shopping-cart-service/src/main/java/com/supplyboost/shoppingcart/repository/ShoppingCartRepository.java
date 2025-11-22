package com.supplyboost.shoppingcart.repository;

import com.supplyboost.shoppingcart.model.ShoppingCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, String> {}
