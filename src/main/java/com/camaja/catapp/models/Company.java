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
@Table(name = "CAT_COMPANIES", schema = "CATAPP")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMPANY")
    private Long idCompany;

    @Size(max = 100)
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Size(max = 100)
    @Column(name = "EMAIL")
    private String email;

    @Size(max = 50)
    @Column(name = "VAT_NUMBER")
    private String vatNumber;

    @Size(max = 255)
    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name = "A$DATE$")
    private Timestamp a$date$;
}
