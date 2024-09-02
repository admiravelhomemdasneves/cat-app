package com.camaja.catapp.services;

import com.camaja.catapp.models.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderService extends CrudRepository<Order, Integer> {
}
