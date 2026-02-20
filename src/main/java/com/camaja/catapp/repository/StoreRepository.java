package com.camaja.catapp.repository;

import com.camaja.catapp.models.Store;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends CrudRepository<Store, Integer> {
}
