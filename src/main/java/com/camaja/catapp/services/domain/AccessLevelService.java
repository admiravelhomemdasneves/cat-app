package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.AccessLevelRequest;
import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.repository.AccessLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccessLevelService {
    private final AccessLevelRepository accessLevelRepository;
    private final TenantAccess tenantAccess;

    public List<AccessLevel> getAllAccessLevels() {
        List<AccessLevel> all = new ArrayList<>();
        all.addAll(accessLevelRepository.findByCompanyIsNull());
        all.addAll(accessLevelRepository.findByCompany(tenantAccess.company()));
        return all.stream()
                .filter(al -> Boolean.TRUE.equals(al.getFlagActive()))
                .toList();
    }

    public AccessLevel getAccessLevelById(Long id) {
        return accessLevelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Access level not found: " + id));
    }

    public AccessLevel saveAccessLevel(AccessLevelRequest request) {
        if (accessLevelRepository.findByNameAndCompanyIsNull(request.getName()).isPresent()) {
            throw new IllegalArgumentException(
                    "A universal role with name '" + request.getName() + "' already exists");
        }
        Company currentCompany = tenantAccess.company();
        AccessLevel level;
        if (request.getIdAccessLevel() != null) {
            // Editing an existing level is only allowed for the current company's own levels.
            level = accessLevelRepository.findById(request.getIdAccessLevel())
                    .filter(al -> al.getCompany() != null
                            && al.getCompany().getIdCompany().equals(currentCompany.getIdCompany()))
                    .orElseThrow(() -> TenantAccess.notFound("Access level", request.getIdAccessLevel()));
        } else {
            level = new AccessLevel();
            level.setCompany(currentCompany);
        }
        level.setName(request.getName());
        level.setFlagActive(true);
        level.setA$date$(Timestamp.from(Instant.now()));
        return accessLevelRepository.save(level);
    }

    public void deleteAccessLevel(Long id) {
        AccessLevel level = accessLevelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Access level not found: " + id));
        if (level.getCompany() == null) {
            throw new IllegalArgumentException("Cannot delete a universal role");
        }
        Long currentCompanyId = tenantAccess.company().getIdCompany();
        if (!level.getCompany().getIdCompany().equals(currentCompanyId)) {
            throw new IllegalArgumentException("Access level does not belong to your company");
        }
        accessLevelRepository.deleteById(id);
    }
}
