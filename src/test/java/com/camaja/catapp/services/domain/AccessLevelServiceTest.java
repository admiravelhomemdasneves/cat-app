package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.AccessLevelRequest;
import com.camaja.catapp.models.AccessLevel;
import org.springframework.test.util.ReflectionTestUtils;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.repository.AccessLevelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessLevelServiceTest {

    @Mock AccessLevelRepository accessLevelRepository;
    @Mock TenantAccess tenantAccess;

    @InjectMocks AccessLevelService accessLevelService;

    Company companyA;
    Company companyB;

    @BeforeEach
    void setUp() {
        companyA = company(1L, "Company A");
        companyB = company(2L, "Company B");
    }

    // ─── getAllAccessLevels ────────────────────────────────────────────────────

    @Test
    void getAllAccessLevels_returnsUniversalPlusCurrentCompanyOnly() {
        AccessLevel universal = level("ADMIN", true, null);
        AccessLevel myRole   = level("Dev Manager", true, companyA);
        AccessLevel theirRole = level("Other Role", true, companyB);

        when(accessLevelRepository.findByCompanyIsNull()).thenReturn(List.of(universal));
        when(accessLevelRepository.findByCompany(companyA)).thenReturn(List.of(myRole));
        when(tenantAccess.company()).thenReturn(companyA);

        List<AccessLevel> result = accessLevelService.getAllAccessLevels();

        assertThat(result).containsExactlyInAnyOrder(universal, myRole);
        assertThat(result).doesNotContain(theirRole);
    }

    @Test
    void getAllAccessLevels_excludesInactiveRoles() {
        AccessLevel active   = level("ADMIN", true, null);
        AccessLevel inactive = level("GUEST", false, null);
        AccessLevel nullFlag = level("EMPLOYEE", null, null);

        when(accessLevelRepository.findByCompanyIsNull()).thenReturn(List.of(active, inactive, nullFlag));
        when(accessLevelRepository.findByCompany(any())).thenReturn(List.of());
        when(tenantAccess.company()).thenReturn(companyA);

        List<AccessLevel> result = accessLevelService.getAllAccessLevels();

        assertThat(result).containsExactly(active);
    }

    // ─── getAccessLevelById ───────────────────────────────────────────────────

    @Test
    void getAccessLevelById_whenNotFound_throwsIllegalArgumentException() {
        when(accessLevelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accessLevelService.getAccessLevelById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Access level not found");
    }

    // ─── saveAccessLevel ──────────────────────────────────────────────────────

    @Test
    void saveAccessLevel_setsCurrentCompanyAndSaves() {
        when(tenantAccess.company()).thenReturn(companyA);
        when(accessLevelRepository.findByNameAndCompanyIsNull("Dev Manager")).thenReturn(Optional.empty());
        when(accessLevelRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccessLevelRequest input = new AccessLevelRequest();
        input.setName("Dev Manager");

        AccessLevel saved = accessLevelService.saveAccessLevel(input);

        ArgumentCaptor<AccessLevel> captor = ArgumentCaptor.forClass(AccessLevel.class);
        verify(accessLevelRepository).save(captor.capture());
        assertThat(captor.getValue().getCompany()).isEqualTo(companyA);
        assertThat(saved.getFlagActive()).isTrue();
    }

    @Test
    void saveAccessLevel_existingOwnRole_onlyRenamesAndKeepsCompany() {
        AccessLevel myRole = level("Old Name", false, companyA);
        ReflectionTestUtils.setField(myRole, "idAccessLevel", 7L);
        when(tenantAccess.company()).thenReturn(companyA);
        when(accessLevelRepository.findByNameAndCompanyIsNull("New Name")).thenReturn(Optional.empty());
        when(accessLevelRepository.findById(7L)).thenReturn(Optional.of(myRole));
        when(accessLevelRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccessLevelRequest input = new AccessLevelRequest();
        input.setIdAccessLevel(7L);
        input.setName("New Name");

        AccessLevel saved = accessLevelService.saveAccessLevel(input);

        assertThat(saved).isSameAs(myRole);
        assertThat(saved.getName()).isEqualTo("New Name");
        assertThat(saved.getCompany()).isEqualTo(companyA);
    }

    @Test
    void saveAccessLevel_existingRoleOfAnotherCompany_isNotFoundAndNotSaved() {
        AccessLevel theirRole = level("Their Role", true, companyB);
        when(tenantAccess.company()).thenReturn(companyA);
        when(accessLevelRepository.findByNameAndCompanyIsNull("Hijack")).thenReturn(Optional.empty());
        when(accessLevelRepository.findById(8L)).thenReturn(Optional.of(theirRole));

        AccessLevelRequest input = new AccessLevelRequest();
        input.setIdAccessLevel(8L);
        input.setName("Hijack");

        assertThatThrownBy(() -> accessLevelService.saveAccessLevel(input))
                .isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
        verify(accessLevelRepository, never()).save(any());
    }

    @Test
    void saveAccessLevel_whenNameClashesWithUniversalRole_throwsIllegalArgumentException() {
        when(accessLevelRepository.findByNameAndCompanyIsNull("ADMIN"))
                .thenReturn(Optional.of(level("ADMIN", true, null)));

        AccessLevelRequest input = new AccessLevelRequest();
        input.setName("ADMIN");

        assertThatThrownBy(() -> accessLevelService.saveAccessLevel(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("universal role");
    }

    // ─── deleteAccessLevel ────────────────────────────────────────────────────

    @Test
    void deleteAccessLevel_universalRole_throwsIllegalArgumentException() {
        AccessLevel universal = level("ADMIN", true, null);
        when(accessLevelRepository.findById(1L)).thenReturn(Optional.of(universal));

        assertThatThrownBy(() -> accessLevelService.deleteAccessLevel(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot delete a universal role");
    }

    @Test
    void deleteAccessLevel_anotherCompanysRole_throwsIllegalArgumentException() {
        AccessLevel theirRole = level("Their Role", true, companyB);
        when(accessLevelRepository.findById(2L)).thenReturn(Optional.of(theirRole));
        when(tenantAccess.company()).thenReturn(companyA);

        assertThatThrownBy(() -> accessLevelService.deleteAccessLevel(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to your company");
    }

    @Test
    void deleteAccessLevel_ownCompanysRole_callsDeleteById() {
        AccessLevel myRole = level("My Role", true, companyA);
        when(accessLevelRepository.findById(3L)).thenReturn(Optional.of(myRole));
        when(tenantAccess.company()).thenReturn(companyA);

        accessLevelService.deleteAccessLevel(3L);

        verify(accessLevelRepository).deleteById(3L);
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private AccessLevel level(String name, Boolean active, Company company) {
        AccessLevel al = new AccessLevel();
        al.setName(name);
        al.setFlagActive(active);
        al.setCompany(company);
        return al;
    }

    private Company company(Long id, String name) {
        Company c = new Company();
        org.springframework.test.util.ReflectionTestUtils.setField(c, "idCompany", id);
        c.setName(name);
        return c;
    }
}
