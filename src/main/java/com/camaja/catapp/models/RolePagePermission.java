package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "CAT_ROLE_PAGE_PERMISSIONS", schema = "CATAPP",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ID_ACCESS_LEVEL", "ID_BACKOFFICE_PAGE"}))
public class RolePagePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ACCESS_LEVEL", nullable = false)
    private AccessLevel accessLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_BACKOFFICE_PAGE", nullable = false)
    private BackofficePage backofficePage;

    @Column(name = "CAN_VIEW")
    private Boolean canView;

    @Column(name = "CAN_CREATE")
    private Boolean canCreate;

    @Column(name = "CAN_EDIT")
    private Boolean canEdit;

    @Column(name = "CAN_DELETE")
    private Boolean canDelete;

    @JsonIgnore
    @Column(name = "A$DATE$")
    private Timestamp a$date$;
}
