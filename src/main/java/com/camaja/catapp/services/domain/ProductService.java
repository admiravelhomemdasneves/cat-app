package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.dto.ProductParameterDTO;
import com.camaja.catapp.mappers.ProductParameterMapper;
import com.camaja.catapp.models.Product;
import com.camaja.catapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductParameterMapper productParameterMapper;
    private final SecurityUtils securityUtils;

    public List<Product> getActiveProducts() {
        return productRepository.findByCompanyAndFlagActive(securityUtils.getCurrentCompany(), true);
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

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void saveProduct(Product product) {
        if (product.getCompany() == null) {
            product.setCompany(securityUtils.getCurrentCompany());
        }
        productRepository.save(product);
    }
}