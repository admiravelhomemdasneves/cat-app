package com.camaja.catapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CAT_PRICE_QUANTITIES", schema = "CATAPP")
public class PriceQuantity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRICE_QUANTITY")
    private Long idPriceQuantity;

    @Column(name = "PRICE_VALUE", precision = 19, scale = 4)
    private BigDecimal priceValue;

    @Column(name = "CURRENCY", length = 3)
    private String currency;

    @Column(name = "QUANTITY", precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "QUANTITY_METRIC", length = 50)
    private String quantityMetric;
}
