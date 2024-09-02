package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Address;
import com.camaja.catapp.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/address")
@CrossOrigin
public class AddressController {
    @Autowired
    AddressService addressService;

    @GetMapping
    Iterable<Address> read() {
        return addressService.findAll();
    }

    @GetMapping("/{id}")
    Optional<Address> read(@PathVariable int id) {
        return addressService.findById(id);
    }

    @PostMapping
    Address save(@RequestBody Address address) {
        address.setA$user$(new Timestamp(System.currentTimeMillis()));
        addressService.save(address);
        return address;
    }

    @PutMapping
    Address update(@RequestBody Address address) {
        address.setA$user$(new Timestamp(System.currentTimeMillis()));
        addressService.save(address);
        return address;
    }

    @DeleteMapping("/{id}")
    Optional<Address> delete(@PathVariable int id) {
        addressService.deleteById(id);
        return read(id);
    }
}
