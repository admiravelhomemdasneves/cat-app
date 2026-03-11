package com.camaja.catapp.dto;

import com.camaja.catapp.models.Color;
import com.camaja.catapp.models.Price;
import com.camaja.catapp.models.Store;
import com.camaja.catapp.models.Vat;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ProductParameterDTO {
    private Long idProduct;
    private String catalogReference;
    private String name;
    private String brand;
    private String productType;
    private String description;
    private Long idProductParameter;
    private Store store;
    private String sku;
    private String size;
    private Color color;
    private Price price;
    private Vat vat;
    private String imageUrl;
    private Boolean defaultSku;
    private Boolean flagActive;
}
