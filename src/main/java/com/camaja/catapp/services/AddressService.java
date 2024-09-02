package com.camaja.catapp.services;

import com.camaja.catapp.models.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressService extends CrudRepository<Address, Integer> {
}
