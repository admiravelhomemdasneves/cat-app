package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_PRICE", schema="CATAPP")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_PRICE")
    private Long idPrice;

    @Column(name="PRICE")
    private BigDecimal price;

    @Column(name="QUANTITY")
    private Integer quantity;

    @Column(name="CURRENCY")
    private String currency;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
