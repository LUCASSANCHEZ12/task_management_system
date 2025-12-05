package com.task.manager.demo.controller.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.project.ProjectDTO;
import com.task.manager.demo.dto.project.ProjectRequest;
import com.task.manager.demo.dto.project.ProjectUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.entity.Type_Enum;
import com.task.manager.demo.exception.GlobalExceptionHandler;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.service.project.ProjectService;
import com.task.manager.demo.service.project.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@TestPropertySource(properties = {
//        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
//        "spring.datasource.driver-class-name=org.h2.Driver",
//        "spring.datasource.username=sa",
//        "spring.datasource.password=",
//        "spring.jpa.hibernate.ddl-auto=create-drop",
//        "spring.flyway.enabled=false"
//})
@Import({ProjectServiceImpl.class, GlobalExceptionHandler.class})
@DisplayName("ProjectController - Integration Tests")
public class ProjectControllerTest {

    @MockitoBean
    private ProjectService service;

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
    @DisplayName("Should return unauthorized when accessing project endpoint without authentication")
    void shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        ProjectRequest createRequest = new ProjectRequest(
                "Test Project",
                "Test Project description"
        );
        mockMvc.perform(post("/api/project/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fail creating project with empty title")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingProjectWithEmptyTitle() throws Exception {
        ProjectRequest createRequest = new ProjectRequest(
                "",
                "Test Project description"
        );
        mockMvc.perform(post("/api/project/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail creating project with empty title")
    @WithMockUser(roles = "USER")
    void shouldFailCreatingProjectWithEmptyDescription() throws Exception {
        ProjectRequest createRequest = new ProjectRequest(
                "Test Project",
                ""
        );
        mockMvc.perform(post("/api/project/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully create a project")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyCreateAProject() throws Exception {
        ProjectRequest createRequest = new ProjectRequest(
                "Test project",
                "Test Project description"
        );
        mockMvc.perform(post("/api/project/create")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should successfully get all projects")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyGetAllProjects() throws Exception {
        ProjectDTO project = new ProjectDTO(
                UUID.randomUUID(),
                "Test Project",
                "Test Project description",
                null,null,null,null
        );

        when(service.getAll()).thenReturn(List.of(project));

        mockMvc.perform(get("/api/project/"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title").value("Test Project"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("Test Project description"));
    }

    @Test
    @DisplayName("Should successfully get project by ID")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyGetProjectById() throws Exception {
        UUID id = UUID.randomUUID();
        ProjectDTO project = new ProjectDTO(
                id,
                "Test Project",
                "Test Project description",
                null, null,null,null
        );

        when(service.findById(id)).thenReturn(project);

        mockMvc.perform(get("/api/project/{id}", id))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Project"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test Project description"));
    }

    @Test
    @DisplayName("Should return not found when project id does not exists")
    @WithMockUser(roles = "USER")
    void shouldReturnNotFoundWhenGetProjectById() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenThrow(new ResourceNotFoundException("Project not found"));
        mockMvc.perform(get("/api/project/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should successfully get all projects and return at least one")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyGetAllProjectsAndReturnOne() throws Exception {
        UUID id = UUID.randomUUID();
        List<ProjectDTO> projects = List.of(
                new ProjectDTO(
                        id,
                        "Test Project",
                        "Test Project description",
                        null, null,null,null
                )
        );
        when(service.getAll()).thenReturn(projects);

        mockMvc.perform(get("/api/project/"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Project"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test Project description"));
    }


    @Test
    @DisplayName("Should return projects filtered by title")
    @WithMockUser(roles = "USER")
    void shouldReturnProjectsFilteredByTitle() throws Exception {
        UUID id = UUID.randomUUID();
        String searchTitle = "Test";
        List<ProjectDTO> projects = List.of(
                new ProjectDTO(
                        id,
                        "Test Project",
                        "Test Project description",
                        null, null,null,null
                )
        );

        when(service.searchProjectByTitle(searchTitle)).thenReturn(projects);

        mockMvc.perform(get("/api/project")
                        .param("title", searchTitle))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Project"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test Project description"));
    }

    @Test
    @DisplayName("Should return empty list when no projects match title")
    @WithMockUser(roles = "USER")
    void shouldReturnEmptyListWhenNoProjectsMatchTitle() throws Exception {
        String searchTitle = "UnknownTitle";

        when(service.searchProjectByTitle(searchTitle)).thenReturn(List.of());

        mockMvc.perform(get("/api/project")
                        .param("title", searchTitle))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return all tasks for a project")
    @WithMockUser(roles = "USER")
    void shouldReturnAllTasksForProject() throws Exception {

        UUID projectId = UUID.randomUUID();

        List<TaskDTO> tasks = List.of(
                new TaskDTO(
                        UUID.randomUUID(),
                        "Task Test 1",
                        "Task Test 1 description",
                        false, null,null,null,
                        0, Type_Enum.TASK, null,
                        null, null, projectId
                ),
                new TaskDTO(
                        UUID.randomUUID(),
                        "Task Test 2",
                        "Task Test 2 description",
                        false, null,null,null,
                        0, Type_Enum.TASK, null,
                        null, null, projectId
                )
        );

        when(service.getAllTasksInProject(projectId)).thenReturn(tasks);

        mockMvc.perform(get("/api/project/{id}/tasks", projectId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Task Test 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].project_id").value(""+projectId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value("Task Test 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].project_id").value(""+projectId));
    }

    @Test
    @DisplayName("Should return 404 when project not found")
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenProjectNotFound() throws Exception {

        UUID projectId = UUID.randomUUID();

        when(service.getAllTasksInProject(projectId))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        mockMvc.perform(get("/api/project/{id}/tasks", projectId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return all epics for a project")
    @WithMockUser(roles = "USER")
    void shouldReturnAllEpicsForProject() throws Exception {
        UUID projectId = UUID.randomUUID();

        List<EpicDTO> epics = List.of(
                new EpicDTO(
                        UUID.randomUUID(), "Epic test 1", "Epic test 1 description",
                        false, null,null,null,
                         null,null, 0, projectId
                ),
                new EpicDTO(
                        UUID.randomUUID(), "Epic test 2", "Epic test 2 description",
                        false, null,null,null,
                        null,null, 0, projectId
                )
        );

        when(service.getAllEpicsInProject(projectId)).thenReturn(epics);

        mockMvc.perform(get("/api/project/{id}/epics", projectId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Epic test 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].project_id").value(""+projectId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value("Epic test 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].project_id").value(""+projectId));
    }

    @Test
    @DisplayName("Should successfully update a project")
    @WithMockUser(roles = "USER")
    void shouldSuccessfullyUpdateProject() throws Exception {

        UUID projectId = UUID.randomUUID();

        ProjectUpdateDTO updateRequest = new ProjectUpdateDTO(
                "Updated Title",
                "Updated Description",
                false, null
        );

        ProjectDTO updatedProject = new ProjectDTO(
                projectId,
                "Updated Title",
                "Updated Description",
                null, null, null, null
        );

        when(service.update(projectId, updateRequest)).thenReturn(updatedProject);

        mockMvc.perform(patch("/api/project/{id}", projectId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated Description"));
    }
    @Test
    @DisplayName("Should return 404 when project not found")
    @WithMockUser(roles = "USER")
    void shouldReturn404WhenProjectNotFoundInUpdate() throws Exception {

        UUID projectId = UUID.randomUUID();

        ProjectUpdateDTO updateRequest = new ProjectUpdateDTO(
                "Updated Title",
                "Updated Description",
                false, null
        );

        ProjectDTO updatedProject = new ProjectDTO(
                projectId,
                "Updated Title",
                "Updated Description",
                null, null, null, null
        );

        when(service.update(projectId, updateRequest))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        mockMvc.perform(patch("/api/project/{id}", projectId)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

//    @Test
//    @DisplayName("Should delete project successfully")
//    @WithMockUser(username = "testUser", roles = "USER")
//    void shouldDeleteProjectSuccessfully() throws Exception {
//
//        UUID projectId = UUID.randomUUID();
//
//        doNothing().when(service).deleteById(eq(projectId), any(UUID.class));
//
//        mockMvc.perform(delete("/api/project/{id}", projectId)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
//                        .value("Project successfully deleted"));
//    }

    @Test
    @DisplayName("Should return 404 when project does not exist")
    @WithMockUser(username = "testUser", roles = "USER")
    void shouldReturn404WhenProjectNotFoundWhenDelete() throws Exception {

        UUID projectId = UUID.randomUUID();

        doThrow(new ResourceNotFoundException("Project not found"))
                .when(service).deleteById(eq(projectId), any(UUID.class));

        mockMvc.perform(delete("/api/project/{id}", projectId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }


}
