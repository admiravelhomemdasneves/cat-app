package com.camaja.catapp.repository;

import com.camaja.catapp.models.ProductOLD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOLDRepository extends JpaRepository<ProductOLD, Long> {
    List<ProductOLD> findByCatalogReference(String catalogReference);
}