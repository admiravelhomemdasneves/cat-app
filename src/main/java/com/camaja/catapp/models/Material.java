package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="CAT_MATERIALS", schema="CATAPP")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_MATERIAL")
    private Long idMaterial;

    @Size(max=100, message="Name can't have more than 100 characters")
    @Column(name="NAME")
    private String name;

    @Size(max=500, message="Description can't have more than 500 characters")
    @Column(name="DESCRIPTION")
    private String description;

    @Size(max=100, message="Category can't have more than 100 characters")
    @Column(name="CATEGORY")
    private String category;

    @Size(max=200, message="Seller can't have more than 200 characters")
    @Column(name="SELLER")
    private String seller;

    @Size(max=200, message="Brand can't have more than 200 characters")
    @Column(name="BRAND")
    private String brand;

    @Column(name="PARENT_ID")
    private Long parentId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "COST_PRICE_ID")
    private PriceQuantity costPrice;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RETAIL_PRICE_ID")
    private PriceQuantity retailPrice;

    @JsonManagedReference("material-images")
    @OneToMany(mappedBy="material", cascade=CascadeType.ALL)
    @SQLRestriction("FLAG_ACTIVE = 1")
    private List<MaterialImage> images;

    @JsonManagedReference("material-tags")
    @OneToMany(mappedBy="material", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<MaterialTag> tags;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_COMPANY")
    private Company company;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$USER$")
    private String a$user$;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
