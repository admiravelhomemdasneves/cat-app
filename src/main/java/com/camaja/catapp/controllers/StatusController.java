package com.camaja.catapp.controllers;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.models.Status;
import com.camaja.catapp.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * Rows with company = NULL are global: visible to every company, changeable only by SUPER_ADMIN.
 * Rows of another company are never visible (404).
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/orderStatus")
@CrossOrigin
public class StatusController {
    private final StatusRepository repository;
    private final TenantAccess tenantAccess;

    @GetMapping
    List<Status> getActive() {
        return repository.findVisibleByCompany(tenantAccess.company());
    }

    @GetMapping("/{id}")
    Status read(@PathVariable Long id) {
        return repository.findVisible(id, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("Status", id));
    }

    @Transactional
    @GetMapping("/inactivate/{id}")
    Status inactivate(@PathVariable Long id) {
        Status existing = read(id);
        tenantAccess.assertCanModify(existing.getCompany());
        existing.setFlagActive(false);
        existing.setA$user$(new Timestamp(System.currentTimeMillis()));
        return repository.save(existing);
    }

    @Transactional
    @PostMapping
    Status save(@RequestBody Status input, @RequestParam(defaultValue = "false") boolean global) {
        Status target;
        if (input.getId_status() != null) {
            target = read(input.getId_status());
            tenantAccess.assertCanModify(target.getCompany());
        } else {
            target = new Status();
            // New values belong to the current company; ?global=true (SUPER_ADMIN only) creates a global one.
            target.setCompany(tenantAccess.companyForNewCatalogRow(global));
        }
        target.setName(input.getName());
        target.setDescription(input.getDescription());
        target.setColorCode(input.getColorCode());
        target.setTerminalStatus(input.getTerminalStatus());
        target.setFlagActive(input.getFlagActive() == null ? Boolean.TRUE : input.getFlagActive());
        target.setA$user$(new Timestamp(System.currentTimeMillis()));
        return repository.save(target);
    }

    @PutMapping
    Status update(@RequestBody Status input, @RequestParam(defaultValue = "false") boolean global) {
        return save(input, global);
    }
}
