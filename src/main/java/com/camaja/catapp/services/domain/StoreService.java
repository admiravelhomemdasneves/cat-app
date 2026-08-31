package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.Store;
import com.camaja.catapp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StoreService {
    private final StoreRepository storeRepository;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Store getStoreById(Long id) {
        return storeRepository.findById(id).orElse(null);
    }

    public void saveStore(Store store) {
        storeRepository.save(store);
    }
}