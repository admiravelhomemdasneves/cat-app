package com.camaja.catapp.repository;

import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {
    Iterable<OrderItem> findByOrder(Optional<Order> order);
}
