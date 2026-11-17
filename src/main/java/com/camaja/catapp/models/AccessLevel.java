package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(
    name = "CAT_ACCESS_LEVELS",
    schema = "CATAPP",
    uniqueConstraints = @UniqueConstraint(columnNames = {"NAME", "ID_COMPANY"})
)
public class AccessLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ACCESS_LEVEL")
    private Long idAccessLevel;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "FLAG_ACTIVE")
    private Boolean flagActive;

    // null = universal role (visible to all, read-only); non-null = company-specific role
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_COMPANY", nullable = true)
    private Company company;

    @JsonIgnore
    @Column(name = "A$DATE$")
    private Timestamp a$date$;
}
