package com.camaja.catapp.dto;

import lombok.Getter;
import lombok.Setter;

/** Writable fields of the current user's company. The ID and active flag are never taken from the client. */
@Getter
@Setter
public class CompanyRequest {
    private String name;
    private String email;
    private String vatNumber;
    private String address;
}
