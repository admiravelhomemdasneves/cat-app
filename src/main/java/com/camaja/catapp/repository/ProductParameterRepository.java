package com.camaja.catapp.repository;

import com.camaja.catapp.models.ProductParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductParameterRepository extends JpaRepository<ProductParameters, Integer> {
}
