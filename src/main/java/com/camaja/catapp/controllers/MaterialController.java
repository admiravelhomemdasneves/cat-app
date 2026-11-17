package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.MaterialRequest;
import com.camaja.catapp.models.Material;
import com.camaja.catapp.models.MaterialImage;
import com.camaja.catapp.services.domain.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/** All access goes through MaterialService, which only exposes the current company's materials. */
@RequiredArgsConstructor
@RestController
@RequestMapping("/material")
@CrossOrigin
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    List<Material> getActiveMaterials() {
        return materialService.getActiveMaterials();
    }

    @GetMapping("/{id}")
    Material read(@PathVariable Long id) {
        return materialService.getOwnedMaterial(id);
    }

    @GetMapping("/inactivate/{id}")
    Material inactivateMaterial(@PathVariable Long id) {
        return materialService.inactivateMaterial(id);
    }

    @PostMapping
    Material save(@RequestBody MaterialRequest material) {
        return materialService.saveMaterial(material);
    }

    @PutMapping
    Material update(@RequestBody MaterialRequest material) {
        return materialService.saveMaterial(material);
    }

    @DeleteMapping("/image/{imageId}")
    ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        materialService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    ResponseEntity<MaterialImage> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(materialService.uploadImage(id, file));
    }
}
