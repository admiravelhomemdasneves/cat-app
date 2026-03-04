package com.camaja.catapp.controllers;

import com.camaja.catapp.models.ProductOLD;
import com.camaja.catapp.repository.ProductOLDRepository;
import com.camaja.catapp.services.infrastructure.AzureBlobService;
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
    ProductOLDRepository productOLDRepository;

    @Autowired
    AzureBlobService azureBlobService;

    @GetMapping
    Iterable<ProductOLD> getActiveProducts() {
        List<ProductOLD> productOLDS = (List<ProductOLD>) productOLDRepository.findAll();
        List<ProductOLD> activeProductOLDS = productOLDS.stream().filter(product -> product.getFlagActive() == null || product.getFlagActive() == true).collect(Collectors.toList());
        return activeProductOLDS;
    }

    @GetMapping("/{id}")
    Optional<ProductOLD> read(@PathVariable int id) {
        return productOLDRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    ProductOLD inactivateProduct(@PathVariable int id) {
        Optional<ProductOLD> productOptional = productOLDRepository.findById(id);

        if (productOptional.isPresent()) {
            ProductOLD productOLD = productOptional.get();
            productOLD.setFlagActive(false);
            productOLD.setA$date$(new Timestamp(System.currentTimeMillis()));
            productOLDRepository.save(productOLD);
            return productOLD;
        } else {
            return null;
        }
    }

    @PostMapping
    ProductOLD save(@RequestBody ProductOLD productOLD) {
        productOLD.setA$date$(new Timestamp(System.currentTimeMillis()));
        productOLDRepository.save(productOLD);
        return productOLD;
    }

    @PostMapping("/image")
    public ResponseEntity<?> save(
            @RequestPart("product") ProductOLD productOLD,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {

        if (file != null && !file.isEmpty()) {
            String imageUrl = azureBlobService.uploadFile(file);
            productOLD.setImageUrl(imageUrl);
        }

        productOLDRepository.save(productOLD);

        return ResponseEntity.ok().build();
    }

    @PutMapping
    ProductOLD update(@RequestBody ProductOLD productOLD) {
        productOLD.setA$date$(new Timestamp(System.currentTimeMillis()));
        productOLDRepository.save(productOLD);
        return productOLD;
    }

    @DeleteMapping("/{id}")
    Optional<ProductOLD> delete(@PathVariable int id) {
        productOLDRepository.deleteById(id);
        return read(id);
    }
}
