package com.camaja.catapp.repository;

import com.camaja.catapp.models.OrderPriority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPriorityRepository extends CrudRepository<OrderPriority, Integer> {
}
