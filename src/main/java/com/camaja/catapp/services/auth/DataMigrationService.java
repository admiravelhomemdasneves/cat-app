package com.camaja.catapp.services.auth;

import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.BackofficePage;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.RolePagePermission;
import com.camaja.catapp.repository.AccessLevelRepository;
import com.camaja.catapp.repository.BackofficePageRepository;
import com.camaja.catapp.repository.CompanyRepository;
import com.camaja.catapp.repository.RolePagePermissionRepository;
import com.camaja.catapp.repository.ContactRepository;
import com.camaja.catapp.repository.OrderRepository;
import com.camaja.catapp.repository.ProductRepository;
import com.camaja.catapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
@Profile("dev")
public class DataMigrationService {
    private final AccessLevelRepository accessLevelRepository;
    private final BackofficePageRepository backofficePageRepository;
    private final RolePagePermissionRepository rolePagePermissionRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;

    @PostConstruct
    @Transactional
    public void migrate() {
        log.info("Running dev data migration...");
        AccessLevel adminLevel = seedAccessLevel("ADMIN");
        seedAccessLevel("MANAGER");
        seedAccessLevel("EMPLOYEE");
        seedAccessLevel("GUEST");
        Company company = seedCompany("A Catarina Almeida");
        migrateExistingEntities(company, adminLevel);
        seedBackofficePages();
        seedAdminOnlyPagePermissions("/colaborators");
        seedAdminOnlyPagePermissions("/roles");
        log.info("Dev data migration complete.");
    }

    private AccessLevel seedAccessLevel(String name) {
        return accessLevelRepository.findByName(name).orElseGet(() -> {
            AccessLevel al = new AccessLevel();
            al.setName(name);
            al.setFlagActive(true);
            al.setA$date$(Timestamp.from(Instant.now()));
            log.info("Seeded AccessLevel: {}", name);
            return accessLevelRepository.save(al);
        });
    }

    private Company seedCompany(String name) {
        return companyRepository.findByName(name).orElseGet(() -> {
            Company c = new Company();
            c.setName(name);
            c.setFlagActive(true);
            c.setA$date$(Timestamp.from(Instant.now()));
            log.info("Seeded Company: {}", name);
            return companyRepository.save(c);
        });
    }

    private void seedBackofficePages() {
        seedPage("Orders", "/orders");
        seedPage("Contacts", "/contacts");
        seedPage("Products", "/products");
        seedPage("Statuses", "/orderStatus");
        seedPage("Priorities", "/orderPriority");
        seedPage("Printing Services", "/printingService");
        seedPage("Colaborators", "/colaborators");
        seedPage("Roles", "/roles");
    }

    private void seedAdminOnlyPagePermissions(String path) {
        BackofficePage page = backofficePageRepository.findByPath(path).orElse(null);
        if (page == null) return;
        for (String roleName : new String[]{"MANAGER", "EMPLOYEE", "GUEST"}) {
            accessLevelRepository.findByName(roleName).ifPresent(level ->
                rolePagePermissionRepository.findByAccessLevelAndBackofficePage(level, page).orElseGet(() -> {
                    RolePagePermission perm = new RolePagePermission();
                    perm.setAccessLevel(level);
                    perm.setBackofficePage(page);
                    perm.setCanView(false);
                    perm.setCanCreate(false);
                    perm.setCanEdit(false);
                    perm.setCanDelete(false);
                    perm.setA$date$(Timestamp.from(Instant.now()));
                    log.info("Seeded RolePagePermission: {} on {} → all denied", roleName, path);
                    return rolePagePermissionRepository.save(perm);
                })
            );
        }
    }

    private void seedPage(String name, String path) {
        backofficePageRepository.findByPath(path).orElseGet(() -> {
            BackofficePage page = new BackofficePage();
            page.setName(name);
            page.setPath(path);
            page.setA$date$(Timestamp.from(Instant.now()));
            log.info("Seeded BackofficePage: {} → {}", name, path);
            return backofficePageRepository.save(page);
        });
    }

    private void migrateExistingEntities(Company company, AccessLevel adminLevel) {
        orderRepository.findAll().stream()
                .filter(o -> o.getCompany() == null)
                .forEach(o -> { o.setCompany(company); orderRepository.save(o); });

        contactRepository.findAll().stream()
                .filter(c -> c.getCompany() == null)
                .forEach(c -> { c.setCompany(company); contactRepository.save(c); });

        productRepository.findAll().stream()
                .filter(p -> p.getCompany() == null)
                .forEach(p -> { p.setCompany(company); productRepository.save(p); });

        userRepository.findAll().stream()
                .filter(u -> u.getCompany() == null)
                .forEach(u -> {
                    u.setCompany(company);
                    u.setAccessLevel(adminLevel);
                    userRepository.save(u);
                });

        log.info("Migrated existing entities to company '{}'", company.getName());
    }
}
