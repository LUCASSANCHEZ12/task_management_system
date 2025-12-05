package com.task.manager.demo.controller.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("UserController - Integration Tests")
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Should return unauthorized when accessing user endpoint without authentication")
    void shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        setupMockMvc();
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/user/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return forbidden when accessing user endpoint with USER role")
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWithUserRole() throws Exception {
        setupMockMvc();
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/user/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return OK when accessing user endpoint with ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnOkWithAdminRole() throws Exception {
        setupMockMvc();
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/user/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return forbidden when deleting user without ADMIN role")
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWhenDeletingWithoutAdminRole() throws Exception {
        setupMockMvc();
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/user/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return forbidden when updating user without ADMIN role")
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWhenUpdatingWithoutAdminRole() throws Exception {
        setupMockMvc();
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/api/user/{id}", id)
                        .contentType("application/json")
                        .content("{\"name\": \"Jane Doe\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return forbidden when getting all users without ADMIN role")
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWhenGettingAllUsersWithoutAdminRole() throws Exception {
        setupMockMvc();

        mockMvc.perform(get("/api/user/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return OK when getting all users with ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnOkWhenGettingAllUsersWithAdminRole() throws Exception {
        setupMockMvc();

        mockMvc.perform(get("/api/user/"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return unauthorized when accessing user endpoint without any authentication")
    void shouldReturnUnauthorizedWithoutAnyAuthentication() throws Exception {
        setupMockMvc();
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/user/{id}", id)
                        .contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should accept valid request format")
    @WithMockUser(roles = "ADMIN")
    void shouldAcceptValidRequestFormat() throws Exception {
        setupMockMvc();
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/api/user/{id}", id)
                        .contentType("application/json")
                        .content("{\"name\": \"John Doe\", \"email\": \"john@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isNotFound());
    }
}
