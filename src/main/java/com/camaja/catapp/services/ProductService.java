package com.camaja.catapp.services;

import com.camaja.catapp.models.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductService extends CrudRepository<Product, Integer> {
}
