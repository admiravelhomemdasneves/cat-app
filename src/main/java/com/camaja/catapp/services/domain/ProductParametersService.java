package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.ProductParameters;
import com.camaja.catapp.repository.ProductParameterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductParametersService {
    @Autowired ProductParameterRepository productParameterRepository;

    public List<ProductParameters> getAllProductParameters() {
        return productParameterRepository.findAll();
    }

    public List<ProductParameters> getProductParametersByProduct(Long idProduct) {
        return productParameterRepository.findByProductIdProduct(idProduct);
    }

    public ProductParameters getProductParameterById(Long id) {
        return productParameterRepository.findById(id.intValue()).orElse(null);
    }

    public void saveProductParameter(ProductParameters productParameter) {
        productParameterRepository.save(productParameter);
    }
}