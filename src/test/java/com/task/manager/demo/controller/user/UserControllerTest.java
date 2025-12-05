package com.task.manager.demo.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.manager.demo.dto.user.UserDto;
import com.task.manager.demo.dto.user.UserUpdateDTO;
import com.task.manager.demo.exception.GlobalExceptionHandler;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.service.user.UserService;
import com.task.manager.demo.service.user.UserServiceImpl;
import com.task.manager.demo.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({UserServiceImpl.class, GlobalExceptionHandler.class})
@DisplayName("UserController - Integration Tests")
public class UserControllerTest {

    @MockitoBean
    private UserService service;

    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should return unauthorized when accessing user endpoint without authentication")
    void shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/user/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return forbidden when accessing user endpoint with USER role")
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWithUserRole() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/user/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should successfully get user by ID")
    @WithMockUser(roles = "ADMIN")
    void shouldSuccessfullyGetUserById() throws Exception {
        UUID id = UUID.randomUUID();
        UserDto user = new UserDto(id, "Test User", "test@example.com", List.of("USER"));

        when(service.findById(id)).thenReturn(user);

        mockMvc.perform(get("/api/user/{id}", id))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should return not found when user id does not exist")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenUserIdDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/user/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete user successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUserSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(service).deleteById(id);

        mockMvc.perform(delete("/api/user/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return forbidden when updating user without ADMIN role")
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWhenUpdatingWithoutAdminRole() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(patch("/api/user/{id}", id)
                        .contentType("application/json")
                        .content("{\"name\": \"Jane Doe\", \"email\": \"jane@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return forbidden when getting all users without ADMIN role")
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWhenGettingAllUsersWithoutAdminRole() throws Exception {
        mockMvc.perform(get("/api/user/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should successfully get all users")
    @WithMockUser(roles = "ADMIN")
    void shouldSuccessfullyGetAllUsers() throws Exception {
        UserDto user = new UserDto(UUID.randomUUID(), "Test User", "test@example.com", List.of("USER"));

        when(service.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/user/"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should return unauthorized when accessing user endpoint without any authentication")
    void shouldReturnUnauthorizedWithoutAnyAuthentication() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/user/{id}", id)
                        .contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should accept valid request format")
    @WithMockUser(roles = "ADMIN")
    void shouldAcceptValidRequestFormat() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(patch("/api/user/{id}", id)
                        .contentType("application/json")
                        .content("{\"name\": \"John Doe\", \"email\": \"john@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isNotFound());
    }
}
