package com.camaja.catapp.mappers;

import com.camaja.catapp.dto.ProductParameterDTO;
import com.camaja.catapp.models.Product;
import com.camaja.catapp.models.ProductParameters;
import org.springframework.stereotype.Component;

@Component
public class ProductParameterMapper {
    public ProductParameterDTO toDto(ProductParameters parameter) {
        ProductParameterDTO parameterDTO = new ProductParameterDTO();
        Product product = parameter.getProduct();

        parameterDTO.setIdProduct(product.getIdProduct());
        parameterDTO.setCatalogReference(product.getCatalogReference());
        parameterDTO.setName(product.getName());
        parameterDTO.setBrand(product.getBrand());
        parameterDTO.setProductType(product.getProductType());
        parameterDTO.setDescription(product.getDescription());
        parameterDTO.setIdProductParameter(parameter.getIdProductParameter());
        parameterDTO.setPrice(parameter.getPrice());
        parameterDTO.setStore(parameter.getStore());
        parameterDTO.setSku(parameter.getSku());
        parameterDTO.setSize(parameter.getSize());
        parameterDTO.setColor(parameter.getColor());
        parameterDTO.setVat(parameter.getVat());
        parameterDTO.setImageUrl(parameter.getImageUrl());
        parameterDTO.setDefaultSku(parameter.getDefaultSku());
        parameterDTO.setFlagActive(parameter.getFlagActive());

        return parameterDTO;
    }
}
