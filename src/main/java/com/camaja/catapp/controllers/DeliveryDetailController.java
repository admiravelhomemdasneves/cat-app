package com.camaja.catapp.controllers;

import com.camaja.catapp.models.DeliveryDetail;
import com.camaja.catapp.repository.DeliveryDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/deliveryDetail")
@CrossOrigin
public class DeliveryDetailController {
    @Autowired
    DeliveryDetailRepository deliveryDetailRepository;

    @GetMapping
    Iterable<DeliveryDetail> read() {
        return deliveryDetailRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<DeliveryDetail> read(@PathVariable int id) {
        return deliveryDetailRepository.findById(id);
    }

    @PostMapping
    DeliveryDetail save(@RequestBody DeliveryDetail deliveryDetail) {
        deliveryDetail.setA$user$(new Timestamp(System.currentTimeMillis()));
        deliveryDetailRepository.save(deliveryDetail);
        return deliveryDetail;
    }

    @PutMapping
    DeliveryDetail update(@RequestBody DeliveryDetail deliveryDetail) {
        deliveryDetail.setA$user$(new Timestamp(System.currentTimeMillis()));
        deliveryDetailRepository.save(deliveryDetail);
        return deliveryDetail;
    }

    @DeleteMapping("/{id}")
    Optional<DeliveryDetail> delete(@PathVariable int id) {
        deliveryDetailRepository.deleteById(id);
        return read(id);
    }
}
