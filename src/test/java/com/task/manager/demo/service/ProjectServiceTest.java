package com.task.manager.demo.service;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.project.ProjectDTO;
import com.task.manager.demo.dto.project.ProjectRequest;
import com.task.manager.demo.dto.project.ProjectUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskRequest;
import com.task.manager.demo.entity.*;
import com.task.manager.demo.exception.BadRequestException;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.EpicMapper;
import com.task.manager.demo.mapper.ProjectMapper;
import com.task.manager.demo.mapper.TaskMapper;
import com.task.manager.demo.repository.EpicRepository;
import com.task.manager.demo.repository.ProjectRepository;
import com.task.manager.demo.repository.TaskRepository;
import com.task.manager.demo.repository.UserRepository;
import com.task.manager.demo.service.project.ProjectServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private UUID id;
    private ProjectRequest oldProjectRequest;
    private Project oldProject;

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EpicRepository epicRepository;
    @Mock
    private EpicMapper epicMapper;

    @Mock
    private ProjectMapper mapper;

    @InjectMocks
    private ProjectServiceImpl service;

    @BeforeEach
    public void init() {
        id = UUID.randomUUID();
        oldProject = new Project();
        oldProject.setId(UUID.randomUUID());
        oldProject.setProject_title("Old Project");
        oldProject.setProject_description("Old Project for testing");
        oldProjectRequest = new ProjectRequest( "Old Project",  "Old Project for testing");
    }

    @AfterEach
    public void tearDown() {
        oldProject = null;
    }

    @Test
    @DisplayName("Create project successfully")
    void shouldCreateProject() {
        ProjectDTO expectedDto = new ProjectDTO(id, "Old Project", "Old Project for testing", null,null,null, null);

        when(repository.save(ArgumentMatchers.any(Project.class))).thenReturn(oldProject);
        when(mapper.toDto(oldProject)).thenReturn(expectedDto);
        ProjectDTO result = service.create(oldProjectRequest);
        assertEquals(expectedDto.title(), result.title());
        assertEquals(expectedDto.description(), result.description());

        verify(repository).save(ArgumentMatchers.any(Project.class));
        verify(mapper).toDto(oldProject);
    }

    @Test
    @DisplayName("Fail to create project with empty title")
    void shouldNotCreateProjectTestWithEmptyTitle() {
        ProjectRequest request = new ProjectRequest( "",  "Old Project for testing" );
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.create(request));
        assertEquals("Argument 'Title must not be blank' is not valid", exception.getMessage());
    }

    @Test
    @DisplayName("Fail to create two project with same title")
    void shouldNotCreateTwoTaskWithSameTitle() {
        ProjectRequest req1 = new ProjectRequest( "Old Project",  "Old Project for testing" );
        ProjectRequest req2 = new ProjectRequest( "Old Project",  "Old Project for testing" );
        ProjectDTO expectedDto = new ProjectDTO(id, "Old Project", "Old Project for testing", null,null,null, null);

        when(repository.save(ArgumentMatchers.any(Project.class))).thenReturn(oldProject);
        when(mapper.toDto(oldProject)).thenReturn(expectedDto);
        ProjectDTO result1 = service.create(req1);
        assertNotNull(result1);
        when(repository.existsByTitle("Old Project")).thenReturn(true);
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.create(req2));
        assertEquals("Title already exists", ex.getMessage());
    }

    @Test
    @DisplayName("Fail to create project with empty description")
    void shouldNotCreateProjectTestWithEmptyDescription() {
        ProjectRequest request = new ProjectRequest( "Old Project",  "" );
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.create(request));
        assertEquals("Argument 'Description must not be blank' is not valid", exception.getMessage());

    }

    @Test
    @DisplayName("Fail to find false project")
    void shouldNotFindFalseProject() {
        UUID random = UUID.randomUUID();
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.findById(random));
        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully return a list of tasks")
    void shouldReturnAllProjects() {
        ProjectDTO expectedDto = new ProjectDTO(id, "Old Project", "Old Project for testing", null,null,null, null);

        when(repository.findAll()).thenReturn(List.of(oldProject));
        when(mapper.toDto(oldProject)).thenReturn(expectedDto);

        List<ProjectDTO> result_projects = service.getAll();
        assertNotNull(result_projects);
        assertEquals(1, result_projects.size());
        assertEquals(expectedDto.title(), result_projects.get(0).title());
        assertEquals(expectedDto.description(), result_projects.get(0).description());
    }

    @Test
    @DisplayName("Should return an empty list and not null object")
    void shouldEmptyList() {
        List<ProjectDTO> result_tasks = service.getAll();
        assertNotNull(result_tasks);
        assertEquals(0, result_tasks.size());
    }

    @Test
    @DisplayName("Should find an existing project")
    void shouldFindExistingProject() {
        ProjectDTO expectedDto = new ProjectDTO(id, "Old Project", "Old Project for testing", null,null,null, null);

        when(repository.findById(oldProject.getId())).thenReturn(Optional.of(oldProject));
        when(mapper.toDto(oldProject)).thenReturn(expectedDto);

        ProjectDTO result = service.findById(oldProject.getId());
        assertNotNull(result);
        assertEquals(expectedDto.title(), result.title());
        assertEquals(expectedDto.description(), result.description());

    }

    @Test
    @DisplayName("Should not find a nonexisting project")
    void shouldReturnNotFoundTask() {
        UUID random = UUID.randomUUID();
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.findById(random));
        assertEquals("Project not found", ex.getMessage());
    }

    @Test
    @DisplayName("Successfully delete a project")
    void shouldDeleteProject() {
        UUID requesterId = UUID.randomUUID();
        User requester = User.builder().id(requesterId).build();

        when(repository.findById(oldProject.getId()))
                .thenReturn(Optional.of(oldProject));
        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(requester));

        assertDoesNotThrow(() -> service.deleteById(oldProject.getId(), requesterId));
        verify(repository).findById(oldProject.getId());
        verify(userRepository).findById(requesterId);
        assertEquals(requesterId, oldProject.getDeletedBy());
        verify(repository).save(oldProject);
        verify(repository).deleteById(oldProject.getId());
    }


    @Test
    @DisplayName("Fail to delete a project with false project_id")
    void shouldNotDeleteProjectByProjectId() {
        UUID random = UUID.randomUUID();
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.deleteById(random, random));
        assertEquals("Project not found", ex.getMessage());
    }

    @Test
    @DisplayName("Fail to delete a project with false user_id")
    void shouldNotDeleteProjectByUserId() {
        UUID random = UUID.randomUUID();

        when(repository.findById(oldProject.getId()))
                .thenReturn(Optional.of(oldProject));
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.deleteById(oldProject.getId(), random));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    @DisplayName("Successfully update a project")
    void shouldUpdateProject() {
        UUID userId = UUID.randomUUID();

        ProjectUpdateDTO request = new ProjectUpdateDTO(
                "New Name",
                "New Description",
                true,
                userId
        );

        ProjectDTO expectedDto = new ProjectDTO(
                oldProject.getId(),
                "New Name",
                "New Description",
                null, null, null,
                userId
        );

        when(repository.findById(oldProject.getId()))
                .thenReturn(Optional.of(oldProject));

        doAnswer(invocation -> {
            ProjectUpdateDTO req = invocation.getArgument(0);
            Project target = invocation.getArgument(1);

            target.setProject_title(req.title());
            target.setProject_description(req.description());
            return null;
        }).when(mapper).toEntity(request, oldProject);

        when(repository.save(oldProject)).thenReturn(oldProject);
        when(mapper.toDto(oldProject)).thenReturn(expectedDto);

        ProjectDTO result = service.update(oldProject.getId(), request);

        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.title(), result.title());
        assertEquals(expectedDto.description(), result.description());

        verify(repository).findById(oldProject.getId());
        verify(mapper).toEntity(request, oldProject);
        verify(repository).save(oldProject);
        verify(mapper).toDto(oldProject);
    }

    @Test
    @DisplayName("Fail to update project when project not found")
    void shouldFailWhenProjectNotFound() {
        UUID userId = UUID.randomUUID();
        ProjectUpdateDTO request = new ProjectUpdateDTO(
                "New Name",
                "New Description",
                true,
                userId
        );
        when(repository.findById(oldProject.getId()))
                .thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.update(oldProject.getId(), request));
        assertEquals("Project not found", ex.getMessage());
        verify(repository).findById(oldProject.getId());
        verify(mapper, never()).toEntity(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should return list of ProjectDTO when searching by title")
    void shouldSearchProjectsByTitle() {
        String title = "Test Project";

        Project proj1 = Project.builder().id(UUID.randomUUID()).project_title("Test Project A").build();
        Project proj2 = Project.builder().id(UUID.randomUUID()).project_title("Test Project B").build();

        ProjectDTO dto1 = new ProjectDTO(
                proj1.getId(),
                proj1.getProject_title(),
                proj1.getProject_description(), null,null,null, null);
        ProjectDTO dto2 = new ProjectDTO(
                proj2.getId(),
                proj2.getProject_title(),
                proj2.getProject_description(), null,null,null, null);

        List<Project> projects = List.of(proj1, proj2);
        List<ProjectDTO> expected = List.of(dto1, dto2);

        when(repository.searchByTitle(title)).thenReturn(projects);
        when(mapper.toDto(proj1)).thenReturn(dto1);
        when(mapper.toDto(proj2)).thenReturn(dto2);

        List<ProjectDTO> result = service.searchProjectByTitle(title);

        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);

        verify(repository).searchByTitle(title);
        verify(mapper).toDto(proj1);
        verify(mapper).toDto(proj2);
    }

    @Test
    @DisplayName("Should return all tasks in a project")
    void shouldGetAllTasksInProject() {
        Task task1 = Task.builder().id(UUID.randomUUID()).title("Task A").build();
        Task task2 = Task.builder().id(UUID.randomUUID()).title("Task B").build();

        TaskDTO dto1 = new  TaskDTO(
                task1.getId(),
                task1.getTitle(),
                task1.getDescription(),
                false, null, null, null, 0,
                Type_Enum.TASK, null, null, null, oldProject.getId()
        );
        TaskDTO dto2 = new  TaskDTO(
                task2.getId(),
                task2.getTitle(),
                task2.getDescription(),
                false, null, null, null, 0,
                Type_Enum.TASK, null, null, null, oldProject.getId()
        );

        List<Task> tasks = List.of(task1, task2);
        List<TaskDTO> expected = List.of(dto1, dto2);

        when(taskRepository.findAllByProject_Id(oldProject.getId())).thenReturn(tasks);
        when(taskMapper.toDto(task1)).thenReturn(dto1);
        when(taskMapper.toDto(task2)).thenReturn(dto2);

        List<TaskDTO> result = service.getAllTasksInProject(oldProject.getId());

        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);

        verify(taskRepository).findAllByProject_Id(oldProject.getId());
        verify(taskMapper).toDto(task1);
        verify(taskMapper).toDto(task2);
    }

    @Test
    @DisplayName("Should return all epics in a project")
    void shouldGetAllEpicsInProject() {
        Epic epic1 = Epic.builder().id(UUID.randomUUID()).epic_title("Task A").build();
        Epic epic2 = Epic.builder().id(UUID.randomUUID()).epic_title("Task B").build();

        EpicDTO dto1 = new  EpicDTO(
                epic1.getId(),
                epic1.getEpic_title(),
                epic1.getEpic_description(),
                false, null, null, null, null,
                null, 0, oldProject.getId()
        );
        EpicDTO dto2 = new  EpicDTO(
                epic2.getId(),
                epic2.getEpic_title(),
                epic2.getEpic_description(),
                false, null, null, null, null,
                null, 0, oldProject.getId()
        );

        List<Epic> epics = List.of(epic1, epic2);
        List<EpicDTO> expected = List.of(dto1, dto2);

        when(epicRepository.findAllByProject_Id(oldProject.getId())).thenReturn(epics);
        when(epicMapper.toDto(epic1)).thenReturn(dto1);
        when(epicMapper.toDto(epic2)).thenReturn(dto2);

        List<EpicDTO> result = service.getAllEpicsInProject(oldProject.getId());

        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);

        verify(epicRepository).findAllByProject_Id(oldProject.getId());
        verify(epicMapper).toDto(epic1);
        verify(epicMapper).toDto(epic2);
    }

}
