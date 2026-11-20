package com.camaja.catapp.services.auth;

import com.camaja.catapp.dto.LoginUserDTO;
import com.camaja.catapp.dto.RegisterUserDTO;
import com.camaja.catapp.dto.VerifyUserDTO;
import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.User;
import com.camaja.catapp.repository.AccessLevelRepository;
import com.camaja.catapp.repository.CompanyRepository;
import com.camaja.catapp.repository.UserRepository;
import com.camaja.catapp.services.infrastructure.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock UserRepository userRepository;
    @Mock CompanyRepository companyRepository;
    @Mock AccessLevelRepository accessLevelRepository;
    @Mock AuthenticationManager authenticationManager;
    @Mock PasswordEncoder passwordEncoder;
    @Mock EmailService emailService;

    AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        // Manual construction to match the explicit constructor parameter order
        authenticationService = new AuthenticationService(
                userRepository, companyRepository, accessLevelRepository,
                authenticationManager, passwordEncoder, emailService
        );
    }

    // ─── signup ───────────────────────────────────────────────────────────────

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void signup_happyPath_savesDisabledUserWithVerificationCode() {
        Company companyA = company(1L);
        loggedInAs(userIn(companyA, false));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(companyRepository.findById(1L)).thenReturn(Optional.of(companyA));
        when(accessLevelRepository.findById(2L)).thenReturn(Optional.of(new AccessLevel()));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterUserDTO dto = newUserDto("new@example.com");
        dto.setAccessLevelId(2L);

        authenticationService.signup(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.isEnabled()).isFalse();
        assertThat(saved.getVerificationCode()).isNotNull();
        assertThat(saved.getVerificationExpiration()).isAfter(LocalDateTime.now());
        assertThat(saved.getCompany()).isSameAs(companyA);
    }

    @Test
    void signup_byCompanyUser_ignoresRequestedCompanyAndUsesCreatorsCompany() {
        Company companyA = company(1L);
        loggedInAs(userIn(companyA, false));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(companyRepository.findById(1L)).thenReturn(Optional.of(companyA));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterUserDTO dto = newUserDto("user@example.com");
        dto.setCompanyId(2L);  // another company: must be ignored

        User saved = authenticationService.signup(dto);

        assertThat(saved.getCompany()).isSameAs(companyA);
        verify(companyRepository, never()).findById(2L);
    }

    @Test
    void signup_bySuperAdminWithoutCompany_usesRequestedCompany() {
        Company companyB = company(2L);
        loggedInAs(userIn(null, true));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(companyRepository.findById(2L)).thenReturn(Optional.of(companyB));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterUserDTO dto = newUserDto("user@example.com");
        dto.setCompanyId(2L);

        User saved = authenticationService.signup(dto);

        assertThat(saved.getCompany()).isSameAs(companyB);
        assertThat(saved.isSuperAdmin()).isFalse();
    }

    @Test
    void signup_bySuperAdminWithoutCompany_withoutCompanyId_isRejected() {
        loggedInAs(userIn(null, true));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.signup(newUserDto("user@example.com")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("companyId is required");
        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_byUserWithoutCompany_isForbidden() {
        loggedInAs(userIn(null, false));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        RegisterUserDTO dto = newUserDto("user@example.com");
        dto.setCompanyId(1L);

        assertThatThrownBy(() -> authenticationService.signup(dto))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_withAccessLevelOfAnotherCompany_isRejected() {
        Company companyA = company(1L);
        AccessLevel theirRole = new AccessLevel();
        theirRole.setCompany(company(2L));
        loggedInAs(userIn(companyA, false));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(companyRepository.findById(1L)).thenReturn(Optional.of(companyA));
        when(accessLevelRepository.findById(5L)).thenReturn(Optional.of(theirRole));

        RegisterUserDTO dto = newUserDto("user@example.com");
        dto.setAccessLevelId(5L);

        assertThatThrownBy(() -> authenticationService.signup(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to your company");
        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_withDuplicateEmail_throwsIllegalArgumentException() {
        when(userRepository.findByEmail("dup@example.com")).thenReturn(Optional.of(new User()));

        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("dup@example.com");
        dto.setUsername("someone");
        dto.setPassword("pass");

        assertThatThrownBy(() -> authenticationService.signup(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_withNonExistentCompanyId_throwsIllegalArgumentException() {
        loggedInAs(userIn(null, true));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        RegisterUserDTO dto = newUserDto("user@example.com");
        dto.setCompanyId(99L);

        assertThatThrownBy(() -> authenticationService.signup(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Company not found");
    }

    // ─── authenticate ─────────────────────────────────────────────────────────

    @Test
    void authenticate_withDisabledUser_throwsRuntimeException_withoutCallingAuthManager() {
        User disabled = new User("user", "u@e.com", "pwd");
        disabled.setEnabled(false);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(disabled));

        LoginUserDTO dto = new LoginUserDTO();
        dto.setUsername("user");
        dto.setPassword("pass");

        assertThatThrownBy(() -> authenticationService.authenticate(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not verified");

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void authenticate_happyPath_returnsUser() {
        User user = new User("user", "u@e.com", "pwd");
        user.setEnabled(true);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        LoginUserDTO dto = new LoginUserDTO();
        dto.setUsername("user");
        dto.setPassword("pass");

        User result = authenticationService.authenticate(dto);

        assertThat(result).isSameAs(user);
        verify(authenticationManager).authenticate(any());
    }

    // ─── verifyUser ───────────────────────────────────────────────────────────

    @Test
    void verifyUser_happyPath_enablesUser() {
        User user = new User("u", "u@e.com", "pwd");
        user.setEnabled(false);
        user.setVerificationCode("123456");
        user.setVerificationExpiration(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByEmail("u@e.com")).thenReturn(Optional.of(user));

        VerifyUserDTO dto = new VerifyUserDTO();
        dto.setEmail("u@e.com");
        dto.setVerificationCode("123456");

        authenticationService.verifyUser(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().isEnabled()).isTrue();
        assertThat(captor.getValue().getVerificationCode()).isNull();
        assertThat(captor.getValue().getVerificationExpiration()).isNull();
    }

    @Test
    void verifyUser_withExpiredCode_throwsRuntimeException() {
        User user = new User("u", "u@e.com", "pwd");
        user.setVerificationCode("123456");
        user.setVerificationExpiration(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail("u@e.com")).thenReturn(Optional.of(user));

        VerifyUserDTO dto = new VerifyUserDTO();
        dto.setEmail("u@e.com");
        dto.setVerificationCode("123456");

        assertThatThrownBy(() -> authenticationService.verifyUser(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void verifyUser_withWrongCode_throwsRuntimeException() {
        User user = new User("u", "u@e.com", "pwd");
        user.setVerificationCode("123456");
        user.setVerificationExpiration(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByEmail("u@e.com")).thenReturn(Optional.of(user));

        VerifyUserDTO dto = new VerifyUserDTO();
        dto.setEmail("u@e.com");
        dto.setVerificationCode("000000");

        assertThatThrownBy(() -> authenticationService.verifyUser(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid");
    }

    // ─── resendVerificationCode ───────────────────────────────────────────────

    @Test
    void resendVerificationCode_forAlreadyVerifiedUser_throwsRuntimeException() {
        User user = new User("u", "u@e.com", "pwd");
        user.setEnabled(true);
        when(userRepository.findByEmail("u@e.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authenticationService.resendVerificationCode("u@e.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already verified");
    }

    // ─── user/company invariant ───────────────────────────────────────────────

    @Test
    void userWithoutCompany_isOnlyValidForSuperAdmin() {
        assertThat(userIn(null, false).hasValidCompany()).isFalse();
        assertThat(userIn(null, true).hasValidCompany()).isTrue();
        assertThat(userIn(company(1L), false).hasValidCompany()).isTrue();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private static void loggedInAs(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    private static User userIn(Company company, boolean superAdmin) {
        User user = new User("creator", "creator@example.com", "pwd");
        user.setCompany(company);
        user.setSuperAdmin(superAdmin);
        return user;
    }

    private static Company company(Long id) {
        Company c = new Company();
        ReflectionTestUtils.setField(c, "idCompany", id);
        return c;
    }

    private static RegisterUserDTO newUserDto(String email) {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail(email);
        dto.setUsername("user");
        dto.setPassword("pass");
        return dto;
    }
}
