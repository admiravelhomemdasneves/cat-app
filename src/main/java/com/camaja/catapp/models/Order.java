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
@Table(name="CAT_ORDERS", schema="CATAPP")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_ORDER")
    private Long id_order;

    @Size(max=40, message="Name can't have more than 40 characters")
    @Column(name="NAME")
    private String name;

    @Size(max=400, message="Description can't have more than 400 characters")
    @Column(name="DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name="ID_STATUS")
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name="ID_CONTACT")
    private Contact contact;

    @ManyToOne
    @JoinColumn(name="ID_PRIORITY")
    private OrderPriority orderPriority;

    @JsonIgnore
    @OneToMany(mappedBy="order")
    private List<OrderProduct> orderProducts;
/*
    @OneToOne
    @JsonIgnore
    @JoinColumn(name="ID_DELIVERY_DETAIL")
    private DeliveryDetail deliveryDetail;
*/
    @Column(name="DATE_REQUESTED")
    private Timestamp date_requested;

    @Column(name="DATE_STATUS")
    private Timestamp date_status;

    @Column(name="DATE_CONCLUSION")
    private Timestamp date_conclusion;

    @Column(name="DATE_DUE")
    private Timestamp date_due;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
