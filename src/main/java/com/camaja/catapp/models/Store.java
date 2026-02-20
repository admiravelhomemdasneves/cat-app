package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_STORES_API", schema="CATAPP")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_STORES_API")
    private Long idStore;

    @Column(name="STORE_NAME")
    private String storeName;

    @Column(name="REQUEST_NAME")
    private String requestName;

    @Column(name="REQUEST_DESCRIPTION")
    private String requestDescription;

    @Column(name="REQUEST_METHOD")
    private String requestMethod;

    @Column(name="URL")
    private String url;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;
}
