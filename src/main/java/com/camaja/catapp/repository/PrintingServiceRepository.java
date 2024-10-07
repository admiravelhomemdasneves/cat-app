package com.camaja.catapp.repository;

import com.camaja.catapp.models.PrintingService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintingServiceRepository extends CrudRepository<PrintingService, Integer> {
}
