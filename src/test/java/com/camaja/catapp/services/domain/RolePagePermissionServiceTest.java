package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.dto.PermissionRequestDTO;
import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.BackofficePage;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.RolePagePermission;
import com.camaja.catapp.models.User;
import com.camaja.catapp.repository.AccessLevelRepository;
import com.camaja.catapp.repository.BackofficePageRepository;
import com.camaja.catapp.repository.RolePagePermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolePagePermissionServiceTest {

    @Mock RolePagePermissionRepository rolePagePermissionRepository;
    @Mock AccessLevelRepository accessLevelRepository;
    @Mock BackofficePageRepository backofficePageRepository;
    @Mock SecurityUtils securityUtils;

    @InjectMocks RolePagePermissionService rolePagePermissionService;

    Company companyA;
    Company companyB;

    @BeforeEach
    void setUp() {
        companyA = company(1L, "Company A");
        companyB = company(2L, "Company B");
    }

    // ─── getPermissionsForCurrentRole ─────────────────────────────────────────

    @Test
    void getPermissionsForCurrentRole_withNullAccessLevel_returnsEmptyList() {
        User user = new User("u", "u@e.com", "pwd");
        user.setAccessLevel(null);
        when(securityUtils.getCurrentUser()).thenReturn(user);

        List<RolePagePermission> result = rolePagePermissionService.getPermissionsForCurrentRole();

        assertThat(result).isEmpty();
        verify(rolePagePermissionRepository, never()).findByAccessLevel(any());
    }

    @Test
    void getPermissionsForCurrentRole_withValidAccessLevel_delegatesToRepository() {
        AccessLevel level = new AccessLevel();
        User user = new User("u", "u@e.com", "pwd");
        user.setAccessLevel(level);
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(rolePagePermissionRepository.findByAccessLevel(level))
                .thenReturn(List.of(new RolePagePermission(), new RolePagePermission()));

        List<RolePagePermission> result = rolePagePermissionService.getPermissionsForCurrentRole();

        assertThat(result).hasSize(2);
    }

    // ─── getAllPermissions ────────────────────────────────────────────────────

    @Test
    void getAllPermissions_returnsUniversalAndCompanyRolePermissions() {
        AccessLevel universal = levelWithCompany(null);
        AccessLevel myRole    = levelWithCompany(companyA);
        when(accessLevelRepository.findByCompanyIsNull()).thenReturn(List.of(universal));
        when(accessLevelRepository.findByCompany(companyA)).thenReturn(List.of(myRole));
        when(securityUtils.getCurrentCompany()).thenReturn(companyA);
        when(rolePagePermissionRepository.findByAccessLevelIn(any())).thenReturn(List.of(new RolePagePermission()));

        rolePagePermissionService.getAllPermissions();

        verify(rolePagePermissionRepository).findByAccessLevelIn(argThat(list ->
                list.contains(universal) && list.contains(myRole)));
    }

    // ─── savePermission ───────────────────────────────────────────────────────

    @Test
    void savePermission_withNullId_createsNewPermission() {
        AccessLevel level = levelWithCompany(companyA);
        BackofficePage page = new BackofficePage();
        when(accessLevelRepository.findById(1L)).thenReturn(Optional.of(level));
        when(backofficePageRepository.findById(2L)).thenReturn(Optional.of(page));
        when(securityUtils.getCurrentCompany()).thenReturn(companyA);
        when(rolePagePermissionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PermissionRequestDTO dto = dto(null, 1L, 2L);
        rolePagePermissionService.savePermission(dto);

        verify(rolePagePermissionRepository, never()).findById(any());
        verify(rolePagePermissionRepository).save(any(RolePagePermission.class));
    }

    @Test
    void savePermission_withExistingId_fetchesAndUpdatesExistingEntity() {
        RolePagePermission existing = new RolePagePermission();
        AccessLevel level = levelWithCompany(companyA);
        BackofficePage page = new BackofficePage();
        when(accessLevelRepository.findById(1L)).thenReturn(Optional.of(level));
        when(backofficePageRepository.findById(2L)).thenReturn(Optional.of(page));
        when(securityUtils.getCurrentCompany()).thenReturn(companyA);
        when(rolePagePermissionRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(rolePagePermissionRepository.save(any())).thenReturn(existing);

        PermissionRequestDTO dto = dto(42L, 1L, 2L);
        rolePagePermissionService.savePermission(dto);

        verify(rolePagePermissionRepository).findById(42L);
        verify(rolePagePermissionRepository).save(existing);
    }

    @Test
    void savePermission_withInvalidAccessLevelId_throwsIllegalArgumentException() {
        when(accessLevelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rolePagePermissionService.savePermission(dto(null, 99L, 1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Access level not found");
    }

    @Test
    void savePermission_withInvalidBackofficePageId_throwsIllegalArgumentException() {
        AccessLevel level = levelWithCompany(companyA);
        when(accessLevelRepository.findById(1L)).thenReturn(Optional.of(level));
        when(backofficePageRepository.findById(99L)).thenReturn(Optional.empty());
        when(securityUtils.getCurrentCompany()).thenReturn(companyA);

        assertThatThrownBy(() -> rolePagePermissionService.savePermission(dto(null, 1L, 99L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Backoffice page not found");
    }

    @Test
    void savePermission_withUniversalRole_throwsIllegalArgumentException() {
        AccessLevel universal = levelWithCompany(null);
        when(accessLevelRepository.findById(1L)).thenReturn(Optional.of(universal));

        assertThatThrownBy(() -> rolePagePermissionService.savePermission(dto(null, 1L, 2L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("universal role");
    }

    @Test
    void savePermission_withAnotherCompanysRole_throwsIllegalArgumentException() {
        AccessLevel theirRole = levelWithCompany(companyB);
        when(accessLevelRepository.findById(1L)).thenReturn(Optional.of(theirRole));
        when(securityUtils.getCurrentCompany()).thenReturn(companyA);

        assertThatThrownBy(() -> rolePagePermissionService.savePermission(dto(null, 1L, 2L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to your company");
    }

    // ─── deletePermission ─────────────────────────────────────────────────────

    @Test
    void deletePermission_ownRole_callsDeleteById() {
        AccessLevel myRole = levelWithCompany(companyA);
        RolePagePermission perm = new RolePagePermission();
        perm.setAccessLevel(myRole);
        when(rolePagePermissionRepository.findById(7L)).thenReturn(Optional.of(perm));
        when(securityUtils.getCurrentCompany()).thenReturn(companyA);

        rolePagePermissionService.deletePermission(7L);

        verify(rolePagePermissionRepository).deleteById(7L);
    }

    @Test
    void deletePermission_universalRole_throwsIllegalArgumentException() {
        AccessLevel universal = levelWithCompany(null);
        RolePagePermission perm = new RolePagePermission();
        perm.setAccessLevel(universal);
        when(rolePagePermissionRepository.findById(7L)).thenReturn(Optional.of(perm));

        assertThatThrownBy(() -> rolePagePermissionService.deletePermission(7L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("universal role");
    }

    @Test
    void deletePermission_anotherCompanysRole_throwsIllegalArgumentException() {
        AccessLevel theirRole = levelWithCompany(companyB);
        RolePagePermission perm = new RolePagePermission();
        perm.setAccessLevel(theirRole);
        when(rolePagePermissionRepository.findById(7L)).thenReturn(Optional.of(perm));
        when(securityUtils.getCurrentCompany()).thenReturn(companyA);

        assertThatThrownBy(() -> rolePagePermissionService.deletePermission(7L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to your company");
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private AccessLevel levelWithCompany(Company company) {
        AccessLevel al = new AccessLevel();
        al.setName("Role");
        al.setFlagActive(true);
        al.setCompany(company);
        return al;
    }

    private PermissionRequestDTO dto(Long id, Long accessLevelId, Long pageId) {
        PermissionRequestDTO dto = new PermissionRequestDTO();
        dto.setId(id);
        dto.setAccessLevelId(accessLevelId);
        dto.setBackofficePageId(pageId);
        dto.setCanView(true);
        dto.setCanCreate(false);
        dto.setCanEdit(false);
        dto.setCanDelete(false);
        return dto;
    }

    private Company company(Long id, String name) {
        Company c = new Company();
        ReflectionTestUtils.setField(c, "idCompany", id);
        c.setName(name);
        return c;
    }
}
