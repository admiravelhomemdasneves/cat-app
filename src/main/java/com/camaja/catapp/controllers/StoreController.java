package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Store;
import com.camaja.catapp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stores")
@CrossOrigin
public class StoreController {
    private final StoreRepository storeRepository;

    @GetMapping
    Iterable<Store> getActiveOrderStores() {
        List<Store> stores = (List<Store>) storeRepository.findAll();
        List<Store> activeStores = stores.stream().filter(store -> store.getFlagActive() == null || store.getFlagActive() == true).collect(Collectors.toList());
        return activeStores;
    }

    @GetMapping("/{id}")
    Optional<Store> read(@PathVariable Long id) {
        return storeRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Store inactivateOrderStore(@PathVariable Long id) {
        Optional<Store> orderStoreOptional = storeRepository.findById(id);

        if (orderStoreOptional.isPresent()) {
            Store store = orderStoreOptional.get();
            store.setFlagActive(false);
            storeRepository.save(store);
            return store;
        } else {
            return null;
        }
    }

    @PostMapping
    Store save(@RequestBody Store store) {
        storeRepository.save(store);
        return store;
    }

    @DeleteMapping("/{id}")
    Optional<Store> delete(@PathVariable Long id) {
        storeRepository.deleteById(id);
        return read(id);
    }
}
