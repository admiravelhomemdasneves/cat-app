package com.camaja.catapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Writable material fields. Company, audit fields, price/tag IDs and images are never taken
 * from the client (images are managed through the upload/delete endpoints).
 */
@Getter
@Setter
public class MaterialRequest {
    private Long idMaterial;
    private String name;
    private String description;
    private String category;
    private String seller;
    private String brand;
    private Long parentId;
    private Price costPrice;
    private Price retailPrice;
    private List<Tag> tags;
    private Boolean flagActive;

    @Getter
    @Setter
    public static class Price {
        private BigDecimal priceValue;
        private String currency;
        private BigDecimal quantity;
        private String quantityMetric;
    }

    @Getter
    @Setter
    public static class Tag {
        private String tagName;
    }
}
