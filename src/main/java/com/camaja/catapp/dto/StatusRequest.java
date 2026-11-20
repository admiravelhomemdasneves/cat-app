package com.camaja.catapp.dto;

import lombok.Getter;
import lombok.Setter;

/** Writable order-status fields. The company is always decided by the server. */
@Getter
@Setter
public class StatusRequest {
    private Long id_status;
    private String name;
    private String description;
    private String colorCode;
    private Boolean terminalStatus;
    private Boolean flagActive;
}
