package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.Price;
import com.camaja.catapp.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceService {
    @Autowired PriceRepository priceRepository;

    public List<Price> getAllPrices() {
        return priceRepository.findAll();
    }

    public Price getPriceById(Long id) {
        return priceRepository.findById(id.intValue()).orElse(null);
    }

    public void savePrice(Price price) {
        priceRepository.save(price);
    }
}