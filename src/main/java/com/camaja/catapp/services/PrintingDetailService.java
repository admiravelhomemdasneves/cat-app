package com.camaja.catapp.services;

import com.camaja.catapp.models.PrintingDetail;
import org.springframework.data.repository.CrudRepository;

public interface PrintingDetailService extends CrudRepository<PrintingDetail, Integer> {
}
