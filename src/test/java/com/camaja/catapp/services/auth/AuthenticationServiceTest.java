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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
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

    @Test
    void signup_happyPath_savesDisabledUserWithVerificationCode() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(companyRepository.findById(1L)).thenReturn(Optional.of(new Company()));
        when(accessLevelRepository.findById(2L)).thenReturn(Optional.of(new AccessLevel()));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("new@example.com");
        dto.setUsername("newuser");
        dto.setPassword("secret");
        dto.setCompanyId(1L);
        dto.setAccessLevelId(2L);

        authenticationService.signup(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.isEnabled()).isFalse();
        assertThat(saved.getVerificationCode()).isNotNull();
        assertThat(saved.getVerificationExpiration()).isAfter(LocalDateTime.now());
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
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("user@example.com");
        dto.setUsername("user");
        dto.setPassword("pass");
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
}
