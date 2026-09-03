package com.camaja.catapp.controllers;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.models.Material;
import com.camaja.catapp.models.MaterialImage;
import com.camaja.catapp.repository.MaterialImageRepository;
import com.camaja.catapp.repository.MaterialRepository;
import com.camaja.catapp.services.infrastructure.AzureBlobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/material")
@CrossOrigin
public class MaterialController {

    private final MaterialRepository materialRepository;
    private final MaterialImageRepository materialImageRepository;
    private final AzureBlobService azureBlobService;
    private final SecurityUtils securityUtils;

    @GetMapping
    List<Material> getActiveMaterials() {
        return materialRepository.findByCompanyAndFlagActive(securityUtils.getCurrentCompany(), true);
    }

    @GetMapping("/{id}")
    Optional<Material> read(@PathVariable Long id) {
        return materialRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Material inactivateMaterial(@PathVariable Long id) {
        Optional<Material> materialOptional = materialRepository.findById(id);
        if (materialOptional.isPresent()) {
            Material material = materialOptional.get();
            material.setFlagActive(false);
            material.setA$user$(securityUtils.getCurrentUser().getUsername());
            material.setA$date$(new Timestamp(System.currentTimeMillis()));
            materialRepository.save(material);
            return material;
        }
        return null;
    }

    @PostMapping
    Material save(@RequestBody Material material) {
        material.setCompany(securityUtils.getCurrentCompany());
        material.setA$user$(securityUtils.getCurrentUser().getUsername());
        material.setA$date$(new Timestamp(System.currentTimeMillis()));
        if (material.getTags() != null) material.getTags().forEach(t -> t.setMaterial(material));
        materialRepository.save(material);
        return material;
    }

    @PutMapping
    Material update(@RequestBody Material material) {
        material.setCompany(securityUtils.getCurrentCompany());
        material.setA$user$(securityUtils.getCurrentUser().getUsername());
        material.setA$date$(new Timestamp(System.currentTimeMillis()));
        if (material.getTags() != null) material.getTags().forEach(t -> t.setMaterial(material));
        materialRepository.save(material);
        return material;
    }

    @DeleteMapping("/image/{imageId}")
    ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        MaterialImage image = materialImageRepository.findById(imageId).orElseThrow();
        try {
            azureBlobService.deleteFile(image.getImageUrl());
        } catch (Exception e) {
            // blob may already be gone — still soft-delete the DB record
        }
        image.setFlagActive(false);
        materialImageRepository.save(image);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    ResponseEntity<MaterialImage> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Material material = materialRepository.findById(id).orElseThrow();
        String url = azureBlobService.uploadFile(file);
        MaterialImage image = new MaterialImage();
        image.setMaterial(material);
        image.setImageUrl(url);
        image.setFlagActive(true);
        return ResponseEntity.ok(materialImageRepository.save(image));
    }

    @DeleteMapping("/{id}")
    Optional<Material> delete(@PathVariable Long id) {
        materialRepository.deleteById(id);
        return read(id);
    }
}
