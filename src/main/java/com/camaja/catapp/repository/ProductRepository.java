package com.camaja.catapp.repository;

import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCompanyAndFlagActive(Company company, Boolean flagActive);
}
