package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Vat;
import com.camaja.catapp.repository.VatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vat")
@CrossOrigin
public class VatController {
    private final VatRepository vatRepository;

    @GetMapping
    Iterable<Vat> getActiveVats() {
        List<Vat> vats = (List<Vat>) vatRepository.findAll();
        List<Vat> activeVats = vats.stream().filter(vat -> vat.getFlagActive() != null && vat.getFlagActive() == true).collect(Collectors.toList());
        return activeVats;
    }

    @GetMapping("/{id}")
    Optional<Vat> read(@PathVariable Long id) {
        return vatRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Vat inactivateVat(@PathVariable Long id) {
        Optional<Vat> vatOptional = vatRepository.findById(id);

        if (vatOptional.isPresent()) {
            Vat vat = vatOptional.get();
            vat.setFlagActive(false);
            vat.setA$date$(new Timestamp(System.currentTimeMillis()));
            vatRepository.save(vat);
            return vat;
        } else {
            return null;
        }
    }

    @PostMapping
    Vat save(@RequestBody Vat vat) {
        vat.setA$date$(new Timestamp(System.currentTimeMillis()));
        vatRepository.save(vat);
        return vat;
    }

    @PutMapping
    Vat update(@RequestBody Vat vat) {
        vat.setA$date$(new Timestamp(System.currentTimeMillis()));
        vatRepository.save(vat);
        return vat;
    }

    @DeleteMapping("/{id}")
    Optional<Vat> delete(@PathVariable Long id) {
        vatRepository.deleteById(id);
        return read(id);
    }
}
