package com.camaja.catapp.services.domain;

import com.camaja.catapp.dto.ProductOptionsDTO;
import com.camaja.catapp.models.Product;
import com.camaja.catapp.models.ProductParameters;
import com.camaja.catapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired ProductRepository productRepository;

    public List<Product> getActiveProducts() {
        return productRepository.findAll().stream().filter(product -> Boolean.TRUE.equals(product.getFlagActive())).toList();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public List<ProductOptionsDTO> getProductOptions() {
        List<Product> products = getActiveProducts();
        List<ProductOptionsDTO> productOptions = new ArrayList<>();

        products.forEach(product -> {
            Optional<ProductParameters> defaultParameters = product.getParameters().stream()
                    .filter(parameter -> Boolean.TRUE.equals(parameter.getFlagActive())
                            && Boolean.TRUE.equals(parameter.getDefaultSku()))
                    .findFirst();

            ProductOptionsDTO productOptionsDTO = new ProductOptionsDTO();

            productOptionsDTO.setIdProduct(product.getIdProduct());
            productOptionsDTO.setCatalogReference(product.getCatalogReference());
            productOptionsDTO.setName(product.getName());
            productOptionsDTO.setBrand(product.getBrand());
            productOptionsDTO.setProductType(product.getProductType());
            productOptionsDTO.setDescription(product.getDescription());
            productOptionsDTO.setFlagActive(product.getFlagActive());

            if (defaultParameters.isPresent()) {
                ProductParameters defaultParameter = defaultParameters.get();
                productOptionsDTO.setIdProductParameterDefault(defaultParameter.getIdProductParameter());
                productOptionsDTO.setStoreDefault(defaultParameter.getStore());
                productOptionsDTO.setSkuDefault(defaultParameter.getSku());
                productOptionsDTO.setSizeDefault(defaultParameter.getSize());
                productOptionsDTO.setColorDefault(defaultParameter.getColor());
                productOptionsDTO.setPriceDefault(defaultParameter.getPrice());
                productOptionsDTO.setVatDefault(defaultParameter.getVat());
                productOptionsDTO.setImageUrlDefault(defaultParameter.getImageUrl());
            }

            productOptions.add(productOptionsDTO);
        });

        return productOptions;
    }
}