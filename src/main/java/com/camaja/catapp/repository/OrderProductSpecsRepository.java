package com.camaja.catapp.repository;

import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.models.OrderProductSpecs;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderProductSpecsRepository extends CrudRepository<OrderProductSpecs, Integer> {
    Iterable<OrderProductSpecs> findByOrderProduct(Optional<OrderProduct> orderProduct);
}
