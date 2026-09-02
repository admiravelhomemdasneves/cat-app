package com.camaja.catapp.controllers;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.models.Material;
import com.camaja.catapp.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/material")
@CrossOrigin
public class MaterialController {

    private final MaterialRepository materialRepository;
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
        materialRepository.save(material);
        return material;
    }

    @PutMapping
    Material update(@RequestBody Material material) {
        material.setCompany(securityUtils.getCurrentCompany());
        material.setA$user$(securityUtils.getCurrentUser().getUsername());
        material.setA$date$(new Timestamp(System.currentTimeMillis()));
        materialRepository.save(material);
        return material;
    }

    @DeleteMapping("/{id}")
    Optional<Material> delete(@PathVariable Long id) {
        materialRepository.deleteById(id);
        return read(id);
    }
}
