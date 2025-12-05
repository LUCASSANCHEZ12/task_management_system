package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Project;
import com.task.manager.demo.entity.Task;
import com.task.manager.demo.entity.Type_Enum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("TaskRepository - Integration Tests")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Project testProject;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setProjectTitle("Test Project");
        testProject.setProjectDescription("A test project for task repository");
        testProject = projectRepository.save(testProject);

        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setDescription("A test task description");
        testTask.setStory_points(5);
        testTask.setCompleted(false);
        testTask.setType(Type_Enum.TASK);
        testTask.setProject(testProject);
    }

    @Test
    @DisplayName("Should save a task successfully")
    void shouldSaveTaskSuccessfully() {
        Task savedTask = taskRepository.save(testTask);

        assertNotNull(savedTask);
        assertNotNull(savedTask.getId());
        assertEquals("Test Task", savedTask.getTitle());
        assertEquals("A test task description", savedTask.getDescription());
        assertEquals(5, savedTask.getStory_points());
        assertEquals(false, savedTask.isCompleted());
    }

    @Test
    @DisplayName("Should find task by ID")
    void shouldFindTaskById() {
        Task savedTask = taskRepository.save(testTask);

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        assertTrue(foundTask.isPresent());
        assertEquals("Test Task", foundTask.get().getTitle());
        assertEquals(testProject.getId(), foundTask.get().getProject().getId());
    }

    @Test
    @DisplayName("Should return empty Optional when task not found")
    void shouldReturnEmptyOptionalWhenTaskNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<Task> foundTask = taskRepository.findById(nonExistentId);

        assertFalse(foundTask.isPresent());
    }

    @Test
    @DisplayName("Should find all tasks for a user - returns empty list when no user assigned")
    void shouldFindAllTasksForUser() {
        UUID userId = UUID.randomUUID();
        taskRepository.save(testTask);

        Task secondTask = new Task();
        secondTask.setTitle("Second Task");
        secondTask.setDescription("Another task");
        secondTask.setStory_points(3);
        secondTask.setType(Type_Enum.TASK);
        secondTask.setProject(testProject);
        taskRepository.save(secondTask);

        List<Task> userTasks = taskRepository.findAllByUser_Id(userId);

        assertNotNull(userTasks);
        assertEquals(0, userTasks.size());
    }

    @Test
    @DisplayName("Should return empty list when user has no tasks")
    void shouldReturnEmptyListWhenUserHasNoTasks() {
        UUID nonExistentUserId = UUID.randomUUID();

        List<Task> userTasks = taskRepository.findAllByUser_Id(nonExistentUserId);

        assertNotNull(userTasks);
        assertEquals(0, userTasks.size());
    }

    @Test
    @DisplayName("Should search tasks by title")
    void shouldSearchTasksByTitle() {
        taskRepository.save(testTask);

        Task secondTask = new Task();
        secondTask.setTitle("Database Migration Task");
        secondTask.setDescription("Migrate database schema");
        secondTask.setStory_points(8);
        secondTask.setType(Type_Enum.TASK);
        secondTask.setProject(testProject);
        taskRepository.save(secondTask);

        List<Task> searchResults = taskRepository.searchByTitle("Task");

        assertEquals(2, searchResults.size());
    }

    @Test
    @DisplayName("Should search tasks by title case-insensitive")
    void shouldSearchTasksByTitleCaseInsensitive() {
        taskRepository.save(testTask);

        List<Task> searchResults = taskRepository.searchByTitle("test task");

        assertEquals(1, searchResults.size());
        assertEquals("Test Task", searchResults.get(0).getTitle());
    }

    @Test
    @DisplayName("Should return empty list when search title not found")
    void shouldReturnEmptyListWhenSearchTitleNotFound() {
        taskRepository.save(testTask);

        List<Task> searchResults = taskRepository.searchByTitle("NonExistentTitle");

        assertNotNull(searchResults);
        assertEquals(0, searchResults.size());
    }

    @Test
    @DisplayName("Should find tasks by epic ID")
    void shouldFindTasksByEpicId() {
        UUID epicId = UUID.randomUUID();
        testTask.setEpic(null);
        taskRepository.save(testTask);

        List<Task> epicTasks = taskRepository.findAllByEpic_Id(epicId);

        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size());
    }

    @Test
    @DisplayName("Should find tasks by project ID")
    void shouldFindTasksByProjectId() {
        taskRepository.save(testTask);

        Task secondTask = new Task();
        secondTask.setTitle("Another Project Task");
        secondTask.setDescription("Task in same project");
        secondTask.setStory_points(4);
        secondTask.setType(Type_Enum.SUBTASK);
        secondTask.setProject(testProject);
        taskRepository.save(secondTask);

        List<Task> projectTasks = taskRepository.findAllByProject_Id(testProject.getId());

        assertEquals(2, projectTasks.size());
        assertTrue(projectTasks.stream().allMatch(t -> t.getProject().getId().equals(testProject.getId())));
    }

    @Test
    @DisplayName("Should return empty list when project has no tasks")
    void shouldReturnEmptyListWhenProjectHasNoTasks() {
        Project newProject = new Project();
        newProject.setProjectTitle("Empty Project");
        newProject.setProjectDescription("Project with no tasks");
        newProject = projectRepository.save(newProject);

        List<Task> projectTasks = taskRepository.findAllByProject_Id(newProject.getId());

        assertNotNull(projectTasks);
        assertEquals(0, projectTasks.size());
    }

    @Test
    @DisplayName("Should verify task exists by title and project ID")
    void shouldVerifyTaskExistsByTitleAndProjectId() {
        taskRepository.save(testTask);

        boolean exists = taskRepository.existsByTitleAndProjectId("Test Task", testProject.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should verify task does not exist by title and project ID")
    void shouldVerifyTaskDoesNotExistByTitleAndProjectId() {
        taskRepository.save(testTask);

        boolean exists = taskRepository.existsByTitleAndProjectId("Non Existent Task", testProject.getId());

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete task by ID")
    void shouldDeleteTaskById() {
        Task savedTask = taskRepository.save(testTask);
        UUID taskId = savedTask.getId();

        taskRepository.deleteById(taskId);
        Optional<Task> deletedTask = taskRepository.findById(taskId);

        assertFalse(deletedTask.isPresent());
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() {
        Task savedTask = taskRepository.save(testTask);

        savedTask.setTitle("Updated Task Title");
        savedTask.setDescription("Updated description");
        savedTask.setStory_points(10);
        savedTask.setCompleted(true);

        Task updatedTask = taskRepository.save(savedTask);

        assertEquals("Updated Task Title", updatedTask.getTitle());
        assertEquals("Updated description", updatedTask.getDescription());
        assertEquals(10, updatedTask.getStory_points());
        assertTrue(updatedTask.isCompleted());
    }

    @Test
    @DisplayName("Should get all tasks")
    void shouldGetAllTasks() {
        taskRepository.save(testTask);

        Task secondTask = new Task();
        secondTask.setTitle("Second Task");
        secondTask.setDescription("Another task");
        secondTask.setStory_points(3);
        secondTask.setType(Type_Enum.SUBTASK);
        secondTask.setProject(testProject);
        taskRepository.save(secondTask);

        List<Task> allTasks = taskRepository.findAll();

        assertEquals(2, allTasks.size());
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist")
    void shouldReturnEmptyListWhenNoTasksExist() {
        List<Task> allTasks = taskRepository.findAll();

        assertNotNull(allTasks);
        assertEquals(0, allTasks.size());
    }

    @Test
    @DisplayName("Should save multiple tasks and retrieve them")
    void shouldSaveMultipleTasksAndRetrieveThem() {
        for (int i = 1; i <= 5; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setDescription("Description for task " + i);
            task.setStory_points(i);
            task.setType(Type_Enum.TASK);
            task.setProject(testProject);
            taskRepository.save(task);
        }

        List<Task> allTasks = taskRepository.findAll();

        assertEquals(5, allTasks.size());
    }
}
