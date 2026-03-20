package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="CAT_PRODUCTS", schema="CATAPP")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_PRODUCT")
    private Long idProduct;

    @Column(name="CATALOG_REFERENCE")
    private String catalogReference;

    @Column(name="NAME")
    private String name;

    @Column(name="BRAND")
    private String brand;

    @Column(name="PRODUCT_TYPE")
    private String productType;

    @Column(name="DESCRIPTION")
    private String description;

    @OneToMany(mappedBy="product", cascade = CascadeType.ALL)
    @JsonManagedReference("product-parameters")
    private List<ProductParameters> parameters;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
