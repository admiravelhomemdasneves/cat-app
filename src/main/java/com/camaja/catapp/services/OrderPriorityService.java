package com.camaja.catapp.services;

import com.camaja.catapp.models.OrderPriority;
import org.springframework.data.repository.CrudRepository;

public interface OrderPriorityService extends CrudRepository<OrderPriority, Integer> {
}
