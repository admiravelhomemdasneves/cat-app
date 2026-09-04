package com.camaja.catapp.repository;

import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem>  findByOrder(Order order);
    List<OrderItem> findByOrderAndFlagActive(Order order, Boolean flagActive);
}
