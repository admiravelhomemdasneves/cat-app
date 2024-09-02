package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="CAT_ORDER_PRODUCTS", schema="CATAPP")
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_ORDER_PRODUCT")
    private Long id_order_product;

    @ManyToOne
    @JoinColumn(name="ID_ORDER")
    private Order order;

    @JsonIgnore
    @OneToMany(mappedBy="orderProduct")
    private List<OrderProductSpecs> orderProductSpecs;

    @Size(max=400, message="Description can't have more than 400 characters")
    @Column(name="DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name="ID_PRODUCT")
    private Product product;

    @ManyToOne
    @JoinColumn(name="ID_PRINTING_SERVICE")
    private PrintingService printingService;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
