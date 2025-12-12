package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name="ID_STATUS")
    private Status status;

    @ManyToOne
    @JoinColumn(name="ID_CONTACT")
    private Contact contact;

    @ManyToOne
    @JoinColumn(name="ID_PRIORITY")
    private Priority priority;

    @OneToMany(mappedBy="order")
    @JsonManagedReference
    private List<OrderItem> orderItems;

    @Column(name="DATE_REQUESTED")
    private Timestamp date_requested;

    @Column(name="DATE_STATUS")
    private Timestamp date_status;

    @Column(name="DATE_CONCLUSION")
    private Timestamp date_conclusion;

    @Column(name="DATE_DUE")
    private Timestamp date_due;

    @ManyToOne
    @JoinColumn(name="DELIVERY_ADDRESS")
    private Address delivery_address;

    @ManyToOne
    @JoinColumn(name="BILL_ADDRESS")
    private Address bill_address;

    @ManyToOne
    @JoinColumn(name = "ID_PRICE_EXPENSE")
    private Price expensesPrice;

    @ManyToOne
    @JoinColumn(name = "ID_PRICE_SERVICE")
    private Price servicePrice;

    @ManyToOne
    @JoinColumn(name = "ID_PRICE_TOTAL")
    private Price totalPrice;

    @ManyToOne
    @JoinColumn(name = "ID_VAT")
    private Vat vat;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
