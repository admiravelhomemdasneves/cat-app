package com.camaja.catapp.controller;

import com.camaja.catapp.controllers.AuthenticationController;
import com.camaja.catapp.models.User;
import com.camaja.catapp.services.auth.AuthenticationService;
import com.camaja.catapp.services.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(MethodSecurityTestConfig.class)
class AuthenticationControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean AuthenticationService authenticationService;
    @MockBean JwtService jwtService;
    // @WebMvcTest doesn't load ApplicationConfiguration, so mock its products directly
    @MockBean UserDetailsService userDetailsService;
    @MockBean AuthenticationProvider authenticationProvider;

    private static final String SIGNUP_BODY = """
            {"email":"u@e.com","username":"user","password":"pass"}
            """;

    private static final String LOGIN_BODY = """
            {"username":"user","password":"pass"}
            """;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void signup_withAdminRole_returns200() throws Exception {
        User user = new User("user", "u@e.com", "encoded");
        user.setEnabled(false);
        when(authenticationService.signup(any())).thenReturn(user);

        mockMvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SIGNUP_BODY))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    void signup_withManagerRole_returns403() throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SIGNUP_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void login_isOpenRoute_returns200WithAuthentication() throws Exception {
        User user = new User("user", "u@e.com", "encoded");
        user.setEnabled(true);
        when(authenticationService.authenticate(any())).thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("fake.jwt.token");
        when(jwtService.getExpirationTime()).thenReturn(3_600_000L);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void verify_isOpenRoute_returns200() throws Exception {
        doNothing().when(authenticationService).verifyUser(any());

        mockMvc.perform(post("/auth/verify")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"u@e.com","verificationCode":"123456"}
                                """))
                .andExpect(status().isOk());
    }
}
