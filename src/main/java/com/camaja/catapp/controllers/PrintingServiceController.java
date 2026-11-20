package com.camaja.catapp.controllers;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.PrintingServiceRequest;
import com.camaja.catapp.models.PrintingService;
import com.camaja.catapp.repository.PrintingServiceRepository;
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
@RequestMapping("/printingService")
@CrossOrigin
public class PrintingServiceController {
    private final PrintingServiceRepository repository;
    private final TenantAccess tenantAccess;

    @GetMapping
    List<PrintingService> getActive() {
        return repository.findVisibleByCompany(tenantAccess.company());
    }

    @GetMapping("/{id}")
    PrintingService read(@PathVariable Long id) {
        return repository.findVisible(id, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("PrintingService", id));
    }

    @Transactional
    @GetMapping("/inactivate/{id}")
    PrintingService inactivate(@PathVariable Long id) {
        PrintingService existing = read(id);
        tenantAccess.assertCanModify(existing.getCompany());
        existing.setFlagActive(false);
        existing.setA$date$(new Timestamp(System.currentTimeMillis()));
        return repository.save(existing);
    }

    @Transactional
    @PostMapping
    PrintingService save(@RequestBody PrintingServiceRequest input, @RequestParam(defaultValue = "false") boolean global) {
        PrintingService target;
        if (input.getId_printing_service() != null) {
            target = read(input.getId_printing_service());
            tenantAccess.assertCanModify(target.getCompany());
        } else {
            target = new PrintingService();
            // New values belong to the current company; ?global=true (SUPER_ADMIN only) creates a global one.
            target.setCompany(tenantAccess.companyForNewCatalogRow(global));
        }
        target.setName(input.getName());
        target.setDescription(input.getDescription());
        target.setRate(input.getRate());
        target.setRateType(input.getRateType());
        target.setFlagActive(input.getFlagActive() == null ? Boolean.TRUE : input.getFlagActive());
        target.setA$date$(new Timestamp(System.currentTimeMillis()));
        return repository.save(target);
    }

    @PutMapping
    PrintingService update(@RequestBody PrintingServiceRequest input, @RequestParam(defaultValue = "false") boolean global) {
        return save(input, global);
    }
}
