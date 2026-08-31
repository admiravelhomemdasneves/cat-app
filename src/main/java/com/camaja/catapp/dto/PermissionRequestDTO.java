package com.camaja.catapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequestDTO {
    private Long id;
    private Long accessLevelId;
    private Long backofficePageId;
    private Boolean canView;
    private Boolean canCreate;
    private Boolean canEdit;
    private Boolean canDelete;
}
