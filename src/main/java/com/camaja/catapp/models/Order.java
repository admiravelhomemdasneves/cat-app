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
    @JsonManagedReference("order-items")
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

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderBill orderBill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPANY")
    private Company company;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
