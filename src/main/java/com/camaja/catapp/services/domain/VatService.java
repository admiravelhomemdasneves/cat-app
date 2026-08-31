package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.Vat;
import com.camaja.catapp.repository.VatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VatService {
    private final VatRepository vatRepository;

    public List<Vat> getAllVats() {
        return vatRepository.findAll();
    }

    public Vat getVatById(Long id) {
        return vatRepository.findById(id).orElse(null);
    }

    public void saveVat(Vat vat) {
        vatRepository.save(vat);
    }
}