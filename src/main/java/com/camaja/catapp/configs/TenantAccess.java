package com.camaja.catapp.configs;

import com.camaja.catapp.models.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Single place for the multi-company (tenant) access rules.
 *
 * <ul>
 *   <li>Company-owned data (orders, contacts, materials…) is only reachable through the
 *       repositories' {@code findOwned}/{@code ...AndCompany} methods, using {@link #company()}.</li>
 *   <li>Shared catalogs (VAT, stores, statuses, priorities, printing services) may also have
 *       {@code company = NULL} rows: global values every company can read, but only SUPER_ADMIN can change.</li>
 *   <li>A record from another company is reported as 404, so its existence is not leaked.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class TenantAccess {

    private final SecurityUtils securityUtils;

    public Company company() {
        Company company = securityUtils.getCurrentCompany();
        if (company == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not associated with a company");
        }
        return company;
    }

    public static ResponseStatusException notFound(String what, Object id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found: " + id);
    }

    /** Company for a new shared-catalog row: global (null) only when a SUPER_ADMIN explicitly asks for it. */
    public Company companyForNewCatalogRow(boolean global) {
        if (!global) {
            return company();
        }
        if (!securityUtils.isSuperAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only SUPER_ADMIN can create global values");
        }
        return null;
    }

    /** A catalog row already known to be visible can be changed by its company; global rows only by SUPER_ADMIN. */
    public void assertCanModify(Company rowCompany) {
        if (rowCompany == null) {
            if (!securityUtils.isSuperAdmin()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Global values can only be changed by SUPER_ADMIN");
            }
            return;
        }
        if (!rowCompany.getIdCompany().equals(company().getIdCompany())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }
}
