package com.camaja.catapp.services;

import com.camaja.catapp.models.DeliveryDetail;
import org.springframework.data.repository.CrudRepository;

public interface DeliveryDetailService extends CrudRepository<DeliveryDetail, Integer> {
}
