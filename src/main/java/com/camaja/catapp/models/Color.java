package com.camaja.catapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="CAT_COLORS", schema="CATAPP")
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_COLOR")
    private Long idColor;

    @Column(name="COLOR_FUNCTION")
    private String colorFunction;

    @Column(name="COLOR_NAME")
    private String colorName;

    @Column(name="COLOR_CODE")
    private String colorCode;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;
}
