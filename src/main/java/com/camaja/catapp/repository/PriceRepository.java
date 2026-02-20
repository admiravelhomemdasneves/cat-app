package com.camaja.catapp.repository;

import com.camaja.catapp.models.Price;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends CrudRepository<Price, Integer> {
}
