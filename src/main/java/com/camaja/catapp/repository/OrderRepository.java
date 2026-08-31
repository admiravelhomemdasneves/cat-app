package com.camaja.catapp.repository;

import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCompanyAndFlagActive(Company company, Boolean flagActive);
}
