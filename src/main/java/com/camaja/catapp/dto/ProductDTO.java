package com.camaja.catapp.dto;

import com.camaja.catapp.models.Store;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDTO {
    private String sku;
    private String name;
    private String brand;
    private Store store;
    private String productType;
    private String description;
    private List<ProductParametersDTO> productParametersList;
}