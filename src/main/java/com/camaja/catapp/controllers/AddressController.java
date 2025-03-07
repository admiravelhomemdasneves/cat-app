package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Address;
import com.camaja.catapp.models.Contact;
import com.camaja.catapp.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/address")
@CrossOrigin
public class AddressController {
    @Autowired
    AddressRepository addressRepository;

    @GetMapping
    Iterable<Address> getActiveAdresses() {
        List<Address> adresses = (List<Address>) addressRepository.findAll();
        List<Address> activeAdresses = adresses.stream().filter(address -> address.getFlagActive() != null && address.getFlagActive() == true).collect(Collectors.toList());
        return activeAdresses;
    }

    @GetMapping("/{id}")
    Optional<Address> read(@PathVariable int id) {
        return addressRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Address inactivateAddress(@PathVariable int id) {
        Optional<Address> addressOptional = addressRepository.findById(id);

        if (addressOptional.isPresent()) {
            Address address = addressOptional.get();
            address.setFlagActive(false);
            address.setA$date$(new Timestamp(System.currentTimeMillis()));
            addressRepository.save(address);
            return address;
        } else {
            return null;
        }
    }

    @PostMapping
    Address save(@RequestBody Address address) {
        address.setA$date$(new Timestamp(System.currentTimeMillis()));
        addressRepository.save(address);
        return address;
    }

    @PutMapping
    Address update(@RequestBody Address address) {
        address.setA$date$(new Timestamp(System.currentTimeMillis()));
        addressRepository.save(address);
        return address;
    }

    @DeleteMapping("/{id}")
    Optional<Address> delete(@PathVariable int id) {
        addressRepository.deleteById(id);
        return read(id);
    }
}
