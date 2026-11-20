package com.camaja.catapp.dto;

import lombok.Getter;
import lombok.Setter;

/** Writable access-level fields. The company and active flag are always decided by the server. */
@Getter
@Setter
public class AccessLevelRequest {
    private Long idAccessLevel;
    private String name;
}
