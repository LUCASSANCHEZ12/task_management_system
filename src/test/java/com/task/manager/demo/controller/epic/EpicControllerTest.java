package com.task.manager.demo.controller.epic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.epic.EpicRequest;
import com.task.manager.demo.dto.epic.EpicUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.entity.Type_Enum;
import com.task.manager.demo.exception.GlobalExceptionHandler;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.service.epic.EpicService;
import java.util.List;
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

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.config.import=",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
@Import({GlobalExceptionHandler.class})
@DisplayName("EpicController - Integration Tests")
public class EpicControllerTest {

    @MockitoBean
    private EpicService service;

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
    @DisplayName("Should successfully create an epic")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyCreateAnEpic() throws Exception {
        UUID projectId = UUID.randomUUID();
        EpicRequest request = new EpicRequest("Test Epic", "Test Description", 5, projectId);
        EpicDTO createdEpic = new EpicDTO(
                UUID.randomUUID(), "Test Epic", "Test Description",
                false, null, null, null,
                null, null, 5, projectId
        );

        when(service.create(request)).thenReturn(createdEpic);

        mockMvc.perform(post("/api/epic/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
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
    @DisplayName("Should successfully get epic by ID")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyGetEpicById() throws Exception {
        UUID epicId = UUID.randomUUID();
        EpicDTO epic = new EpicDTO(
                epicId, "Test Epic", "Test Epic description",
                false, null, null, null,
                null, null, 0, null
        );

        when(service.findById(epicId)).thenReturn(epic);

        mockMvc.perform(get("/api/epic/{id}", epicId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(epicId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Epic"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test Epic description"));
    }

    @Test
    @DisplayName("Should return not found when epic id does not exist")
    @WithMockUser(roles = "USER")
    void shouldReturnNotFoundWhenEpicIdDoesNotExist() throws Exception {
        UUID epicId = UUID.randomUUID();
        when(service.findById(epicId)).thenThrow(new ResourceNotFoundException("Epic not found"));

        mockMvc.perform(get("/api/epic/{id}", epicId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get epic by ID with ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldGetEpicByIdWithAdminRole() throws Exception {
        UUID epicId = UUID.randomUUID();
        EpicDTO epic = new EpicDTO(
                epicId, "Test Epic", "Test Epic description",
                false, null, null, null,
                null, null, 0, null
        );

        when(service.findById(epicId)).thenReturn(epic);

        mockMvc.perform(get("/api/epic/{id}", epicId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Epic"));
    }

    @Test
    @DisplayName("Should return forbidden when getting all tasks in epic without authentication")
    void shouldReturnForbiddenWhenGettingTasksInEpicWithoutAuthentication() throws Exception {
        UUID epicId = UUID.randomUUID();

        mockMvc.perform(get("/api/epic/{id}/tasks", epicId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get all tasks in epic successfully")
    @WithMockUser(roles = "USER")
    void shouldGetAllTasksInEpicSuccessfully() throws Exception {
        UUID epicId = UUID.randomUUID();
        List<TaskDTO> tasks = List.of(
                new TaskDTO(
                        UUID.randomUUID(),
                        "Task Test 1",
                        "Task Test 1 description",
                        false, null, null, null,
                        0, Type_Enum.TASK, epicId,
                        null, null, null
                )
        );

        when(service.getAllTasksInEpic(epicId)).thenReturn(tasks);

        mockMvc.perform(get("/api/epic/{id}/tasks", epicId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].epic_id").value(epicId.toString()));
    }

    @Test
    @DisplayName("Should return forbidden when searching epics without authentication")
    void shouldReturnForbiddenWhenSearchingEpicsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/epic").param("title", "test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return epics filtered by title")
    @WithMockUser(roles = "USER")
    void shouldReturnEpicsFilteredByTitle() throws Exception {
        String searchTitle = "Test";
        EpicDTO epic = new EpicDTO(
                UUID.randomUUID(), "Test Epic", "Test Epic description",
                false, null, null, null,
                null, null, 0, null
        );

        when(service.searchEpicByTitle(searchTitle)).thenReturn(List.of(epic));

        mockMvc.perform(get("/api/epic").param("title", searchTitle))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Epic"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test Epic description"));
    }

    @Test
    @DisplayName("Should return forbidden when getting all epics without authentication")
    void shouldReturnForbiddenWhenGettingAllEpicsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/epic/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should successfully get all epics")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyGetAllEpics() throws Exception {
        EpicDTO epic = new EpicDTO(
                UUID.randomUUID(), "Test Epic", "Test Epic description",
                false, null, null, null,
                null, null, 0, null
        );

        when(service.getAll()).thenReturn(List.of(epic));

        mockMvc.perform(get("/api/epic/"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Epic"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test Epic description"));
    }

    @Test
    @DisplayName("Should return forbidden when completing epic without authentication")
    void shouldReturnForbiddenWhenCompletingEpicWithoutAuthentication() throws Exception {
        UUID epicId = UUID.randomUUID();

        mockMvc.perform(post("/api/epic/complete/{id}", epicId).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should complete epic successfully")
    @WithMockUser(roles = "USER")
    void shouldCompleteEpicSuccessfully() throws Exception {
        UUID epicId = UUID.randomUUID();
        EpicDTO completedEpic = new EpicDTO(
                epicId, "Test Epic", "Test Epic description",
                true, null, null, null,
                null, null, 0, null
        );

        when(service.complete(epicId)).thenReturn(completedEpic);

        mockMvc.perform(post("/api/epic/complete/{id}", epicId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.completed").value(true));
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
    @DisplayName("Should successfully update an epic")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyUpdateEpic() throws Exception {
        UUID epicId = UUID.randomUUID();
        EpicUpdateDTO updateRequest = new EpicUpdateDTO("Updated Epic", "Updated Description", 8);
        EpicDTO updatedEpic = new EpicDTO(
                epicId, "Updated Epic", "Updated Description",
                false, null, null, null,
                null, null, 8, null
        );

        when(service.update(epicId, updateRequest)).thenReturn(updatedEpic);

        mockMvc.perform(patch("/api/epic/{id}", epicId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Epic"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated Description"));
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
    @DisplayName("Should delete epic successfully")
    @WithMockUser(roles = "USER")
    void shouldDeleteEpicSuccessfully() throws Exception {
        UUID epicId = UUID.randomUUID();
        UUID requester = UUID.randomUUID();

        doNothing().when(service).deleteById(eq(epicId), eq(requester));

        mockMvc.perform(delete("/api/epic/{id}/user/{requester}", epicId, requester).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Epic successfully deleted"));
    }
}
