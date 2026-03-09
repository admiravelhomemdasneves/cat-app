package com.camaja.catapp.dto;

import com.camaja.catapp.models.Color;
import com.camaja.catapp.models.Price;
import com.camaja.catapp.models.Store;
import com.camaja.catapp.models.Vat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductOptionsDTO {
    private Long idProduct;
    private String catalogReference;
    private String name;
    private String brand;
    private String productType;
    private String description;
    private Boolean flagActive;
    private Long idProductParameterDefault;
    private Store storeDefault;
    private String skuDefault;
    private String sizeDefault;
    private Color colorDefault;
    private Price priceDefault;
    private Vat vatDefault;
    private String imageUrlDefault;
    private List<Color> colorOptions;
    private List<String> sizeOptions;
}
