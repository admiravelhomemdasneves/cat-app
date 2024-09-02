package com.camaja.catapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="CAT_ORDER_PRODUCTS_SPECS", schema="CATAPP")
public class OrderProductSpecs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_ORDER_PRODUCT_SPECS")
    private Long id_order_product_specs;

    @ManyToOne
    @JoinColumn(name="ID_ORDER_PRODUCT")
    private OrderProduct orderProduct;

    @Column(name="QUANTITY")
    private int quantity;

    @Size(max=30, message="Size can't have more than 30 characters")
    @Column(name="SIZE")
    private String size;

    @Size(max=30, message="Color can't have more than 30 characters")
    @Column(name="COLOR")
    private String color;

    @Size(max=2)
    @Column(name="FLAG_STATUS")
    private String flagStatus;
}
