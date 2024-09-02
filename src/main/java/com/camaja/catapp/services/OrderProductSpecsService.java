package com.camaja.catapp.services;

import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.models.OrderProductSpecs;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderProductSpecsService extends CrudRepository<OrderProductSpecs, Integer> {
    Iterable<OrderProductSpecs> findByOrderProduct(Optional<OrderProduct> orderProduct);
}
