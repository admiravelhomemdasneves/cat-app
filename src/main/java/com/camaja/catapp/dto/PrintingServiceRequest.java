package com.camaja.catapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** Writable printing-service fields. The company is always decided by the server. */
@Getter
@Setter
public class PrintingServiceRequest {
    private Long id_printing_service;
    private String name;
    private String description;
    private BigDecimal rate;
    private String rateType;
    private Boolean flagActive;
}
