package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_ORDER_ITEMS", schema="CATAPP")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_ORDER_ITEM")
    private Long idOrderItem;

    @ManyToOne
    @JoinColumn(name="ID_ORDER")
    @JsonBackReference("order-contents")
    private Order order;

    @Size(max=400)
    @Column(name="DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name="ID_PRINTING_SERVICE")
    private PrintingService service;

    @ManyToOne
    @JoinColumn(name="ID_MATERIAL")
    private Material material;

    @Column(name="QUANTITY", precision=19, scale=4)
    private BigDecimal quantity;

    @Column(name="SERVICE_RATE", precision=19, scale=4)
    private BigDecimal serviceRate;

    @Column(name="SERVICE_HOURS", precision=19, scale=4)
    private BigDecimal serviceHours;

    @Column(name="EXPENSES", precision=19, scale=4)
    private BigDecimal expenses;

    @Column(name="PRICE", precision=19, scale=4)
    private BigDecimal price;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;

    @JsonIgnore
    @Column(name="A$USER$")
    private String a$user$;
}
