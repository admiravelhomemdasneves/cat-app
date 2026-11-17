package com.camaja.catapp.controller;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.controllers.BackOfficeController;
import com.camaja.catapp.services.auth.AuthenticationService;
import com.camaja.catapp.services.auth.JwtService;
import com.camaja.catapp.services.domain.*;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BackOfficeController.class)
@Import(MethodSecurityTestConfig.class)
class BackOfficeControllerTest {

    @Autowired MockMvc mockMvc;

    // ─── Security infra (@WebMvcTest doesn't load ApplicationConfiguration) ──
    @MockBean JwtService jwtService;
    @MockBean UserDetailsService userDetailsService;     // for JwtAuthenticationFilter
    @MockBean AuthenticationProvider authenticationProvider;  // for SecurityConfiguration

    // ─── Controller dependencies ──────────────────────────────────────────────
    @MockBean ContactService contactService;
    @MockBean VatService vatService;
    @MockBean StoreService storeService;
    @MockBean OrderService orderService;
    @MockBean BillingService billingService;
    @MockBean CompanyService companyService;
    @MockBean AccessLevelService accessLevelService;
    @MockBean UserService userService;
    @MockBean AuthenticationService authenticationService;
    @MockBean SecurityUtils securityUtils;
    @MockBean BackofficePageService backofficePageService;
    @MockBean RolePagePermissionService rolePagePermissionService;

    // ─── GET /bo/permissions ──────────────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getAllPermissions_withAdminRole_returns200() throws Exception {
        when(rolePagePermissionService.getAllPermissions()).thenReturn(List.of());

        mockMvc.perform(get("/bo/permissions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    void getAllPermissions_withManagerRole_returns403() throws Exception {
        mockMvc.perform(get("/bo/permissions"))
                .andExpect(status().isForbidden());
    }

    // ─── POST /bo/permissions/save ────────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void savePermission_withAdminRole_returns200() throws Exception {
        when(rolePagePermissionService.savePermission(any())).thenReturn(null);

        mockMvc.perform(post("/bo/permissions/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accessLevelId":1,"backofficePageId":1,"canView":true,
                                 "canCreate":true,"canEdit":true,"canDelete":true}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void savePermission_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(post("/bo/permissions/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    // ─── GET /bo/users ────────────────────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getUsers_withAdminRole_returns200() throws Exception {
        when(userService.allUsersAsDTO()).thenReturn(List.of());

        mockMvc.perform(get("/bo/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    void getUsers_withManagerRole_returns200() throws Exception {
        when(userService.allUsersAsDTO()).thenReturn(List.of());

        mockMvc.perform(get("/bo/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void getUsers_withEmployeeRole_returns403() throws Exception {
        mockMvc.perform(get("/bo/users"))
                .andExpect(status().isForbidden());
    }

    // ─── GET /bo/companies ────────────────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "SUPER_ADMIN")
    void getCompanies_withSuperAdminRole_returns200() throws Exception {
        when(companyService.getAllCompanies()).thenReturn(List.of());

        mockMvc.perform(get("/bo/companies"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getCompanies_withCompanyAdminRole_returns403() throws Exception {
        mockMvc.perform(get("/bo/companies"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "MANAGER")
    void getCompanies_withManagerRole_returns403() throws Exception {
        mockMvc.perform(get("/bo/companies"))
                .andExpect(status().isForbidden());
    }

}
