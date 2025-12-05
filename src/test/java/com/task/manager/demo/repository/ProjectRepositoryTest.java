package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Project;
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
@DisplayName("ProjectRepository - Integration Tests")
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setProjectTitle("Test Project");
        testProject.setProjectDescription("A comprehensive test project for repository validation");
    }

    @Test
    @DisplayName("Should save a project successfully")
    void shouldSaveProjectSuccessfully() {
        Project savedProject = projectRepository.save(testProject);

        assertNotNull(savedProject);
        assertNotNull(savedProject.getId());
        assertEquals("Test Project", savedProject.getProjectTitle());
        assertEquals("A comprehensive test project for repository validation", savedProject.getProjectDescription());
    }

    @Test
    @DisplayName("Should find project by ID")
    void shouldFindProjectById() {
        Project savedProject = projectRepository.save(testProject);

        Optional<Project> foundProject = projectRepository.findById(savedProject.getId());

        assertTrue(foundProject.isPresent());
        assertEquals("Test Project", foundProject.get().getProjectTitle());
    }

    @Test
    @DisplayName("Should return empty Optional when project not found")
    void shouldReturnEmptyOptionalWhenProjectNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<Project> foundProject = projectRepository.findById(nonExistentId);

        assertFalse(foundProject.isPresent());
    }

    @Test
    @DisplayName("Should search projects by title")
    void shouldSearchProjectsByTitle() {
        projectRepository.save(testProject);

        Project secondProject = new Project();
        secondProject.setProjectTitle("Backend Development Project");
        secondProject.setProjectDescription("All backend related tasks");
        projectRepository.save(secondProject);

        List<Project> searchResults = projectRepository.searchByTitle("Project");

        assertEquals(2, searchResults.size());
    }

    @Test
    @DisplayName("Should search projects by title case-insensitive")
    void shouldSearchProjectsByTitleCaseInsensitive() {
        projectRepository.save(testProject);

        List<Project> searchResults = projectRepository.searchByTitle("test project");

        assertEquals(1, searchResults.size());
        assertEquals("Test Project", searchResults.get(0).getProjectTitle());
    }

    @Test
    @DisplayName("Should search projects with partial title match")
    void shouldSearchProjectsWithPartialTitleMatch() {
        projectRepository.save(testProject);

        Project frontendProject = new Project();
        frontendProject.setProjectTitle("Frontend Implementation");
        frontendProject.setProjectDescription("Frontend related tasks");
        projectRepository.save(frontendProject);

        List<Project> searchResults = projectRepository.searchByTitle("Project");

        assertEquals(1, searchResults.size());
    }

    @Test
    @DisplayName("Should return empty list when search title not found")
    void shouldReturnEmptyListWhenSearchTitleNotFound() {
        projectRepository.save(testProject);

        List<Project> searchResults = projectRepository.searchByTitle("NonExistentProject");

        assertNotNull(searchResults);
        assertEquals(0, searchResults.size());
    }

    @Test
    @DisplayName("Should verify project exists by title")
    void shouldVerifyProjectExistsByTitle() {
        projectRepository.save(testProject);

        boolean exists = projectRepository.existsByProjectTitle("Test Project");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should verify project does not exist by title")
    void shouldVerifyProjectDoesNotExistByTitle() {
        projectRepository.save(testProject);

        boolean exists = projectRepository.existsByProjectTitle("Non Existent Project");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete project by ID")
    void shouldDeleteProjectById() {
        Project savedProject = projectRepository.save(testProject);
        UUID projectId = savedProject.getId();

        projectRepository.deleteById(projectId);
        Optional<Project> deletedProject = projectRepository.findById(projectId);

        assertFalse(deletedProject.isPresent());
    }

    @Test
    @DisplayName("Should update project successfully")
    void shouldUpdateProjectSuccessfully() {
        Project savedProject = projectRepository.save(testProject);

        savedProject.setProjectTitle("Updated Project Title");
        savedProject.setProjectDescription("Updated project description");

        Project updatedProject = projectRepository.save(savedProject);

        assertEquals("Updated Project Title", updatedProject.getProjectTitle());
        assertEquals("Updated project description", updatedProject.getProjectDescription());
    }

    @Test
    @DisplayName("Should get all projects")
    void shouldGetAllProjects() {
        projectRepository.save(testProject);

        Project secondProject = new Project();
        secondProject.setProjectTitle("Second Project");
        secondProject.setProjectDescription("Another project");
        projectRepository.save(secondProject);

        List<Project> allProjects = projectRepository.findAll();

        assertEquals(2, allProjects.size());
    }

    @Test
    @DisplayName("Should return empty list when no projects exist")
    void shouldReturnEmptyListWhenNoProjectsExist() {
        List<Project> allProjects = projectRepository.findAll();

        assertNotNull(allProjects);
        assertEquals(0, allProjects.size());
    }

    @Test
    @DisplayName("Should save multiple projects and retrieve them")
    void shouldSaveMultipleProjectsAndRetrieveThem() {
        for (int i = 1; i <= 5; i++) {
            Project project = new Project();
            project.setProjectTitle("Project " + i);
            project.setProjectDescription("Description for project " + i);
            projectRepository.save(project);
        }

        List<Project> allProjects = projectRepository.findAll();

        assertEquals(5, allProjects.size());
    }

    @Test
    @DisplayName("Should handle project with special characters in title")
    void shouldHandleProjectWithSpecialCharactersInTitle() {
        Project specialProject = new Project();
        specialProject.setProjectTitle("Project @#$ & Test");
        specialProject.setProjectDescription("Project with special characters");

        Project savedProject = projectRepository.save(specialProject);

        Optional<Project> foundProject = projectRepository.findById(savedProject.getId());

        assertTrue(foundProject.isPresent());
        assertEquals("Project @#$ & Test", foundProject.get().getProjectTitle());
    }

    @Test
    @DisplayName("Should handle very long project titles")
    void shouldHandleVeryLongProjectTitles() {
        String longTitle = "A".repeat(256);
        Project longTitleProject = new Project();
        longTitleProject.setProjectTitle(longTitle);
        longTitleProject.setProjectDescription("Project with very long title");

        Project savedProject = projectRepository.save(longTitleProject);

        Optional<Project> foundProject = projectRepository.findById(savedProject.getId());

        assertTrue(foundProject.isPresent());
        assertEquals(longTitle, foundProject.get().getProjectTitle());
    }

    @Test
    @DisplayName("Should handle multiple search results")
    void shouldHandleMultipleSearchResults() {
        for (int i = 1; i <= 3; i++) {
            Project project = new Project();
            project.setProjectTitle("Search Test Project " + i);
            project.setProjectDescription("Test search functionality");
            projectRepository.save(project);
        }

        List<Project> searchResults = projectRepository.searchByTitle("Search Test");

        assertEquals(3, searchResults.size());
    }

    @Test
    @DisplayName("Should find recently updated project")
    void shouldFindRecentlyUpdatedProject() {
        Project savedProject = projectRepository.save(testProject);
        UUID projectId = savedProject.getId();

        savedProject.setProjectTitle("Recently Updated Project");
        projectRepository.save(savedProject);

        Optional<Project> foundProject = projectRepository.findById(projectId);

        assertTrue(foundProject.isPresent());
        assertEquals("Recently Updated Project", foundProject.get().getProjectTitle());
    }
}
