package com.camaja.catapp.services;

import com.camaja.catapp.models.OrderStatus;
import org.springframework.data.repository.CrudRepository;

public interface OrderStatusService extends CrudRepository<OrderStatus, Integer> {
}
