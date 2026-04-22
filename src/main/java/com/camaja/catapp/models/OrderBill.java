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

    @ManyToOne
    @JoinColumn(name="BILL_ADDRESS")
    private Address bill_address;

    @Column(name = "VAT_RATE", precision = 5, scale = 4)
    private BigDecimal vatRate;

    @Column(name = "VAT_AMOUNT", precision = 19, scale = 4)
    private BigDecimal vatAmount;

    @Column(name = "EXPENSES_AMOUNT", precision = 19, scale = 4)
    private BigDecimal expensesAmount;

    @Column(name = "SERVICE_AMOUNT", precision = 19, scale = 4)
    private BigDecimal serviceAmount;

    @Column(name = "TOTAL_AMOUNT", precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
