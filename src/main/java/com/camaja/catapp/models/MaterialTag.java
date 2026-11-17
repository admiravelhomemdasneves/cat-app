package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="CAT_MATERIAL_TAGS", schema="CATAPP")
public class MaterialTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_MATERIAL_TAG")
    private Long idMaterialTag;

    @JsonBackReference("material-tags")
    @ManyToOne
    @JoinColumn(name="ID_MATERIAL")
    private Material material;

    @Column(name="TAG_NAME")
    private String tagName;
}
