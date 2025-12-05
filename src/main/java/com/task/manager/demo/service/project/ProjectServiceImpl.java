package com.task.manager.demo.service.project;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.project.ProjectDTO;
import com.task.manager.demo.dto.project.ProjectRequest;
import com.task.manager.demo.dto.project.ProjectUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.entity.Epic;
import com.task.manager.demo.entity.Project;
import com.task.manager.demo.entity.Task;
import com.task.manager.demo.entity.User;
import com.task.manager.demo.exception.BadRequestException;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.EpicMapper;
import com.task.manager.demo.mapper.ProjectMapper;
import com.task.manager.demo.mapper.TaskMapper;
import com.task.manager.demo.repository.EpicRepository;
import com.task.manager.demo.repository.ProjectRepository;
import com.task.manager.demo.repository.TaskRepository;
import com.task.manager.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link ProjectService} interface that manages CRUD and
 * business operations related to projects within a project management system.
 * <p>
 * This service handles creation, retrieval, updating, deletion, and search of
 * projects, as well as retrieval of associated tasks and epics. It interacts
 * with repositories for persistence and mappers for DTO conversions.
 * </p>
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final EpicRepository epicRepository;
    private final EpicMapper epicMapper;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    /**
     * Constructs a new {@code ProjectServiceImpl} with all required dependencies.
     *
     * @param taskRepository    repository for task persistence
     * @param taskMapper        mapper for converting Task entities to DTOs
     * @param epicRepository    repository for epic persistence
     * @param epicMapper        mapper for converting Epic entities to DTOs
     * @param projectRepository repository for project persistence
     * @param projectMapper     mapper for converting Project entities to DTOs
     * @param userRepository    repository for user persistence
     */
    public ProjectServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, EpicRepository epicRepository, EpicMapper epicMapper, ProjectRepository projectRepository, ProjectMapper projectMapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.epicRepository = epicRepository;
        this.epicMapper = epicMapper;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new project based on the provided {@link ProjectRequest}.
     * <p>
     * Validates the title and description, ensures uniqueness of the project title,
     * and persists the new project.
     * </p>
     *
     * @param request the project creation request containing title and description
     * @return a {@link ProjectDTO} representing the created project
     * @throws IllegalArgumentException if title or description is blank
     * @throws BadRequestException      if the project title already exists
     */
    @Override
    public ProjectDTO create(ProjectRequest request) {
        if (request.title().isBlank()) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        if (request.description().isBlank()) {
            throw new IllegalArgumentException("Description must not be blank");
        }

        if (projectRepository.existsByProjectTitle(request.title())) {
            throw new BadRequestException("Title already exists");
        }
        Project project = Project.builder()
                .projectTitle(request.title())
                .projectDescription(request.description())
                .build();
        return projectMapper.toDto(projectRepository.save(project));
    }

    /**
     * Retrieves a project by its unique identifier.
     *
     * @param project_Id the UUID of the project to retrieve
     * @return a {@link ProjectDTO} representing the project
     * @throws ResourceNotFoundException if the project does not exist
     */
    @Override
    public ProjectDTO findById(UUID project_Id) {
        Project project = projectRepository.findById(project_Id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return projectMapper.toDto(project);
    }

    /**
     * Retrieves all projects stored in the system.
     *
     * @return a list of {@link ProjectDTO} representing all projects
     */
    @Override
    public List<ProjectDTO> getAll() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(projectMapper::toDto).toList();
    }

    /**
     * Retrieves all tasks associated with a specific project.
     *
     * @param projectId the UUID of the project
     * @return a list of {@link TaskDTO} representing tasks in the project
     */
    @Override
    public List<TaskDTO> getAllTasksInProject(UUID projectId) {
        List<Task> tasks = taskRepository.findAllByProject_Id(projectId);
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    /**
     * Retrieves all epics associated with a specific project.
     *
     * @param project_Id the UUID of the project
     * @return a list of {@link EpicDTO} representing epics in the project
     */
    @Override
    public List<EpicDTO> getAllEpicsInProject(UUID project_Id){
        List<Epic> epics = epicRepository.findAllByProject_Id(project_Id);
        return epics.stream().map(epicMapper::toDto).toList();
    }

    /**
     * Deletes a project by its ID and records which user performed the deletion.
     *
     * @param project_Id the UUID of the project to delete
     * @param requester  the UUID of the user performing the deletion
     * @throws ResourceNotFoundException if the project or user does not exist
     */
    @Override
    public void deleteById(UUID project_Id, UUID requester) {
        Optional<Project> proj = projectRepository.findById(project_Id);
        if (proj.isEmpty()) {
            throw new ResourceNotFoundException("Project not found");
        }
        Optional<User> user = userRepository.findById(requester);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        proj.get().setDeletedBy(requester);
        projectRepository.save(proj.get());
        projectRepository.deleteById(project_Id);
    }

    /**
     * Updates an existing project with the provided data.
     *
     * @param project_Id the UUID of the project to update
     * @param request    the update details encapsulated in {@link ProjectUpdateDTO}
     * @return an updated {@link ProjectDTO} after applying changes
     * @throws ResourceNotFoundException if the project does not exist
     */
    @Override
    public ProjectDTO update(UUID project_Id, ProjectUpdateDTO request) {
        Project project = projectRepository.findById(project_Id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        projectMapper.toEntity(request, project);
        return projectMapper.toDto(projectRepository.save(project));
    }

    /**
     * Searches for projects whose title matches the given text.
     *
     * @param title the title or partial title to search for
     * @return a list of {@link ProjectDTO} matching the search criteria
     */
    @Override
    public List<ProjectDTO> searchProjectByTitle(String title) {
        List<Project> projects = projectRepository.searchByTitle(title);
        return projects.stream().map(projectMapper::toDto).toList();
    }
}
