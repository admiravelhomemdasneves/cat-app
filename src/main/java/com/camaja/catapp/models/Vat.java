package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_VAT", schema="CATAPP")
public class Vat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_VAT")
    private Long idVat;

    @Column(name="COUNTRY")
    private String country;

    @Column(name="VAT_RATE")
    private BigDecimal vatRate;

    // null = global value, visible to every company
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPANY")
    private Company company;

    @Column(name="FLAG_ACTIVE")
    private Boolean flagActive;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$date$;

    @JsonProperty("global")
    public boolean isGlobal() {
        return company == null;
    }
}
