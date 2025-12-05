package com.task.manager.demo.controller.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("AuthController - Integration Tests")
class AuthControllerTest {

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
    @DisplayName("Should fail login with empty email")
    void shouldFailLoginWithEmptyEmail() throws Exception {
        setupMockMvc();
        String loginRequest = "{\"email\": \"\", \"password\": \"password123\"}";

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail login with invalid email format")
    void shouldFailLoginWithInvalidEmailFormat() throws Exception {
        setupMockMvc();
        String loginRequest = "{\"email\": \"invalid-email\", \"password\": \"password123\"}";

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail login with empty password")
    void shouldFailLoginWithEmptyPassword() throws Exception {
        setupMockMvc();
        String loginRequest = "{\"email\": \"user@example.com\", \"password\": \"\"}";

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail register with empty name")
    void shouldFailRegisterWithEmptyName() throws Exception {
        setupMockMvc();
        String registerRequest = "{\"name\": \"\", \"email\": \"user@example.com\", \"password\": \"password123\"}";

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content(registerRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail register with short name")
    void shouldFailRegisterWithShortName() throws Exception {
        setupMockMvc();
        String registerRequest = "{\"name\": \"J\", \"email\": \"user@example.com\", \"password\": \"password123\"}";

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content(registerRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail register with invalid email format")
    void shouldFailRegisterWithInvalidEmailFormat() throws Exception {
        setupMockMvc();
        String registerRequest = "{\"name\": \"John Doe\", \"email\": \"invalid-email\", \"password\": \"password123\"}";

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content(registerRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail register with short password")
    void shouldFailRegisterWithShortPassword() throws Exception {
        setupMockMvc();
        String registerRequest = "{\"name\": \"John Doe\", \"email\": \"user@example.com\", \"password\": \"12345\"}";

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content(registerRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should accept valid login request format")
    void shouldAcceptValidLoginRequestFormat() throws Exception {
        setupMockMvc();
        String loginRequest = "{\"email\": \"user@example.com\", \"password\": \"password123\"}";

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should accept valid register request format")
    void shouldAcceptValidRegisterRequestFormat() throws Exception {
        setupMockMvc();
        String uniqueEmail = "newuser" + System.currentTimeMillis() + "@example.com";
        String registerRequest = "{\"name\": \"John Doe\", \"email\": \"" + uniqueEmail + "\", \"password\": \"password123\"}";

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content(registerRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    @DisplayName("Should return response with correct authentication format")
    void shouldReturnResponseWithCorrectAuthenticationFormat() throws Exception {
        setupMockMvc();
        String uniqueEmail = "janetest" + System.currentTimeMillis() + "@example.com";
        String registerRequest = "{\"name\": \"Jane Doe\", \"email\": \"" + uniqueEmail + "\", \"password\": \"password123\"}";

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content(registerRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.type").isString())
                .andExpect(jsonPath("$.expiresIn").isNumber())
                .andExpect(jsonPath("$.roles").isArray());
    }
}
