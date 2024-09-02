package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_CONTACTS", schema="CATAPP")
public class Contact {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_CONTACT")
    private Long id_contact;

    @Size(max=20, message="First name can't have more than 20 characters")
    @NotNull(message="First name cannot be blank.")
    @Column(name="FIRST_NAME")
    private String first_name;

    @Size(max=20, message="Last name can't have more than 20 characters")
    @Column(name="LAST_NAME")
    private String last_name;

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
