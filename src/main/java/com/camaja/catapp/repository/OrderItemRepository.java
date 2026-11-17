package com.camaja.catapp.repository;

import com.camaja.catapp.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem>  findByOrder(Order order);
    List<OrderItem> findByOrderAndFlagActive(Order order, Boolean flagActive);

    @Query("SELECT i FROM OrderItem i WHERE i.idOrderItem = :id AND i.order.company = :company")
    Optional<OrderItem> findOwned(@Param("id") Long id, @Param("company") Company company);
}
