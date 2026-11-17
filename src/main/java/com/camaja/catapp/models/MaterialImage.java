package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="CAT_MATERIAL_IMAGES", schema="CATAPP")
public class MaterialImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_MATERIAL_IMAGE")
    private Long idMaterialImage;

    @JsonBackReference("material-images")
    @ManyToOne
    @JoinColumn(name="ID_MATERIAL")
    private Material material;

    @Column(name="IMAGE_URL")
    private String imageUrl;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;
}
