package com.camaja.catapp.configs;

import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    /** Username for the A$USER$ audit columns. */
    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    /**
     * Raw company of the current user: NULL only for a SUPER_ADMIN without a company.
     * To scope data use {@link TenantAccess#company()}, which rejects that case with 403.
     */
    public Company getCurrentCompany() {
        return getCurrentUser().getCompany();
    }

    public String getCurrentRole() {
        User user = getCurrentUser();
        return user.getAccessLevel() != null ? user.getAccessLevel().getName() : null;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(getCurrentRole());
    }

    /** Platform-wide operator (not a company role): manages global values. */
    public boolean isSuperAdmin() {
        return getCurrentUser().isSuperAdmin();
    }
}
