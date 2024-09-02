package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="CAT_PRINTING_DETAILS", schema="CATAPP")
public class PrintingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_PRINTING_DETAIL")
    private Long id_printing_detail;

    @ManyToOne
    @JoinColumn(name="ID_ORDER_PRODUCT")
    private OrderProduct orderProduct;

    @JsonIgnore
    @Column(name="A$DATE$")
    private Timestamp a$user$;
}
