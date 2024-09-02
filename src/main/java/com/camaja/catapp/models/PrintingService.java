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
@Table(name="CAT_PRINTING_SERVICES", schema="CATAPP")
public class PrintingService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_PRINTING_SERVICE")
    private Long id_printing_service;

    @Size(max=40, message="Name can't have more than 40 characters")
    @Column(name="NAME")
    private String name;

    @Size(max=400, message="Description can't have more than 400 characters")
    @Column(name="DESCRIPTION")
    private String description;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$user$;
}
