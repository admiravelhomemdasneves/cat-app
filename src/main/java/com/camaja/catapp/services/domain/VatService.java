package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.Vat;
import com.camaja.catapp.repository.VatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VatService {
    @Autowired VatRepository vatRepository;

    public List<Vat> getAllVats() {
        return vatRepository.findAll();
    }

    public Vat getVatById(Long id) {
        return vatRepository.findById(id.intValue()).orElse(null);
    }

    public void saveVat(Vat vat) {
        vatRepository.save(vat);
    }
}