package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Product;
import com.camaja.catapp.repository.ProductRepository;
import com.camaja.catapp.services.AzureBlobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    AzureBlobService azureBlobService;

    @GetMapping
    Iterable<Product> getActiveProducts() {
        List<Product> products = (List<Product>) productRepository.findAll();
        List<Product> activeProducts = products.stream().filter(product -> product.getFlagActive() == null || product.getFlagActive() == true).collect(Collectors.toList());
        return activeProducts;
    }

    @GetMapping("/{id}")
    Optional<Product> read(@PathVariable int id) {
        return productRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Product inactivateProduct(@PathVariable int id) {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setFlagActive(false);
            product.setA$date$(new Timestamp(System.currentTimeMillis()));
            productRepository.save(product);
            return product;
        } else {
            return null;
        }
    }

    @PostMapping
    Product save(@RequestBody Product product) {
        product.setA$date$(new Timestamp(System.currentTimeMillis()));
        productRepository.save(product);
        return product;
    }

    @PostMapping("/image")
    public ResponseEntity<?> save(
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {

        if (file != null && !file.isEmpty()) {
            String imageUrl = azureBlobService.uploadFile(file);
            product.setImageUrl(imageUrl);
        }

        productRepository.save(product);

        return ResponseEntity.ok().build();
    }

    @PutMapping
    Product update(@RequestBody Product product) {
        product.setA$date$(new Timestamp(System.currentTimeMillis()));
        productRepository.save(product);
        return product;
    }

    @DeleteMapping("/{id}")
    Optional<Product> delete(@PathVariable int id) {
        productRepository.deleteById(id);
        return read(id);
    }
}
