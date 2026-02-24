package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_PRODUCTS", schema="CATAPP")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_PRODUCT")
    private Long id_product;

    @Column(name="NAME")
    private String name;

    @Column(name="BRAND")
    private String brand;

    @Column(name="PRODUCT_TYPE")
    private String productType;

    @Column(name="SIZE")
    private String size;

    @Column(name="COLOR_CODE")
    private String color;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="SKU")
    private String sku;

    @Column(name="CATALOG_REFERENCE")
    private String catalogReference;

    @ManyToOne
    @JoinColumn(name="ID_STORE")
    private Store store;

    @ManyToOne
    @JoinColumn(name="ID_PRICE")
    private Price price;

    @ManyToOne
    @JoinColumn(name="ID_VAT")
    private Vat vat;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
