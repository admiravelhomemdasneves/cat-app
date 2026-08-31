package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.User;
import com.camaja.catapp.repository.AccessLevelRepository;
import com.camaja.catapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock AccessLevelRepository accessLevelRepository;
    @Mock SecurityUtils securityUtils;

    @InjectMocks UserService userService;

    Company companyA;
    Company companyB;

    @BeforeEach
    void setUp() {
        companyA = company(1L, "Company A");
        companyB = company(2L, "Company B");
    }

    // ─── inactivateUser ───────────────────────────────────────────────────────

    @Test
    void inactivateUser_setsEnabledFalseAndSaves() {
        User user = new User("u", "u@e.com", "pwd");
        user.setEnabled(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.inactivateUser(1L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().isEnabled()).isFalse();
    }

    @Test
    void inactivateUser_withUnknownId_throwsIllegalArgumentException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.inactivateUser(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    // ─── updateAccessLevel ────────────────────────────────────────────────────

    @Test
    void updateAccessLevel_universalRole_isAllowedAndSaves() {
        User user = new User("u", "u@e.com", "pwd");
        AccessLevel universal = new AccessLevel();
        universal.setName("MANAGER");
        universal.setCompany(null);  // universal — allowed without company check
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accessLevelRepository.findById(2L)).thenReturn(Optional.of(universal));

        userService.updateAccessLevel(1L, 2L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getAccessLevel().getName()).isEqualTo("MANAGER");
    }

    @Test
    void updateAccessLevel_ownCompanysRole_isAllowedAndSaves() {
        User user = new User("u", "u@e.com", "pwd");
        AccessLevel myRole = new AccessLevel();
        myRole.setName("Dev Manager");
        myRole.setCompany(companyA);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accessLevelRepository.findById(2L)).thenReturn(Optional.of(myRole));
        when(securityUtils.getCurrentCompany()).thenReturn(companyA);

        userService.updateAccessLevel(1L, 2L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getAccessLevel().getName()).isEqualTo("Dev Manager");
    }

    @Test
    void updateAccessLevel_anotherCompanysRole_throwsIllegalArgumentException() {
        User user = new User("u", "u@e.com", "pwd");
        AccessLevel theirRole = new AccessLevel();
        theirRole.setName("Their Role");
        theirRole.setCompany(companyB);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accessLevelRepository.findById(2L)).thenReturn(Optional.of(theirRole));
        when(securityUtils.getCurrentCompany()).thenReturn(companyA);

        assertThatThrownBy(() -> userService.updateAccessLevel(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to your company");
    }

    @Test
    void updateAccessLevel_withInvalidAccessLevelId_throwsIllegalArgumentException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(accessLevelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateAccessLevel(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Access level not found");
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private Company company(Long id, String name) {
        Company c = new Company();
        ReflectionTestUtils.setField(c, "idCompany", id);
        c.setName(name);
        return c;
    }
}
