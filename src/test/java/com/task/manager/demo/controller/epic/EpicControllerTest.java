package com.task.manager.demo.controller.epic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.manager.demo.dto.epic.EpicRequest;
import com.task.manager.demo.dto.epic.EpicUpdateDTO;
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
@DisplayName("EpicController - Integration Tests")
class EpicControllerTest {

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
    @DisplayName("Should return forbidden when creating epic without authentication")
    void shouldReturnForbiddenWhenCreatingEpicWithoutAuthentication() throws Exception {
        UUID projectId = UUID.randomUUID();
        EpicRequest request = new EpicRequest("New Epic", "Description", 5, projectId);

        mockMvc.perform(post("/api/epic/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fail creating epic with empty title")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingEpicWithEmptyTitle() throws Exception {
        UUID projectId = UUID.randomUUID();
        EpicRequest request = new EpicRequest("", "Description", 5, projectId);

        mockMvc.perform(post("/api/epic/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail creating epic with empty description")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingEpicWithEmptyDescription() throws Exception {
        UUID projectId = UUID.randomUUID();
        EpicRequest request = new EpicRequest("Epic Title", "", 5, projectId);

        mockMvc.perform(post("/api/epic/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail creating epic with negative story points")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingEpicWithNegativeStoryPoints() throws Exception {
        UUID projectId = UUID.randomUUID();
        EpicRequest request = new EpicRequest("Epic Title", "Description", -1, projectId);

        mockMvc.perform(post("/api/epic/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail creating epic without project ID")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingEpicWithoutProjectId() throws Exception {
        mockMvc.perform(post("/api/epic/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"title\": \"Epic Title\", \"description\": \"Description\", \"story_points\": 5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return forbidden when getting epic by ID without authentication")
    void shouldReturnForbiddenWhenGettingEpicByIdWithoutAuthentication() throws Exception {
        UUID epicId = UUID.randomUUID();

        mockMvc.perform(get("/api/epic/{id}", epicId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get epic by ID with USER role")
    @WithMockUser(roles = "USER")
    void shouldGetEpicByIdWithUserRole() throws Exception {
        UUID epicId = UUID.randomUUID();

        mockMvc.perform(get("/api/epic/{id}", epicId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get epic by ID with ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldGetEpicByIdWithAdminRole() throws Exception {
        UUID epicId = UUID.randomUUID();

        mockMvc.perform(get("/api/epic/{id}", epicId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return forbidden when getting all tasks in epic without authentication")
    void shouldReturnForbiddenWhenGettingTasksInEpicWithoutAuthentication() throws Exception {
        UUID epicId = UUID.randomUUID();

        mockMvc.perform(get("/api/epic/{id}/tasks", epicId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get all tasks in epic with USER role")
    @WithMockUser(roles = "USER")
    void shouldGetTasksInEpicWithUserRole() throws Exception {
        UUID epicId = UUID.randomUUID();

        mockMvc.perform(get("/api/epic/{id}/tasks", epicId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return forbidden when searching epics without authentication")
    void shouldReturnForbiddenWhenSearchingEpicsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/epic").param("title", "test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should search epics by title with USER role")
    @WithMockUser(roles = "USER")
    void shouldSearchEpicsByTitleWithUserRole() throws Exception {
        mockMvc.perform(get("/api/epic").param("title", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return forbidden when getting all epics without authentication")
    void shouldReturnForbiddenWhenGettingAllEpicsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/epic/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get all epics with USER role")
    @WithMockUser(roles = "USER")
    void shouldGetAllEpicsWithUserRole() throws Exception {
        mockMvc.perform(get("/api/epic/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return forbidden when completing epic without authentication")
    void shouldReturnForbiddenWhenCompletingEpicWithoutAuthentication() throws Exception {
        UUID epicId = UUID.randomUUID();

        mockMvc.perform(post("/api/epic/complete/{id}", epicId).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should complete epic with USER role returns not found")
    @WithMockUser(roles = "USER")
    void shouldCompleteEpicWithUserRole() throws Exception {
        UUID epicId = UUID.randomUUID();

        mockMvc.perform(post("/api/epic/complete/{id}", epicId).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return forbidden when updating epic without authentication")
    void shouldReturnForbiddenWhenUpdatingEpicWithoutAuthentication() throws Exception {
        UUID epicId = UUID.randomUUID();
        EpicUpdateDTO updateRequest = new EpicUpdateDTO("Updated Epic", "Updated Description", 8);

        mockMvc.perform(patch("/api/epic/{id}", epicId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fail updating epic with empty title")
    @WithMockUser(roles = "USER")
    void shouldFailUpdatingEpicWithEmptyTitle() throws Exception {
        UUID epicId = UUID.randomUUID();
        EpicUpdateDTO updateRequest = new EpicUpdateDTO("", "Updated Description", 8);

        mockMvc.perform(patch("/api/epic/{id}", epicId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail updating epic with negative story points")
    @WithMockUser(roles = "USER")
    void shouldFailUpdatingEpicWithNegativeStoryPoints() throws Exception {
        UUID epicId = UUID.randomUUID();
        EpicUpdateDTO updateRequest = new EpicUpdateDTO("Updated Epic", "Updated Description", -1);

        mockMvc.perform(patch("/api/epic/{id}", epicId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return forbidden when deleting epic without authentication")
    void shouldReturnForbiddenWhenDeletingEpicWithoutAuthentication() throws Exception {
        UUID epicId = UUID.randomUUID();
        UUID requester = UUID.randomUUID();

        mockMvc.perform(delete("/api/epic/{id}/user/{requester}", epicId, requester).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should delete epic with USER role returns not found")
    @WithMockUser(roles = "USER")
    void shouldDeleteEpicWithUserRole() throws Exception {
        UUID epicId = UUID.randomUUID();
        UUID requester = UUID.randomUUID();

        mockMvc.perform(delete("/api/epic/{id}/user/{requester}", epicId, requester).with(csrf()))
                .andExpect(status().isNotFound());
    }
}
