package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.dto.PermissionRequestDTO;
import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.BackofficePage;
import com.camaja.catapp.models.RolePagePermission;
import com.camaja.catapp.repository.AccessLevelRepository;
import com.camaja.catapp.repository.BackofficePageRepository;
import com.camaja.catapp.repository.RolePagePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RolePagePermissionService {

    private final RolePagePermissionRepository rolePagePermissionRepository;
    private final AccessLevelRepository accessLevelRepository;
    private final BackofficePageRepository backofficePageRepository;
    private final SecurityUtils securityUtils;

    public List<RolePagePermission> getPermissionsForCurrentRole() {
        AccessLevel accessLevel = securityUtils.getCurrentUser().getAccessLevel();
        if (accessLevel == null) return List.of();
        return rolePagePermissionRepository.findByAccessLevel(accessLevel);
    }

    public List<RolePagePermission> getAllPermissions() {
        List<AccessLevel> relevant = new ArrayList<>();
        relevant.addAll(accessLevelRepository.findByCompanyIsNull());
        relevant.addAll(accessLevelRepository.findByCompany(securityUtils.getCurrentCompany()));
        return rolePagePermissionRepository.findByAccessLevelIn(relevant);
    }

    public RolePagePermission savePermission(PermissionRequestDTO dto) {
        AccessLevel accessLevel = accessLevelRepository.findById(dto.getAccessLevelId())
                .orElseThrow(() -> new IllegalArgumentException("Access level not found: " + dto.getAccessLevelId()));

        assertCompanyOwned(accessLevel);

        BackofficePage page = backofficePageRepository.findById(dto.getBackofficePageId())
                .orElseThrow(() -> new IllegalArgumentException("Backoffice page not found: " + dto.getBackofficePageId()));

        RolePagePermission permission = dto.getId() != null
                ? rolePagePermissionRepository.findById(dto.getId()).orElse(new RolePagePermission())
                : new RolePagePermission();

        permission.setAccessLevel(accessLevel);
        permission.setBackofficePage(page);
        permission.setCanView(dto.getCanView());
        permission.setCanCreate(dto.getCanCreate());
        permission.setCanEdit(dto.getCanEdit());
        permission.setCanDelete(dto.getCanDelete());
        permission.setA$date$(Timestamp.from(Instant.now()));

        return rolePagePermissionRepository.save(permission);
    }

    public void deletePermission(Long id) {
        RolePagePermission permission = rolePagePermissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + id));
        assertCompanyOwned(permission.getAccessLevel());
        rolePagePermissionRepository.deleteById(id);
    }

    private void assertCompanyOwned(AccessLevel level) {
        if (level.getCompany() == null) {
            throw new IllegalArgumentException("Cannot edit permissions for a universal role");
        }
        Long currentCompanyId = securityUtils.getCurrentCompany().getIdCompany();
        if (!level.getCompany().getIdCompany().equals(currentCompanyId)) {
            throw new IllegalArgumentException("Access level does not belong to your company");
        }
    }
}
