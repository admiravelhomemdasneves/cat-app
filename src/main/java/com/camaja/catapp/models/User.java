package com.camaja.catapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="CAT_USERS", schema="CATAPP")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(unique = true, nullable = false)
    private String password;

    @JsonIgnore
    @Column(name = "VERIFICATION_CODE")
    private String verificationCode;

    @JsonIgnore
    @Column(name = "VERIFICATION_EXPIRATION")
    private LocalDateTime verificationExpiration;

    private boolean enabled;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_COMPANY")
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ACCESS_LEVEL")
    private AccessLevel accessLevel;

    /**
     * Platform operator: the only one allowed to manage global values (company = NULL) and to list
     * every company. Not settable through any API; grant it directly in the database.
     */
    @JsonIgnore
    @Column(name = "FLAG_SUPER_ADMIN")
    private Boolean superAdmin;

    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        if (accessLevel != null) authorities.add(new SimpleGrantedAuthority(accessLevel.getName()));
        if (isSuperAdmin()) authorities.add(new SimpleGrantedAuthority(SUPER_ADMIN));
        return authorities;
    }

    public boolean isSuperAdmin() {
        return Boolean.TRUE.equals(superAdmin);
    }

    /** Every user belongs to a company. Only a SUPER_ADMIN (platform operator) may have none. */
    public boolean hasValidCompany() {
        return company != null || isSuperAdmin();
    }

    /** Last line of defence: a user without a company is never written to the database. */
    @PrePersist
    @PreUpdate
    void assertHasCompany() {
        if (!hasValidCompany()) {
            throw new IllegalStateException("A user must belong to a company");
        }
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}
