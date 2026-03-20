package com.camaja.catapp.services.domain;

import com.camaja.catapp.dto.ProductParameterDTO;
import com.camaja.catapp.mappers.ProductParameterMapper;
import com.camaja.catapp.models.Product;
import com.camaja.catapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired ProductRepository productRepository;
    @Autowired ProductParameterMapper productParameterMapper;

    public List<Product> getActiveProducts() {
        return productRepository.findAll().stream().filter(product -> Boolean.TRUE.equals(product.getFlagActive())).toList();
    }

    public List<ProductParameterDTO> getActiveProductsParametersDTO() {
        List<ProductParameterDTO> productParameters = new ArrayList<>();
        List<Product> products = getActiveProducts();

        products.forEach(product -> {
            product.getParameters().forEach(parameter -> {
                if (Boolean.TRUE.equals(parameter.getFlagActive())) {
                    ProductParameterDTO parameterDTO = productParameterMapper.toDto(parameter);
                    productParameters.add(parameterDTO);
                }
            });
        });

        return productParameters;
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }
}