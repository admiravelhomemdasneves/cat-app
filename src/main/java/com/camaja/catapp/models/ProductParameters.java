package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_PRODUCT_PARAMETERS", schema="CATAPP")
public class ProductParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_PRODUCT_PARAMETER")
    private Long idProductParameter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PRODUCT")
    @JsonBackReference("product-parameters")
    private Product product;

    @ManyToOne
    @JoinColumn(name="ID_STORE")
    private Store store;

    @Column(name="SKU")
    private String sku;

    @Column(name="SIZE")
    private String size;

    @ManyToOne
    @JoinColumn(name="COLOR")
    private Color color;

    @Column(name = "PRICE", precision = 19, scale = 4)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name="ID_VAT")
    private Vat vat;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "DEFAULT_SKU")
    private Boolean defaultSku;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
