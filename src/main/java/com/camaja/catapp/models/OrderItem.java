package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_ORDER_PRODUCTS", schema="CATAPP")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_ORDER_PRODUCT")
    private Long id_order_product;

    @ManyToOne
    @JoinColumn(name="ID_ORDER")
    @JsonBackReference("order-items")
    private Order order;

    @Size(max=400, message="Description can't have more than 400 characters")
    @Column(name="DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name="ID_PRODUCT_PARAMETER")
    private ProductParameters product;

    @ManyToOne
    @JoinColumn(name="ID_PRINTING_SERVICE")
    private PrintingService printingService;

    @Column(name="QUANTITY")
    private Integer quantity;

    @Column(name="SERVICE_HOURS")
    private Integer serviceHours;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
