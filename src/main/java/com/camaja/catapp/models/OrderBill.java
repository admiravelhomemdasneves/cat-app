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
@Table(name="CAT_ORDER_BILL", schema="CATAPP")
public class OrderBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_ORDER_BILL")
    private Long idOrderBill;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "ID_ORDER")
    private Order order;

    @Column(name = "BILL_ADDRESS", length = 500)
    private String bill_address;

    @Column(name = "VAT_RATE", precision = 5, scale = 4)
    private BigDecimal vatRate;

    @Column(name = "TOTAL_AMOUNT", precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name="NIF")
    private String nif;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
