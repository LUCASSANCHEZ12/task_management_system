package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Epic;
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

import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.config.import=",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
@DisplayName("EpicRepository - Integration Tests")
class EpicRepositoryTest {

    @Autowired
    private EpicRepository epicRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Epic testEpic;
    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setProjectTitle("Test Project");
        testProject.setProjectDescription("A test project for epic repository");
        testProject = projectRepository.save(testProject);

        testEpic = new Epic();
        testEpic.setEpicTitle("Test Epic");
        testEpic.setEpicDescription("A comprehensive test epic");
        testEpic.setEpicStoryPoints(20);
        testEpic.setCompleted(false);
        testEpic.setProject(testProject);
    }

    @Test
    @DisplayName("Should save an epic successfully")
    void shouldSaveEpicSuccessfully() {
        Epic savedEpic = epicRepository.save(testEpic);

        assertNotNull(savedEpic);
        assertNotNull(savedEpic.getId());
        assertEquals("Test Epic", savedEpic.getEpicTitle());
        assertEquals("A comprehensive test epic", savedEpic.getEpicDescription());
        assertEquals(20, savedEpic.getEpicStoryPoints());
        assertFalse(savedEpic.isCompleted());
    }

    @Test
    @DisplayName("Should find epic by ID")
    void shouldFindEpicById() {
        Epic savedEpic = epicRepository.save(testEpic);

        Optional<Epic> foundEpic = epicRepository.findById(savedEpic.getId());

        assertTrue(foundEpic.isPresent());
        assertEquals("Test Epic", foundEpic.get().getEpicTitle());
        assertEquals(testProject.getId(), foundEpic.get().getProject().getId());
    }

    @Test
    @DisplayName("Should return empty Optional when epic not found")
    void shouldReturnEmptyOptionalWhenEpicNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<Epic> foundEpic = epicRepository.findById(nonExistentId);

        assertFalse(foundEpic.isPresent());
    }

    @Test
    @DisplayName("Should search epics by title")
    void shouldSearchEpicsByTitle() {
        epicRepository.save(testEpic);

        Epic secondEpic = new Epic();
        secondEpic.setEpicTitle("User Authentication Epic");
        secondEpic.setEpicDescription("Implement user authentication");
        secondEpic.setEpicStoryPoints(30);
        secondEpic.setProject(testProject);
        epicRepository.save(secondEpic);

        List<Epic> searchResults = epicRepository.searchByTitle("Epic");

        assertEquals(2, searchResults.size());
    }

    @Test
    @DisplayName("Should search epics by title case-insensitive")
    void shouldSearchEpicsByTitleCaseInsensitive() {
        epicRepository.save(testEpic);

        List<Epic> searchResults = epicRepository.searchByTitle("test epic");

        assertEquals(1, searchResults.size());
        assertEquals("Test Epic", searchResults.get(0).getEpicTitle());
    }

    @Test
    @DisplayName("Should search epics with partial title match")
    void shouldSearchEpicsWithPartialTitleMatch() {
        epicRepository.save(testEpic);

        List<Epic> searchResults = epicRepository.searchByTitle("Test");

        assertEquals(1, searchResults.size());
    }

    @Test
    @DisplayName("Should return empty list when search title not found")
    void shouldReturnEmptyListWhenSearchTitleNotFound() {
        epicRepository.save(testEpic);

        List<Epic> searchResults = epicRepository.searchByTitle("NonExistentEpic");

        assertNotNull(searchResults);
        assertEquals(0, searchResults.size());
    }

    @Test
    @DisplayName("Should find epics by project ID")
    void shouldFindEpicsByProjectId() {
        epicRepository.save(testEpic);

        Epic secondEpic = new Epic();
        secondEpic.setEpicTitle("Second Epic");
        secondEpic.setEpicDescription("Another epic");
        secondEpic.setEpicStoryPoints(15);
        secondEpic.setProject(testProject);
        epicRepository.save(secondEpic);

        List<Epic> projectEpics = epicRepository.findAllByProject_Id(testProject.getId());

        assertEquals(2, projectEpics.size());
        assertTrue(projectEpics.stream().allMatch(e -> e.getProject().getId().equals(testProject.getId())));
    }

    @Test
    @DisplayName("Should return empty list when project has no epics")
    void shouldReturnEmptyListWhenProjectHasNoEpics() {
        Project newProject = new Project();
        newProject.setProjectTitle("Empty Project");
        newProject.setProjectDescription("Project with no epics");
        newProject = projectRepository.save(newProject);

        List<Epic> projectEpics = epicRepository.findAllByProject_Id(newProject.getId());

        assertNotNull(projectEpics);
        assertEquals(0, projectEpics.size());
    }

    @Test
    @DisplayName("Should verify epic exists by title and project ID")
    void shouldVerifyEpicExistsByTitleAndProjectId() {
        epicRepository.save(testEpic);

        boolean exists = epicRepository.existsByEpicTitleAndProjectId("Test Epic", testProject.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should verify epic does not exist by title and project ID")
    void shouldVerifyEpicDoesNotExistByTitleAndProjectId() {
        epicRepository.save(testEpic);

        boolean exists = epicRepository.existsByEpicTitleAndProjectId("Non Existent Epic", testProject.getId());

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete epic by ID")
    void shouldDeleteEpicById() {
        Epic savedEpic = epicRepository.save(testEpic);
        UUID epicId = savedEpic.getId();

        epicRepository.deleteById(epicId);
        Optional<Epic> deletedEpic = epicRepository.findById(epicId);

        assertFalse(deletedEpic.isPresent());
    }

    @Test
    @DisplayName("Should update epic successfully")
    void shouldUpdateEpicSuccessfully() {
        Epic savedEpic = epicRepository.save(testEpic);

        savedEpic.setEpicTitle("Updated Epic Title");
        savedEpic.setEpicDescription("Updated epic description");
        savedEpic.setEpicStoryPoints(40);
        savedEpic.setCompleted(true);

        Epic updatedEpic = epicRepository.save(savedEpic);

        assertEquals("Updated Epic Title", updatedEpic.getEpicTitle());
        assertEquals("Updated epic description", updatedEpic.getEpicDescription());
        assertEquals(40, updatedEpic.getEpicStoryPoints());
        assertTrue(updatedEpic.isCompleted());
    }

    @Test
    @DisplayName("Should get all epics")
    void shouldGetAllEpics() {
        epicRepository.save(testEpic);

        Epic secondEpic = new Epic();
        secondEpic.setEpicTitle("Second Epic");
        secondEpic.setEpicDescription("Another epic");
        secondEpic.setEpicStoryPoints(25);
        secondEpic.setProject(testProject);
        epicRepository.save(secondEpic);

        List<Epic> allEpics = epicRepository.findAll();

        assertEquals(2, allEpics.size());
    }

    @Test
    @DisplayName("Should return empty list when no epics exist")
    void shouldReturnEmptyListWhenNoEpicsExist() {
        List<Epic> allEpics = epicRepository.findAll();

        assertNotNull(allEpics);
        assertEquals(0, allEpics.size());
    }

    @Test
    @DisplayName("Should save multiple epics and retrieve them")
    void shouldSaveMultipleEpicsAndRetrieveThem() {
        for (int i = 1; i <= 5; i++) {
            Epic epic = new Epic();
            epic.setEpicTitle("Epic " + i);
            epic.setEpicDescription("Description for epic " + i);
            epic.setEpicStoryPoints(i * 10);
            epic.setProject(testProject);
            epicRepository.save(epic);
        }

        List<Epic> allEpics = epicRepository.findAll();

        assertEquals(5, allEpics.size());
    }

    @Test
    @DisplayName("Should handle epic with relationships to tasks")
    void shouldHandleEpicWithTasks() {
        Epic savedEpic = epicRepository.save(testEpic);

        Task task = new Task();
        task.setTitle("Task in Epic");
        task.setDescription("A task belonging to epic");
        task.setType(Type_Enum.TASK);
        task.setProject(testProject);
        task.setEpic(savedEpic);
        taskRepository.save(task);

        Optional<Epic> foundEpic = epicRepository.findById(savedEpic.getId());

        assertTrue(foundEpic.isPresent());
        assertNotNull(foundEpic.get().getTasks());
        assertEquals(savedEpic.getId(), foundEpic.get().getId());
    }

    @Test
    @DisplayName("Should handle epic with empty task list")
    void shouldHandleEpicWithEmptyTaskList() {
        Epic savedEpic = epicRepository.save(testEpic);

        Optional<Epic> foundEpic = epicRepository.findById(savedEpic.getId());

        assertTrue(foundEpic.isPresent());
        assertNotNull(foundEpic.get().getTasks());
        assertEquals(0, foundEpic.get().getTasks().size());
    }

    @Test
    @DisplayName("Should handle epic with special characters in title")
    void shouldHandleEpicWithSpecialCharactersInTitle() {
        Epic specialEpic = new Epic();
        specialEpic.setEpicTitle("Epic @#$ & Test");
        specialEpic.setEpicDescription("Epic with special characters");
        specialEpic.setEpicStoryPoints(15);
        specialEpic.setProject(testProject);

        Epic savedEpic = epicRepository.save(specialEpic);

        Optional<Epic> foundEpic = epicRepository.findById(savedEpic.getId());

        assertTrue(foundEpic.isPresent());
        assertEquals("Epic @#$ & Test", foundEpic.get().getEpicTitle());
    }

    @Test
    @DisplayName("Should handle multiple epics in same project")
    void shouldHandleMultipleEpicsInSameProject() {
        for (int i = 1; i <= 3; i++) {
            Epic epic = new Epic();
            epic.setEpicTitle("Project Epic " + i);
            epic.setEpicDescription("Epic for main project");
            epic.setEpicStoryPoints(20);
            epic.setProject(testProject);
            epicRepository.save(epic);
        }

        List<Epic> projectEpics = epicRepository.findAllByProject_Id(testProject.getId());

        assertEquals(3, projectEpics.size());
        assertTrue(projectEpics.stream().allMatch(e -> "Project Epic".matches("^.*Project Epic.*$")));
    }

    @Test
    @DisplayName("Should find recently updated epic")
    void shouldFindRecentlyUpdatedEpic() {
        Epic savedEpic = epicRepository.save(testEpic);
        UUID epicId = savedEpic.getId();

        savedEpic.setEpicTitle("Recently Updated Epic");
        epicRepository.save(savedEpic);

        Optional<Epic> foundEpic = epicRepository.findById(epicId);

        assertTrue(foundEpic.isPresent());
        assertEquals("Recently Updated Epic", foundEpic.get().getEpicTitle());
    }
}
