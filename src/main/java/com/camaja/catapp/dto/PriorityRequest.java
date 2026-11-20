package com.camaja.catapp.dto;

import lombok.Getter;
import lombok.Setter;

/** Writable order-priority fields. The company is always decided by the server. */
@Getter
@Setter
public class PriorityRequest {
    private Long id_priority;
    private String name;
    private String description;
    private String colorCode;
    private Boolean flagActive;
}
