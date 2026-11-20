package com.camaja.catapp.dto;

import lombok.Getter;
import lombok.Setter;

/** Writable backoffice-page fields. */
@Getter
@Setter
public class BackofficePageRequest {
    private Long id;
    private String name;
    private String path;
}
