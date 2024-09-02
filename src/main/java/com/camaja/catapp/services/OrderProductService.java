package com.camaja.catapp.services;

import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderProduct;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderProductService extends CrudRepository<OrderProduct, Integer> {
    Iterable<OrderProduct> findByOrder(Optional<Order> order);
}
