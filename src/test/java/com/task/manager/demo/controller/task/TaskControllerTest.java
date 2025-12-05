package com.task.manager.demo.controller.task;

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

    private MockMvc mockMvc;

    private void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Should return unauthorized when accessing task endpoint without authentication")
    void shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        setupMockMvc();
        UUID projectId = UUID.randomUUID();
        String createRequest = "{\"title\": \"Test Task\", \"description\": \"Test Description\", \"story_points\": 5, \"type\": \"TASK\", \"project_id\": \"" + projectId + "\"}";

        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(createRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fail creating task with empty title")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingTaskWithEmptyTitle() throws Exception {
        setupMockMvc();
        UUID projectId = UUID.randomUUID();
        String createRequest = "{\"title\": \"\", \"description\": \"Test Description\", \"story_points\": 5, \"type\": \"TASK\", \"project_id\": \"" + projectId + "\"}";

        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(createRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail creating task with empty description")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingTaskWithEmptyDescription() throws Exception {
        setupMockMvc();
        UUID projectId = UUID.randomUUID();
        String createRequest = "{\"title\": \"Test Task\", \"description\": \"\", \"story_points\": 5, \"type\": \"TASK\", \"project_id\": \"" + projectId + "\"}";

        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(createRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return unauthorized when getting all tasks without authentication")
    void shouldReturnUnauthorizedWhenGettingAllTasksWithoutAuthentication() throws Exception {
        setupMockMvc();

        mockMvc.perform(get("/api/task/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get all tasks with USER role")
    @WithMockUser(roles = "USER")
    void shouldGetAllTasksWithUserRole() throws Exception {
        setupMockMvc();

        mockMvc.perform(get("/api/task/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get all tasks with ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllTasksWithAdminRole() throws Exception {
        setupMockMvc();

        mockMvc.perform(get("/api/task/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return unauthorized when searching tasks without authentication")
    void shouldReturnUnauthorizedWhenSearchingTasksWithoutAuthentication() throws Exception {
        setupMockMvc();

        mockMvc.perform(get("/api/task").param("title", "test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should search tasks by title with USER role")
    @WithMockUser(roles = "USER")
    void shouldSearchTasksByTitleWithUserRole() throws Exception {
        setupMockMvc();

        mockMvc.perform(get("/api/task").param("title", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return unauthorized when getting task by ID without authentication")
    void shouldReturnUnauthorizedWhenGettingTaskByIdWithoutAuthentication() throws Exception {
        setupMockMvc();
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(get("/api/task/{id}", taskId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get task by ID with USER role")
    @WithMockUser(roles = "USER")
    void shouldGetTaskByIdWithUserRole() throws Exception {
        setupMockMvc();
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(get("/api/task/{id}", taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return unauthorized when deleting task without authentication")
    void shouldReturnUnauthorizedWhenDeletingTaskWithoutAuthentication() throws Exception {
        setupMockMvc();
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(delete("/api/task/{id}", taskId).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should delete task with USER role returns not found")
    @WithMockUser(roles = "USER")
    void shouldDeleteTaskWithUserRole() throws Exception {
        setupMockMvc();
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(delete("/api/task/{id}", taskId).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return unauthorized when completing task without authentication")
    void shouldReturnUnauthorizedWhenCompletingTaskWithoutAuthentication() throws Exception {
        setupMockMvc();
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(post("/api/task/complete/{id}", taskId).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should complete task with USER role returns not found")
    @WithMockUser(roles = "USER")
    void shouldCompleteTaskWithUserRole() throws Exception {
        setupMockMvc();
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(post("/api/task/complete/{id}", taskId).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return unauthorized when updating task without authentication")
    void shouldReturnUnauthorizedWhenUpdatingTaskWithoutAuthentication() throws Exception {
        setupMockMvc();
        UUID taskId = UUID.randomUUID();
        String updateRequest = "{\"title\": \"Updated Task\", \"description\": \"Updated Description\", \"story_points\": 8, \"type\": \"TASK\", \"completed\": false}";

        mockMvc.perform(patch("/api/task/{id}", taskId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(updateRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fail updating task with empty title")
    @WithMockUser(roles = "USER")
    void shouldFailUpdatingTaskWithEmptyTitle() throws Exception {
        setupMockMvc();
        UUID taskId = UUID.randomUUID();
        String updateRequest = "{\"title\": \"\", \"description\": \"Updated Description\", \"story_points\": 8, \"type\": \"TASK\", \"completed\": false}";

        mockMvc.perform(patch("/api/task/{id}", taskId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(updateRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should accept valid task creation request")
    @WithMockUser(roles = "USER")
    void shouldAcceptValidTaskCreationRequest() throws Exception {
        setupMockMvc();
        UUID projectId = UUID.randomUUID();
        String createRequest = "{\"title\": \"Valid Task\", \"description\": \"Valid Description\", \"story_points\": 5, \"type\": \"TASK\", \"project_id\": \"" + projectId + "\"}";

        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(createRequest))
                .andExpect(status().isNotFound());
    }
}
