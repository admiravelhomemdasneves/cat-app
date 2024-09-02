package com.camaja.catapp.controllers;

import com.camaja.catapp.models.DeliveryDetail;
import com.camaja.catapp.services.DeliveryDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/deliveryDetail")
@CrossOrigin
public class DeliveryDetailController {
    @Autowired
    DeliveryDetailService deliveryDetailService;

    @GetMapping
    Iterable<DeliveryDetail> read() {
        return deliveryDetailService.findAll();
    }

    @GetMapping("/{id}")
    Optional<DeliveryDetail> read(@PathVariable int id) {
        return deliveryDetailService.findById(id);
    }

    @PostMapping
    DeliveryDetail save(@RequestBody DeliveryDetail deliveryDetail) {
        deliveryDetail.setA$user$(new Timestamp(System.currentTimeMillis()));
        deliveryDetailService.save(deliveryDetail);
        return deliveryDetail;
    }

    @PutMapping
    DeliveryDetail update(@RequestBody DeliveryDetail deliveryDetail) {
        deliveryDetail.setA$user$(new Timestamp(System.currentTimeMillis()));
        deliveryDetailService.save(deliveryDetail);
        return deliveryDetail;
    }

    @DeleteMapping("/{id}")
    Optional<DeliveryDetail> delete(@PathVariable int id) {
        deliveryDetailService.deleteById(id);
        return read(id);
    }
}
