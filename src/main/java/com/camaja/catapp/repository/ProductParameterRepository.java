package com.camaja.catapp.repository;

import com.camaja.catapp.models.ProductParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductParameterRepository extends JpaRepository<ProductParameters, Integer> {
    List<ProductParameters> findByProductIdProduct(Long idProduct);
}
