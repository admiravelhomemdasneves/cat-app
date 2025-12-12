package com.camaja.catapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="CAT_COLOR", schema="CATAPP")
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_COLOR")
    private Long id_color;

    @Column(name = "COLOR_CODE")
    private String colorCode;

    @Column(name = "COLOR_NAME")
    private String colorName;
}
