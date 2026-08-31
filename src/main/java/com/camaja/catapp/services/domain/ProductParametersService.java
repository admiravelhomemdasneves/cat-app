package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.ProductParameters;
import com.camaja.catapp.repository.ProductParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductParametersService {
    private final ProductParameterRepository productParameterRepository;

    public List<ProductParameters> getAllProductParameters() {
        return productParameterRepository.findAll();
    }

    public List<ProductParameters> getProductParametersByProduct(Long idProduct) {
        return productParameterRepository.findByProductIdProduct(idProduct);
    }

    public ProductParameters getProductParameterById(Long id) {
        return productParameterRepository.findById(id).orElse(null);
    }

    public void saveProductParameter(ProductParameters productParameter) {
        productParameterRepository.save(productParameter);
    }
}