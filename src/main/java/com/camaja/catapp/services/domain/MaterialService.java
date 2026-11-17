package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.MaterialRequest;
import com.camaja.catapp.models.Material;
import com.camaja.catapp.models.MaterialImage;
import com.camaja.catapp.models.MaterialTag;
import com.camaja.catapp.models.PriceQuantity;
import com.camaja.catapp.repository.MaterialImageRepository;
import com.camaja.catapp.repository.MaterialRepository;
import com.camaja.catapp.services.infrastructure.AzureBlobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final MaterialImageRepository materialImageRepository;
    private final AzureBlobService azureBlobService;
    private final TenantAccess tenantAccess;
    private final SecurityUtils securityUtils;

    public List<Material> getActiveMaterials() {
        return materialRepository.findByCompanyAndFlagActive(tenantAccess.company(), true);
    }

    /** The material if it belongs to the current company, otherwise 404. */
    public Material getOwnedMaterial(Long id) {
        return materialRepository.findByIdMaterialAndCompany(id, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("Material", id));
    }

    @Transactional
    public Material saveMaterial(MaterialRequest request) {
        Material material;
        if (request.getIdMaterial() != null) {
            material = getOwnedMaterial(request.getIdMaterial());
        } else {
            material = new Material();
            material.setCompany(tenantAccess.company());
            material.setTags(new ArrayList<>());
        }
        if (request.getParentId() != null) {
            getOwnedMaterial(request.getParentId());
        }

        material.setName(request.getName());
        material.setDescription(request.getDescription());
        material.setCategory(request.getCategory());
        material.setSeller(request.getSeller());
        material.setBrand(request.getBrand());
        material.setParentId(request.getParentId());
        material.setFlagActive(request.getFlagActive() == null ? Boolean.TRUE : request.getFlagActive());
        material.setCostPrice(applyPrice(material.getCostPrice(), request.getCostPrice()));
        material.setRetailPrice(applyPrice(material.getRetailPrice(), request.getRetailPrice()));

        // Tags are always rebuilt from their names: client-supplied tag IDs are never trusted.
        if (material.getTags() == null) {
            material.setTags(new ArrayList<>());
        }
        material.getTags().clear();
        if (request.getTags() != null) {
            for (MaterialRequest.Tag t : request.getTags()) {
                if (t == null || t.getTagName() == null || t.getTagName().isBlank()) continue;
                MaterialTag tag = new MaterialTag();
                tag.setTagName(t.getTagName());
                tag.setMaterial(material);
                material.getTags().add(tag);
            }
        }

        audit(material);
        return materialRepository.save(material);
    }

    @Transactional
    public Material inactivateMaterial(Long id) {
        Material material = getOwnedMaterial(id);
        material.setFlagActive(false);
        audit(material);
        return materialRepository.save(material);
    }

    @Transactional
    public MaterialImage uploadImage(Long materialId, MultipartFile file) throws IOException {
        Material material = getOwnedMaterial(materialId);
        String url = azureBlobService.uploadFile(file);
        MaterialImage image = new MaterialImage();
        image.setMaterial(material);
        image.setImageUrl(url);
        image.setFlagActive(true);
        return materialImageRepository.save(image);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        MaterialImage image = materialImageRepository.findOwned(imageId, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("Image", imageId));
        try {
            azureBlobService.deleteFile(image.getImageUrl());
        } catch (Exception e) {
            // blob may already be gone, still soft-delete the DB record
            log.warn("Could not delete blob for image {}: {}", imageId, e.getMessage());
        }
        image.setFlagActive(false);
        materialImageRepository.save(image);
    }

    /** Updates the existing price row in place (its ID never comes from the client). */
    private static PriceQuantity applyPrice(PriceQuantity current, MaterialRequest.Price input) {
        if (input == null) {
            return null;
        }
        PriceQuantity price = current != null ? current : new PriceQuantity();
        price.setPriceValue(input.getPriceValue());
        price.setCurrency(input.getCurrency());
        price.setQuantity(input.getQuantity());
        price.setQuantityMetric(input.getQuantityMetric());
        return price;
    }

    private void audit(Material material) {
        material.setA$user$(securityUtils.getCurrentUser().getUsername());
        material.setA$date$(new Timestamp(System.currentTimeMillis()));
    }
}
