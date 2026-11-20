package com.camaja.catapp.controllers;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.PriorityRequest;
import com.camaja.catapp.models.Priority;
import com.camaja.catapp.repository.PriorityRepository;
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
@RequestMapping("/orderPriority")
@CrossOrigin
public class PriorityController {
    private final PriorityRepository repository;
    private final TenantAccess tenantAccess;

    @GetMapping
    List<Priority> getActive() {
        return repository.findVisibleByCompany(tenantAccess.company());
    }

    @GetMapping("/{id}")
    Priority read(@PathVariable Long id) {
        return repository.findVisible(id, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("Priority", id));
    }

    @Transactional
    @GetMapping("/inactivate/{id}")
    Priority inactivate(@PathVariable Long id) {
        Priority existing = read(id);
        tenantAccess.assertCanModify(existing.getCompany());
        existing.setFlagActive(false);
        existing.setA$date$(new Timestamp(System.currentTimeMillis()));
        return repository.save(existing);
    }

    @Transactional
    @PostMapping
    Priority save(@RequestBody PriorityRequest input, @RequestParam(defaultValue = "false") boolean global) {
        Priority target;
        if (input.getId_priority() != null) {
            target = read(input.getId_priority());
            tenantAccess.assertCanModify(target.getCompany());
        } else {
            target = new Priority();
            // New values belong to the current company; ?global=true (SUPER_ADMIN only) creates a global one.
            target.setCompany(tenantAccess.companyForNewCatalogRow(global));
        }
        target.setName(input.getName());
        target.setDescription(input.getDescription());
        target.setColorCode(input.getColorCode());
        target.setFlagActive(input.getFlagActive() == null ? Boolean.TRUE : input.getFlagActive());
        target.setA$date$(new Timestamp(System.currentTimeMillis()));
        return repository.save(target);
    }

    @PutMapping
    Priority update(@RequestBody PriorityRequest input, @RequestParam(defaultValue = "false") boolean global) {
        return save(input, global);
    }
}
