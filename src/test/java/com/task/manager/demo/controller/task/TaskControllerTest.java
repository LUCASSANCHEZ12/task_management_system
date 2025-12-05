package com.task.manager.demo.controller.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskRequest;
import com.task.manager.demo.dto.task.TaskUpdateDTO;
import com.task.manager.demo.entity.Type_Enum;
import com.task.manager.demo.exception.GlobalExceptionHandler;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.service.task.TaskService;
import com.task.manager.demo.service.task.TaskServiceImpl;
import com.task.manager.demo.service.task.TaskServiceImpl;
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
@Import({TaskServiceImpl.class, GlobalExceptionHandler.class})
@DisplayName("TaskController - Integration Tests")
public class TaskControllerTest {

    @MockitoBean
    private TaskService service;

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
    @DisplayName("Should return unauthorized when accessing task endpoint without authentication")
    void shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        UUID projectId = UUID.randomUUID();
        TaskRequest request = new TaskRequest("Test Task", "Test Description", 5, "TASK", null, projectId);

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
        TaskRequest request = new TaskRequest("", "Test Description", 5, "TASK",null, projectId);

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
        TaskRequest request = new TaskRequest("Test Task", "", 5, "TASK", null, projectId);

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
    @DisplayName("Should successfully get all tasks")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyGetAllTasks() throws Exception {
        TaskDTO task = new TaskDTO(
                UUID.randomUUID(),
                "Test Task",
                "Test Task description",
                false, null, null, null,
                0, Type_Enum.TASK, null,
                null, null, null
        );

        when(service.getAll()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/task/"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Task"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test Task description"));
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
    @DisplayName("Should successfully get task by ID")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyGetTaskById() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskDTO task = new TaskDTO(
                taskId,
                "Test Task",
                "Test Task description",
                false, null, null, null,
                0, Type_Enum.TASK, null,
                null, null, null
        );

        when(service.findById(taskId)).thenReturn(task);

        mockMvc.perform(get("/api/task/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(taskId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Task"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test Task description"));
    }

    @Test
    @DisplayName("Should return not found when task id does not exist")
    @WithMockUser(roles = "USER")
    void shouldReturnNotFoundWhenTaskIdDoesNotExist() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(service.findById(taskId)).thenThrow(new ResourceNotFoundException("Task not found"));

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
    @DisplayName("Should delete task successfully")
    @WithMockUser(roles = "USER")
    void shouldDeleteTaskSuccessfully() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doNothing().when(service).deleteById(taskId,userId);

        mockMvc.perform(delete("/api/task/{id}/user/{userId}", taskId, userId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Task deleted successfully"));
    }

    @Test
    @DisplayName("Should return unauthorized when completing task without authentication")
    void shouldReturnUnauthorizedWhenCompletingTaskWithoutAuthentication() throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(post("/api/task/complete/{id}", taskId).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should complete task successfully")
    @WithMockUser(roles = "USER")
    void shouldCompleteTaskSuccessfully() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskDTO completedTask = new TaskDTO(
                taskId,
                "Test Task",
                "Test Task description",
                true, null, null, null,
                0, Type_Enum.TASK, null,
                null, null, null
        );

        when(service.complete(taskId)).thenReturn(completedTask);

        mockMvc.perform(post("/api/task/complete/{id}", taskId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.completed").value(true));
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
    @DisplayName("Should successfully update a task")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyUpdateTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskUpdateDTO updateRequest = new TaskUpdateDTO("Updated Task", "Updated Description", 8, "TASK", false);
        TaskDTO updatedTask = new TaskDTO(
                taskId,
                "Updated Task",
                "Updated Description",
                false, null, null, null,
                8, Type_Enum.TASK, null,
                null, null, null
        );

        when(service.update(taskId, updateRequest)).thenReturn(updatedTask);

        mockMvc.perform(patch("/api/task/{id}", taskId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Task"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @DisplayName("Should successfully create a task")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyCreateATask() throws Exception {
        UUID projectId = UUID.randomUUID();
        TaskRequest request = new TaskRequest("Valid Task", "Valid Description", 5, "TASK", null, projectId);
        TaskDTO createdTask = new TaskDTO(
                UUID.randomUUID(),
                "Valid Task",
                "Valid Description",
                false, null, null, null,
                5, Type_Enum.TASK, null,
                null, null, projectId
        );

        when(service.create(request)).thenReturn(createdTask);

        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return forbidden when getting tasks by user without authentication")
    void shouldReturnForbiddenWhenGettingTasksByUserWithoutAuthentication() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(get("/api/task/user/{id}", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return forbidden when assigning task to epic without authentication")
    void shouldReturnForbiddenWhenAssigningTaskToEpicWithoutAuthentication() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID epicId = UUID.randomUUID();

        mockMvc.perform(post("/api/task/{id}/epic/{epic_id}", taskId, epicId).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should assign task to epic successfully")
    @WithMockUser(roles = "USER")
    void shouldAssignTaskToEpicSuccessfully() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID epicId = UUID.randomUUID();
        TaskDTO assignedTask = new TaskDTO(
                taskId,
                "Test Task",
                "Test Task description",
                false, null, null, null,
                0, Type_Enum.TASK, epicId,
                null, null, null
        );

        when(service.assignToEpic(taskId, epicId)).thenReturn(assignedTask);

        mockMvc.perform(post("/api/task/{id}/epic/{epic_id}", taskId, epicId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.epic_id").value(epicId.toString()));
    }

    @Test
    @DisplayName("Should return forbidden when assigning task to user without authentication")
    void shouldReturnForbiddenWhenAssigningTaskToUserWithoutAuthentication() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/api/task/{id}/user/{user_id}", taskId, userId).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should assign task to user successfully")
    @WithMockUser(roles = "USER")
    void shouldAssignTaskToUserSuccessfully() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TaskDTO assignedTask = new TaskDTO(
                taskId,
                "Test Task",
                "Test Task description",
                false, null, null, null,
                0, Type_Enum.TASK, null,
                null, // parent_id
                userId, // user_id
                null // project_id
        );

        when(service.assignToUser(taskId, userId)).thenReturn(assignedTask);

        mockMvc.perform(post("/api/task/{id}/user/{user_id}", taskId, userId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value(userId.toString()));
    }

    @Test
    @DisplayName("Should return unauthorized when deleting task without csrf token")
    void shouldReturnForbiddenWhenDeletingTaskWithoutCsrfToken() throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(delete("/api/task/{id}", taskId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should search tasks with empty title")
    @WithMockUser(roles = "USER")
    void shouldSearchTasksWithEmptyTitle() throws Exception {
        mockMvc.perform(get("/api/task").param("title", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should fail creating task with negative story points")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingTaskWithNegativeStoryPoints() throws Exception {
        UUID projectId = UUID.randomUUID();
        TaskRequest request = new TaskRequest("Test Task", "Test Description", -1, "TASK", null, projectId);

        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail creating task without project ID")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingTaskWithoutProjectId() throws Exception {
        mockMvc.perform(post("/api/task/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"title\": \"Task Title\", \"description\": \"Description\", \"story_points\": 5, \"type\": \"TASK\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail updating task with empty description")
    @WithMockUser(roles = "USER")
    void shouldFailUpdatingTaskWithEmptyDescription() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskUpdateDTO updateRequest = new TaskUpdateDTO("Updated Task", "", 8, "TASK", false);

        mockMvc.perform(patch("/api/task/{id}", taskId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail updating task with negative story points")
    @WithMockUser(roles = "USER")
    void shouldFailUpdatingTaskWithNegativeStoryPoints() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskUpdateDTO updateRequest = new TaskUpdateDTO("Updated Task", "Updated Description", -1, "TASK", false);

        mockMvc.perform(patch("/api/task/{id}", taskId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
}
