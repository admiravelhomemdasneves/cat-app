package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="CAT_CONTACTS", schema="CATAPP")
public class Contact {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_CONTACT")
    private Long idContact;

    @Column(name="FIRST_NAME")
    private String firstName;

    @Column(name="LAST_NAME")
    private String lastName;

    @Column(name="NIF")
    private String nif;

    @Column(name="PHONE_NUMBER")
    private String phoneNumber;

    @Column(name="EMAIL")
    private String email;

    @OneToMany(mappedBy="contact", cascade = CascadeType.ALL)
    @JsonManagedReference("contact-addresses")
    private List<Address> addresses;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
