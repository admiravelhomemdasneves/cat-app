package com.camaja.catapp.repository;

import com.camaja.catapp.models.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Integer> {
}
