package com.camaja.catapp.repository;

import com.camaja.catapp.models.DeliveryDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryDetailRepository extends CrudRepository<DeliveryDetail, Integer> {
}
