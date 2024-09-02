package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_DELIVERY_DETAILS", schema="CATAPP")
public class DeliveryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_DELIVERY_DETAIL")
    private Long id_delivery_detail;

    @Size(max=400, message="Description can't have more than 400 characters")
    @Column(name="DESCRIPTION")
    private String description;
/*
    @JsonIgnore
    @OneToOne(mappedBy="deliveryDetail")
    private Order order;
*/
    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$user$;
}
