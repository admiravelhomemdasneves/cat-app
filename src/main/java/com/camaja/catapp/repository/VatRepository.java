package com.camaja.catapp.repository;

import com.camaja.catapp.models.Vat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VatRepository extends CrudRepository<Vat, Integer> {
}
