package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Product;
import com.camaja.catapp.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    Iterable<Product> read() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    Optional<Product> read(@PathVariable int id) {
        return productService.findById(id);
    }

    @PostMapping
    Product save(@RequestBody Product product) {
        product.setA$user$(new Timestamp(System.currentTimeMillis()));
        productService.save(product);
        return product;
    }

    @PutMapping
    Product update(@RequestBody Product product) {
        product.setA$user$(new Timestamp(System.currentTimeMillis()));
        productService.save(product);
        return product;
    }

    @DeleteMapping("/{id}")
    Optional<Product> delete(@PathVariable int id) {
        productService.deleteById(id);
        return read(id);
    }
}
