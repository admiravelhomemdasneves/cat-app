package com.camaja.catapp.repository;

import com.camaja.catapp.models.Vat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VatRepository extends JpaRepository<Vat, Integer> {
}
