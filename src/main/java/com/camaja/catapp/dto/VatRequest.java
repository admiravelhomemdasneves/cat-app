package com.camaja.catapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** Writable VAT fields. The company is always decided by the server. */
@Getter
@Setter
public class VatRequest {
    private Long idVat;
    private String country;
    private BigDecimal vatRate;
    private Boolean flagActive;
    /** Only honoured for SUPER_ADMIN on creation: makes the value visible to every company. */
    private Boolean global;
}
