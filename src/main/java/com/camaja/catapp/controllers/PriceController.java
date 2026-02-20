package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Price;
import com.camaja.catapp.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/price")
@CrossOrigin
public class PriceController {
    @Autowired
    PriceRepository priceRepository;

    @GetMapping
    Iterable<Price> getPrices() {
        List<Price> prices = (List<Price>) priceRepository.findAll();
        return prices;
    }

    @GetMapping("/{id}")
    Optional<Price> read(@PathVariable int id) {
        return priceRepository.findById(id);
    }

    @PostMapping
    Price save(@RequestBody Price price) {
        price.setA$date$(new Timestamp(System.currentTimeMillis()));
        priceRepository.save(price);
        return price;
    }

    @PutMapping
    Price update(@RequestBody Price price) {
        price.setA$date$(new Timestamp(System.currentTimeMillis()));
        priceRepository.save(price);
        return price;
    }
}
