package com.camaja.catapp.repository;

import com.camaja.catapp.models.OrderBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderBillRepository extends JpaRepository<OrderBill, Long> {
}
