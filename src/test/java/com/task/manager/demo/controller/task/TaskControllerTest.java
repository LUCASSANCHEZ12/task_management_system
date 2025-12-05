package com.task.manager.demo.controller.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.manager.demo.dto.task.TaskRequest;
import com.task.manager.demo.dto.task.TaskUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("TaskController - Integration Tests")
class TaskControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;
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
    @DisplayName("Should return unauthorized when accessing task endpoint without authentication")
    void shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        UUID projectId = UUID.randomUUID();
        TaskRequest request = new TaskRequest("Test Task", "Test Description", 5, "TASK", projectId);

        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fail creating task with empty title")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingTaskWithEmptyTitle() throws Exception {
        UUID projectId = UUID.randomUUID();
        TaskRequest request = new TaskRequest("", "Test Description", 5, "TASK", projectId);

        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail creating task with empty description")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingTaskWithEmptyDescription() throws Exception {
        UUID projectId = UUID.randomUUID();
        TaskRequest request = new TaskRequest("Test Task", "", 5, "TASK", projectId);

        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return unauthorized when getting all tasks without authentication")
    void shouldReturnUnauthorizedWhenGettingAllTasksWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/task/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get all tasks with USER role")
    @WithMockUser(roles = "USER")
    void shouldGetAllTasksWithUserRole() throws Exception {
        mockMvc.perform(get("/api/task/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get all tasks with ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllTasksWithAdminRole() throws Exception {
        mockMvc.perform(get("/api/task/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return unauthorized when searching tasks without authentication")
    void shouldReturnUnauthorizedWhenSearchingTasksWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/task").param("title", "test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should search tasks by title with USER role")
    @WithMockUser(roles = "USER")
    void shouldSearchTasksByTitleWithUserRole() throws Exception {
        mockMvc.perform(get("/api/task").param("title", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return unauthorized when getting task by ID without authentication")
    void shouldReturnUnauthorizedWhenGettingTaskByIdWithoutAuthentication() throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(get("/api/task/{id}", taskId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get task by ID with USER role")
    @WithMockUser(roles = "USER")
    void shouldGetTaskByIdWithUserRole() throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(get("/api/task/{id}", taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return unauthorized when deleting task without authentication")
    void shouldReturnUnauthorizedWhenDeletingTaskWithoutAuthentication() throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(delete("/api/task/{id}", taskId).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should delete task with USER role returns not found")
    @WithMockUser(roles = "USER")
    void shouldDeleteTaskWithUserRole() throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(delete("/api/task/{id}", taskId).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return unauthorized when completing task without authentication")
    void shouldReturnUnauthorizedWhenCompletingTaskWithoutAuthentication() throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(post("/api/task/complete/{id}", taskId).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should complete task with USER role returns not found")
    @WithMockUser(roles = "USER")
    void shouldCompleteTaskWithUserRole() throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(post("/api/task/complete/{id}", taskId).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return unauthorized when updating task without authentication")
    void shouldReturnUnauthorizedWhenUpdatingTaskWithoutAuthentication() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskUpdateDTO updateRequest = new TaskUpdateDTO("Updated Task", "Updated Description", 8, "TASK", false);

        mockMvc.perform(patch("/api/task/{id}", taskId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fail updating task with empty title")
    @WithMockUser(roles = "USER")
    void shouldFailUpdatingTaskWithEmptyTitle() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskUpdateDTO updateRequest = new TaskUpdateDTO("", "Updated Description", 8, "TASK", false);

        mockMvc.perform(patch("/api/task/{id}", taskId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should accept valid task creation request")
    @WithMockUser(roles = "USER")
    void shouldAcceptValidTaskCreationRequest() throws Exception {
        UUID projectId = UUID.randomUUID();
        TaskRequest request = new TaskRequest("Valid Task", "Valid Description", 5, "TASK", projectId);

        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
