package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.Store;
import com.camaja.catapp.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {
    @Autowired StoreRepository storeRepository;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Store getStoreById(Long id) {
        return storeRepository.findById(id.intValue()).orElse(null);
    }

    public void saveStore(Store store) {
        storeRepository.save(store);
    }
}