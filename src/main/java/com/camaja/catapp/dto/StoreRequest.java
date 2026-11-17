package com.camaja.catapp.dto;

import lombok.Getter;
import lombok.Setter;

/** Writable store fields. The company is always decided by the server. */
@Getter
@Setter
public class StoreRequest {
    private Long idStore;
    private String storeName;
    private String requestName;
    private String requestDescription;
    private String requestMethod;
    private String url;
    private Boolean flagActive;
    /** Only honoured for SUPER_ADMIN on creation: makes the value visible to every company. */
    private Boolean global;
}
