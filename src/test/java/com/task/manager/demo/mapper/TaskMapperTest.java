package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskUpdateDTO;
import com.task.manager.demo.entity.Epic;
import com.task.manager.demo.entity.Project;
import com.task.manager.demo.entity.Task;
import com.task.manager.demo.entity.Type_Enum;
import com.task.manager.demo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskMapper - Unit Tests")
class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = Mappers.getMapper(TaskMapper.class);
    }

    @Test
    @DisplayName("Should map Task entity to TaskDTO")
    void shouldMapTaskEntityToTaskDTO() {
        // Create test data
        UUID taskId = UUID.randomUUID();
        String title = "Test Task";
        String description = "Test task description";
        boolean completed = false;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime finishedAt = LocalDateTime.now().plusDays(1);
        int storyPoints = 5;
        Type_Enum type = Type_Enum.TASK;

        // Create related entities
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");

        Epic epic = new Epic();
        epic.setId(UUID.randomUUID());
        epic.setEpicTitle("Test Epic");

        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setProjectTitle("Test Project");

        Task parentTask = new Task();
        parentTask.setId(UUID.randomUUID());
        parentTask.setTitle("Parent Task");

        // Create task entity
        Task task = new Task();
        task.setId(taskId);
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(completed);
        task.setCreatedAt(createdAt);
        task.setUpdatedAt(updatedAt);
        task.setFinishedAt(finishedAt);
        task.setStory_points(storyPoints);
        task.setType(type);
        task.setUser(user);
        task.setEpic(epic);
        task.setProject(project);
        task.setTask_parent(parentTask);

        // Map to DTO
        TaskDTO taskDTO = taskMapper.toDto(task);

        // Verify mapping
        assertNotNull(taskDTO);
        assertEquals(taskId, taskDTO.id());
        assertEquals(title, taskDTO.title());
        assertEquals(description, taskDTO.description());
        assertEquals(completed, taskDTO.completed());
        assertEquals(createdAt, taskDTO.createdAt());
        assertEquals(updatedAt, taskDTO.updatedAt());
        assertEquals(finishedAt, taskDTO.finishedAt());
        assertEquals(storyPoints, taskDTO.story_points());
        assertEquals(type, taskDTO.type());
        assertEquals(user.getId(), taskDTO.user_id());
        assertEquals(epic.getId(), taskDTO.epic_id());
        assertEquals(project.getId(), taskDTO.project_id());
    }

    @Test
    @DisplayName("Should map Task entity with null relationships to TaskDTO")
    void shouldMapTaskEntityWithNullRelationshipsToTaskDTO() {
        // Create test data
        UUID taskId = UUID.randomUUID();
        String title = "Test Task";
        String description = "Test task description";
        boolean completed = false;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        int storyPoints = 5;
        Type_Enum type = Type_Enum.TASK;

        // Create task entity with null relationships
        Task task = new Task();
        task.setId(taskId);
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(completed);
        task.setCreatedAt(createdAt);
        task.setUpdatedAt(updatedAt);
        task.setStory_points(storyPoints);
        task.setType(type);
        task.setUser(null);
        task.setEpic(null);
        task.setProject(null);
        task.setTask_parent(null);

        // Map to DTO
        TaskDTO taskDTO = taskMapper.toDto(task);

        // Verify mapping
        assertNotNull(taskDTO);
        assertEquals(taskId, taskDTO.id());
        assertEquals(title, taskDTO.title());
        assertEquals(description, taskDTO.description());
        assertEquals(completed, taskDTO.completed());
        assertEquals(createdAt, taskDTO.createdAt());
        assertEquals(updatedAt, taskDTO.updatedAt());
        assertEquals(storyPoints, taskDTO.story_points());
        assertEquals(type, taskDTO.type());
        assertNull(taskDTO.user_id());
        assertNull(taskDTO.epic_id());
        assertNull(taskDTO.project_id());
    }

    @Test
    @DisplayName("Should map TaskUpdateDTO to existing Task entity")
    void shouldMapTaskUpdateDTOToExistingTaskEntity() {
        // Create existing task
        Task existingTask = new Task();
        existingTask.setId(UUID.randomUUID());
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStory_points(3);
        existingTask.setCompleted(false);
        existingTask.setType(Type_Enum.TASK);

        // Create update DTO
        TaskUpdateDTO updateDTO = new TaskUpdateDTO(
                "New Title",
                "New Description",
                8,
                "SUBTASK",
                true
        );

        // Map to existing entity
        taskMapper.toEntity(updateDTO, existingTask);

        // Verify mapping
        assertEquals("New Title", existingTask.getTitle());
        assertEquals("New Description", existingTask.getDescription());
        assertEquals(8, existingTask.getStory_points());
        assertEquals(Type_Enum.SUBTASK, existingTask.getType());
        assertTrue(existingTask.isCompleted());
    }

    @Test
    @DisplayName("Should map TaskUpdateDTO with partial updates to existing Task entity")
    void shouldMapTaskUpdateDTOWithPartialUpdatesToExistingTaskEntity() {
        // Create existing task
        Task existingTask = new Task();
        existingTask.setId(UUID.randomUUID());
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStory_points(3);
        existingTask.setCompleted(false);
        existingTask.setType(Type_Enum.TASK);

        // Create update DTO with only some fields changed
        TaskUpdateDTO updateDTO = new TaskUpdateDTO(
                "Updated Title",  // Only title changed
                "Old Description",  // Same description
                3,  // Same story points
                "TASK",  // Same type
                false  // Same completed status
        );

        // Map to existing entity
        taskMapper.toEntity(updateDTO, existingTask);

        // Verify mapping
        assertEquals("Updated Title", existingTask.getTitle());
        assertEquals("Old Description", existingTask.getDescription());
        assertEquals(3, existingTask.getStory_points());
        assertEquals(Type_Enum.TASK, existingTask.getType());
        assertFalse(existingTask.isCompleted());
    }

    @Test
    @DisplayName("Should map Task with different task types")
    void shouldMapTaskWithDifferentTaskTypes() {
        // Test TASK type
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Task");
        task.setDescription("Task description");
        task.setType(Type_Enum.TASK);

        TaskDTO taskDTO = taskMapper.toDto(task);
        assertEquals(Type_Enum.TASK, taskDTO.type());

        // Test SUBTASK type
        Task subtask = new Task();
        subtask.setId(UUID.randomUUID());
        subtask.setTitle("Subtask");
        subtask.setDescription("Subtask description");
        subtask.setType(Type_Enum.SUBTASK);

        TaskDTO subtaskDTO = taskMapper.toDto(subtask);
        assertEquals(Type_Enum.SUBTASK, subtaskDTO.type());

    }

    @Test
    @DisplayName("Should map Task with completed status and finishedAt")
    void shouldMapTaskWithCompletedStatusAndFinishedAt() {
        // Create completed task
        Task completedTask = new Task();
        completedTask.setId(UUID.randomUUID());
        completedTask.setTitle("Completed Task");
        completedTask.setDescription("Completed task description");
        completedTask.setCompleted(true);
        completedTask.setFinishedAt(LocalDateTime.now());

        TaskDTO completedTaskDTO = taskMapper.toDto(completedTask);

        assertTrue(completedTaskDTO.completed());
        assertNotNull(completedTaskDTO.finishedAt());

        // Create incomplete task
        Task incompleteTask = new Task();
        incompleteTask.setId(UUID.randomUUID());
        incompleteTask.setTitle("Incomplete Task");
        incompleteTask.setDescription("Incomplete task description");
        incompleteTask.setCompleted(false);
        incompleteTask.setFinishedAt(null);

        TaskDTO incompleteTaskDTO = taskMapper.toDto(incompleteTask);

        assertFalse(incompleteTaskDTO.completed());
        assertNull(incompleteTaskDTO.finishedAt());
    }
}