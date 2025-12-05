package com.task.manager.demo.service;

import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskRequest;
import com.task.manager.demo.dto.task.TaskUpdateDTO;
import com.task.manager.demo.entity.Epic;
import com.task.manager.demo.entity.Project;
import com.task.manager.demo.entity.Task;
import com.task.manager.demo.entity.Type_Enum;
import com.task.manager.demo.exception.BadRequestException;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.TaskMapper;
import com.task.manager.demo.repository.EpicRepository;
import com.task.manager.demo.repository.ProjectRepository;
import com.task.manager.demo.repository.TaskRepository;
import com.task.manager.demo.service.task.TaskServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    private UUID id;
    private Task oldTask;
    private TaskRequest oldTaskRequest;
    private Project oldProject;

    @Mock
    private TaskRepository repository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EpicRepository epicRepository;

    @Mock
    private TaskMapper mapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    public void init() {
        id = UUID.randomUUID();
        oldTask = new Task();
        oldTask.setId(id);
        oldTask.setTitle("Old Task");
        oldTask.setType(Type_Enum.TASK);
        oldTask.setDescription("Old Task for testing");
        oldTask.setStory_points(0);
        oldTask.setProject(new Project());
        oldProject = new Project();
        oldProject.setId(UUID.randomUUID());
        oldTaskRequest = new TaskRequest( "Old Task",  "Old Task for testing", 0, "TASK",  oldProject.getId() );
    }

    @AfterEach
    public void tearDown() {
        oldTask = null;
    }

    @Test
    @DisplayName("Create task successfully")
    void shouldCreateTaskTest() {
        TaskDTO expectedDto = new TaskDTO(id, "Old Task", "Old Task for testing", false, null,null,null,0, Type_Enum.TASK, null,null,null,null);

        when(projectRepository.findById(oldProject.getId())).thenReturn(Optional.of(oldProject));
        when(repository.save(ArgumentMatchers.any(Task.class))).thenReturn(oldTask);
        when(mapper.toDto(oldTask)).thenReturn(expectedDto);

        TaskDTO result = taskService.create(oldTaskRequest);

        assertEquals(expectedDto.title(), result.title());
        assertEquals(expectedDto.type(), result.type());
        assertEquals(expectedDto.description(), result.description());
        assertEquals(expectedDto.story_points(), result.story_points());

        verify(repository).save(ArgumentMatchers.any(Task.class));
        verify(mapper).toDto(oldTask);
    }

    @Test
    @DisplayName("Fail to create task with empty title")
    void shouldNotCreateTaskTestWithEmptyTitle() {
        TaskRequest request = new TaskRequest( "",  "Old Task for testing", 0, "TASK",  oldProject.getId() );

        when(projectRepository.findById(oldProject.getId())).thenReturn(Optional.of(oldProject));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.create(request));
        assertEquals("Title must not be blank", exception.getMessage());

    }

    @Test
    @DisplayName("Fail to create two task with same title")
    void shouldNotCreateTwoTaskWithSameTitle() {
        TaskRequest req1 = new TaskRequest("Old task", "Old Task for testing", 0, "TASK", oldProject.getId());
        TaskRequest req2 = new TaskRequest("Old task", "Old Task for testing", 0, "TASK", oldProject.getId());
        TaskDTO expectedDto = new TaskDTO(id, "Old Task", "Old Task for testing", false, null,null,null,0, Type_Enum.TASK, null,null,null,null);

        when(projectRepository.findById(oldProject.getId())).thenReturn(Optional.of(oldProject));
        when(repository.save(ArgumentMatchers.any(Task.class))).thenReturn(oldTask);
        when(mapper.toDto(oldTask)).thenReturn(expectedDto);
        taskService.create(oldTaskRequest);
        TaskDTO result1 = taskService.create(req1);
        assertNotNull(result1);

        when(repository.existsByTitleAndProjectId("Old task", oldProject.getId())).thenReturn(true);
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> taskService.create(req2));
        assertEquals("Title already exists in this project", ex.getMessage());
    }

    @Test
    @DisplayName("Fail to create task with empty description")
    void shouldNotCreateTaskTestWithEmptyDescription() {
        TaskRequest request = new TaskRequest( "Old Task",  "", 0, "TASK", oldProject.getId() );

        when(projectRepository.findById(oldProject.getId())).thenReturn(Optional.of(oldProject));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.create(request));
        assertEquals("Description must not be blank", exception.getMessage());

    }

    @Test
    @DisplayName("Fail to create task with negative story points")
    void shouldNotCreateTaskTestWithNegativeStoryPoints() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);
        TaskRequest request = new TaskRequest( "Old Task",  "Old task test", -10, "TASK",  projectId );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.create(request));
        assertEquals("Story points must not be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Fail to create task with false projectId")
    void shouldNotCreateTaskTestWithFalseProjectId() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);
        TaskRequest request = new TaskRequest( "",  "Old Task for testing", 0, "TASK",  projectId );

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.create(request));
        assertEquals("Project not found", exception.getMessage());

    }

    @Test
    @DisplayName("Fail to create task with empty type")
    void shouldNotCreateTaskTestWithEmptyType() {
        TaskRequest request = new TaskRequest( "Old task",  "Old Task for testing", 0, "",  oldProject.getId() );
        when(projectRepository.findById(oldProject.getId())).thenReturn(Optional.of(oldProject));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.create(request));
        assertEquals("Type must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Fail to create task with false type")
    void shouldNotCreateTaskTestWithFalseType() {
        TaskRequest request = new TaskRequest( "Old task",  "Old Task for testing", 0, "FALSE TASK",  oldProject.getId() );
        when(projectRepository.findById(oldProject.getId())).thenReturn(Optional.of(oldProject));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.create(request));
        assertEquals("No enum constant com.task.manager.demo.entity.Type_Enum.FALSE TASK", exception.getMessage());
    }

    @Test
    @DisplayName("Fail to find false task")
    void shouldNotFindFalseTask() {
        UUID random = UUID.randomUUID();
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.findById(random));
        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    @DisplayName("Success completing a task")
    void shouldCompleteATask() {
        TaskDTO expectedDto = new TaskDTO(id, "Old Task", "Old Task for testing", true , null,null,null,0, Type_Enum.TASK, null,null,null,null);
        when(repository.findById(oldTask.getId())).thenReturn(Optional.of(oldTask));
        when(repository.save(oldTask)).thenReturn(oldTask);
        when(mapper.toDto(oldTask)).thenReturn(expectedDto);
        TaskDTO result = taskService.complete(oldTask.getId());

        assertNotNull(result);
        assertEquals(expectedDto.title(), result.title());
        assertEquals(expectedDto.type(), result.type());
        assertEquals(expectedDto.description(), result.description());
        assertEquals(expectedDto.story_points(), result.story_points());
        assertEquals(expectedDto.completed(), result.completed());

        verify(repository).save(ArgumentMatchers.any(Task.class));
        verify(mapper).toDto(oldTask);
    }

    @Test
    @DisplayName("Fail when receiving a false task id")
    void shouldNotCompleteATask() {
        UUID random = UUID.randomUUID();
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> taskService.complete(random));
        assertEquals("Task not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should successfully return a list of tasks")
    void shouldReturnAllTasks() {
        TaskDTO expectedDto = new TaskDTO(id, "Old Task", "Old Task for testing", true , null,null,null,0, Type_Enum.TASK, null,null,null,null);

        when(repository.findAll()).thenReturn(List.of(oldTask));
        when(mapper.toDto(oldTask)).thenReturn(expectedDto);

        List<TaskDTO> result_tasks = taskService.getAll();
        assertNotNull(result_tasks);
        assertEquals(1, result_tasks.size());
        assertEquals(expectedDto.title(), result_tasks.get(0).title());
        assertEquals(expectedDto.description(), result_tasks.get(0).description());
        assertEquals(expectedDto.story_points(), result_tasks.get(0).story_points());
        assertEquals(expectedDto.completed(), result_tasks.get(0).completed());
    }

    @Test
    @DisplayName("Should return an empty list and not null object")
    void shouldEmptyList() {
        List<TaskDTO> result_tasks = taskService.getAll();
        assertNotNull(result_tasks);
        assertEquals(0, result_tasks.size());
    }

    @Test
    @DisplayName("Should find an existing task")
    void shouldFindExistingTask() {
        TaskDTO expectedDto = new TaskDTO(id, "Old Task", "Old Task for testing", true , null,null,null,0, Type_Enum.TASK, null,null,null,null);

        when(repository.findById(oldTask.getId())).thenReturn(Optional.of(oldTask));
        when(mapper.toDto(oldTask)).thenReturn(expectedDto);

        TaskDTO result = taskService.findById(oldTask.getId());
        assertNotNull(result);
        assertEquals(expectedDto.title(), result.title());
        assertEquals(expectedDto.description(), result.description());
        assertEquals(expectedDto.story_points(), result.story_points());
        assertEquals(expectedDto.completed(), result.completed());

    }

    @Test
    @DisplayName("Should not find a nonexisting task")
    void shouldReturnNotFoundTask() {
        UUID random = UUID.randomUUID();
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> taskService.findById(random));
        assertEquals("Task not found", ex.getMessage());
    }

    @Test
    @DisplayName("Successfully delete a task")
    void shouldDeleteTask() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        task.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(task));
        assertDoesNotThrow(() -> taskService.deleteById(id));
        verify(repository).findById(id);
        verify(repository).deleteById(id);
    }
    @Test
    @DisplayName("Fail to delete a task")
    void shouldNotDeleteTask() {
        UUID random = UUID.randomUUID();
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteById(random));
        assertEquals("Task not found", ex.getMessage());
    }

    @Test
    @DisplayName("Successfully update a task")
    void shouldUpdateTask() {
        TaskUpdateDTO request = new TaskUpdateDTO(
                "New title",
                "New description",
                5,
                "TASK",
                false
        );

        Task updatedTask = new Task();
        updatedTask.setId(id);
        updatedTask.setTitle("New title");

        TaskDTO expectedDto = new TaskDTO(
                id, "New title", "New description",
                false,
                null, null, null,
                5,
                Type_Enum.TASK,
                null, null, null, null
        );

        when(repository.findById(id)).thenReturn(Optional.of(oldTask));
        doAnswer(invocation -> {
            TaskUpdateDTO req = invocation.getArgument(0);
            Task target = invocation.getArgument(1);

            target.setTitle(req.title());
            target.setDescription(req.description());
            target.setStory_points(req.story_points());
            target.setType(Type_Enum.valueOf(req.type()));

            return null;
        }).when(mapper).toEntity(request, oldTask);

        when(repository.save(oldTask)).thenReturn(oldTask);
        when(mapper.toDto(oldTask)).thenReturn(expectedDto);

        TaskDTO result = taskService.update(id, request);

        assertNotNull(result);
        assertEquals("New title", result.title());
        assertEquals("New description", result.description());
        assertEquals(5, result.story_points());

        verify(repository).findById(id);
        verify(mapper).toEntity(request, oldTask);
        verify(repository).save(oldTask);
        verify(mapper).toDto(oldTask);
    }

    @Test
    @DisplayName("Fail to update nonexisting task")
    void shouldNotUpdateTask() {
        UUID random = UUID.randomUUID();
        TaskUpdateDTO request = new TaskUpdateDTO(
                "New title",
                "New description",
                5,
                "TASK",
                false
        );
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> taskService.update(random, request));
        assertEquals("Task not found", ex.getMessage());
    }

    @Test
    @DisplayName("Search tasks by title - should return matching results")
    void shouldReturnTasksMatchingTitle() {
        String title = "bug";

        Task task1 = new Task();
        task1.setId(UUID.randomUUID());
        task1.setTitle("bug in login");

        Task task2 = new Task();
        task2.setId(UUID.randomUUID());
        task2.setTitle("critical bug in navbar");

        List<Task> tasks = List.of(task1, task2);

        TaskDTO dto1 = new TaskDTO(
                task1.getId(), task1.getTitle(), null, false,
                null, null, null, 0, null,
                null, null, null, null
        );

        TaskDTO dto2 = new TaskDTO(
                task2.getId(), task2.getTitle(), null, false,
                null, null, null, 0, null,
                null, null, null, null
        );

        when(repository.searchByTitle(title)).thenReturn(tasks);
        when(mapper.toDto(task1)).thenReturn(dto1);
        when(mapper.toDto(task2)).thenReturn(dto2);

        List<TaskDTO> result = taskService.searchByTaskByTitle(title);

        assertEquals(2, result.size());
        assertEquals("bug in login", result.get(0).title());
        assertEquals("critical bug in navbar", result.get(1).title());

        verify(repository).searchByTitle(title);
        verify(mapper).toDto(task1);
        verify(mapper).toDto(task2);
    }
    @Test
    @DisplayName("Successfully assign task to epic")
    void shouldAssignTaskToEpic() {
        UUID taskId = UUID.randomUUID();
        UUID epicId = UUID.randomUUID();

        Task task = new Task();
        task.setId(taskId);
        Epic epic = new Epic();
        epic.setId(epicId);

        TaskDTO expectedDto = new TaskDTO(
                taskId,
                "Test Task",
                null,
                false,
                null, null, null,
                0,
                null,
                null, null, null, null
        );

        when(repository.findById(taskId)).thenReturn(Optional.of(task));
        when(epicRepository.findById(epicId)).thenReturn(Optional.of(epic));
        when(repository.save(task)).thenReturn(task);
        when(mapper.toDto(task)).thenReturn(expectedDto);

        TaskDTO result = taskService.assignToEpic(taskId, epicId);

        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(epic, task.getEpic());

        verify(repository).findById(taskId);
        verify(epicRepository).findById(epicId);
        verify(repository).save(task);
        verify(mapper).toDto(task);
    }

    @Test
    @DisplayName("Fail to assign task to epic when task is not found")
    void shouldFailWhenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID epicId = UUID.randomUUID();

        when(repository.findById(taskId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex =assertThrows(ResourceNotFoundException.class,
                () -> taskService.assignToEpic(taskId, epicId));
        assertEquals("Task not found", ex.getMessage());
    }

    @Test
    @DisplayName("Fail to assign task to epic when epic is not found")
    void shouldFailWhenEpicNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID epicId = UUID.randomUUID();

        Task task = new Task();
        task.setId(taskId);

        when(repository.findById(taskId)).thenReturn(Optional.of(task));
        when(epicRepository.findById(epicId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex =assertThrows(ResourceNotFoundException.class,
                () -> taskService.assignToEpic(taskId, epicId));
        assertEquals("Epic not found", ex.getMessage());
    }

}
