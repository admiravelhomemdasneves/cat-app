package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_ADDRESSES", schema="CATAPP")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_ADDRESS")
    private Long id_address;

    @Column(name="STREET")
    private String street;

    @Column(name="DOOR_NUMBER")
    private String door_number;

    @Column(name="ZIP_CODE")
    private String zip_code;

    @Column(name="CITY")
    private String city;

    @Column(name="COUNTRY")
    private String country;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$user$;
}
