package com.camaja.catapp.repository;

import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProduct, Integer> {
    Iterable<OrderProduct> findByOrder(Optional<Order> order);
}
