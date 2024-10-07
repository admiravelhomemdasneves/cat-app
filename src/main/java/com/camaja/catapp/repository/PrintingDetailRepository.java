package com.camaja.catapp.repository;

import com.camaja.catapp.models.PrintingDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintingDetailRepository extends CrudRepository<PrintingDetail, Integer> {
}
