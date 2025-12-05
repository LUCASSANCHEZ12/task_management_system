package com.task.manager.demo.service.task;

import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskRequest;
import com.task.manager.demo.dto.task.TaskUpdateDTO;
import com.task.manager.demo.entity.*;
import com.task.manager.demo.exception.BadRequestException;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.TaskMapper;
import com.task.manager.demo.repository.EpicRepository;
import com.task.manager.demo.repository.ProjectRepository;
import com.task.manager.demo.repository.TaskRepository;
import com.task.manager.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link TaskService} interface that manages CRUD and
 * business operations related to tasks within a project management system.
 * <p>
 * This service handles creation, retrieval, updating, deletion, completion,
 * assignment to users and epics, and search of tasks. It ensures tasks are
 * associated with valid projects, epics, and users, and applies validations
 * on fields such as title, type, and story points.
 * </p>
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final EpicRepository epicRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper mapper;
    private final UserRepository userRepository;

    /**
     * Constructs a new {@code TaskServiceImpl} with all required dependencies.
     *
     * @param repository        repository for task persistence
     * @param epicRepository    repository for epic persistence
     * @param projectRepository repository for project persistence
     * @param mapper            mapper for converting Task entities to DTOs
     * @param userRepository    repository for user persistence
     */
    public TaskServiceImpl(TaskRepository repository, EpicRepository epicRepository, ProjectRepository projectRepository, TaskMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.epicRepository = epicRepository;
        this.projectRepository = projectRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new task based on the provided {@link TaskRequest}.
     * <p>
     * Validates fields such as title, description, type, and story points,
     * ensures uniqueness of the task title within the project, and handles
     * optional parent task assignment.
     * </p>
     *
     * @param request the task creation request
     * @return a {@link TaskDTO} representing the created task
     * @throws IllegalArgumentException if title, description, type is blank, or story points are negative
     * @throws BadRequestException      if title already exists in the project or parent task is invalid
     * @throws ResourceNotFoundException if the project does not exist
     */
    @Override
    public TaskDTO create(TaskRequest request) {
        Project project = projectRepository.findById(request.project_id())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (request.title().isBlank()) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        if (request.description().isBlank()) {
            throw new IllegalArgumentException("Description must not be blank");
        }
        if (request.type().isBlank()) {
            throw new IllegalArgumentException("Type must not be blank");
        }
        if (request.story_points() < 0 ){
            throw new IllegalArgumentException("Story points must not be negative");
        }

        if(repository.existsByTitleAndProjectId(request.title(), request.project_id())) {
            throw new BadRequestException("Title already exists in this project");
        }

        Task task = Task.builder()
                .title(request.title())
                .type(Type_Enum.valueOf(request.type()))
                .description(request.description())
                .user(null)
                .epic(null)
                .project(project)
                .task_parent(null)
                .completed(false)
                .build();

        Optional<Task> parent;
        if( request.parent_id() != null){
            parent = repository.findById(request.parent_id());
            if(parent.isEmpty()) {
                throw new BadRequestException("Parent does not exist in the same project");
            }else {
                if(!parent.get().getProject().getId().equals(request.project_id())) {
                    throw new BadRequestException("Parent does not exist in the same project");
                }
                task.setTask_parent(parent.get());
            }
        }

        return mapper.toDto(repository.save(task));
    }

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id the UUID of the task to retrieve
     * @return a {@link TaskDTO} representing the task
     * @throws ResourceNotFoundException if the task does not exist
     */
    @Override
    public TaskDTO findById(UUID id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return mapper.toDto(task);
    }

    /**
     * Marks a task as completed and sets the finish timestamp.
     *
     * @param id the UUID of the task to complete
     * @return an updated {@link TaskDTO} representing the completed task
     * @throws ResourceNotFoundException if the task does not exist
     */
    @Override
    public TaskDTO complete(UUID id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setCompleted(true);
        task.setFinishedAt(LocalDateTime.now());

        return mapper.toDto(repository.save(task));
    }

    /**
     * Retrieves all tasks stored in the system.
     *
     * @return a list of {@link TaskDTO} representing all tasks
     */
    @Override
    public List<TaskDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Retrieves all tasks assigned to a specific user.
     *
     * @param user_id the UUID of the user
     * @return a list of {@link TaskDTO} representing the user's tasks
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    public List<TaskDTO> getAllUserTasks(UUID user_id) {
        if (userRepository.findById(user_id).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        List<Task> tasks = repository.findAllByUser_Id(user_id);
        return tasks.stream().map(mapper::toDto).toList();
    }

    /**
     * Deletes a task by its ID and records the user performing the deletion.
     *
     * @param id      the UUID of the task to delete
     * @param user_id the UUID of the user performing the deletion
     * @throws ResourceNotFoundException if the task does not exist
     */
    @Override
    public void deleteById(UUID id, UUID user_id) {
        Optional<Task> task = repository.findById(id);
        if (task.isEmpty()) {
            throw new ResourceNotFoundException("Task not found");
        }
        task.get().setDeletedBy(user_id);
        repository.save(task.get());
        repository.deleteById(id);
    }

    /**
     * Updates a task with the provided data.
     *
     * @param task_id the UUID of the task to update
     * @param request the update details encapsulated in {@link TaskUpdateDTO}
     * @return an updated {@link TaskDTO} representing the task
     * @throws ResourceNotFoundException if the task does not exist
     */
    @Override
    public TaskDTO update(UUID task_id, TaskUpdateDTO request) {
        Task task = repository.findById(task_id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        mapper.toEntity(request, task);
        task.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(task));
    }

    /**
     * Searches tasks by their title.
     *
     * @param title the title or partial title to search
     * @return a list of {@link TaskDTO} matching the search criteria
     */
    @Override
    public List<TaskDTO> searchByTaskByTitle(String title) {
        List<Task> tasks = repository.searchByTitle(title);
        return tasks.stream().map(mapper::toDto).toList();
    }

    /**
     * Assigns a task to a specific epic.
     *
     * @param task_id the UUID of the task
     * @param epic_id the UUID of the epic
     * @return an updated {@link TaskDTO} representing the task assigned to the epic
     * @throws ResourceNotFoundException if the task or epic does not exist
     * @throws BadRequestException       if the epic does not belong to the same project
     */
    @Override
    public TaskDTO assignToEpic(UUID task_id, UUID epic_id) {
        Optional<Task> task = repository.findById(task_id);
        Optional<Epic> epic = epicRepository.findById(epic_id);
        if (task.isEmpty()) {
            throw new ResourceNotFoundException("Task not found");
        }
        if (epic.isEmpty()) {
            throw new ResourceNotFoundException("Epic not found");
        }
        if (!epic.get().getProject().getId().equals(task.get().getProject().getId())) {
            throw new BadRequestException("Epic does not exist in the same project");
        }
        task.get().setEpic(epic.get());

        repository.save(task.get());

        return mapper.toDto(task.get());
    }

    /**
     * Assigns a task to a specific user.
     *
     * @param task_id the UUID of the task
     * @param user_id the UUID of the user
     * @return an updated {@link TaskDTO} representing the task assigned to the user
     * @throws ResourceNotFoundException if the task or user does not exist
     */
    @Override
    public TaskDTO assignToUser(UUID task_id, UUID user_id) {
        Optional<Task> task = repository.findById(task_id);
        Optional<User> user = userRepository.findById(user_id);
        if (task.isEmpty()) {
            throw new ResourceNotFoundException("Task not found");
        }
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        task.get().setUser(user.get());

        repository.save(task.get());

        return mapper.toDto(task.get());
    }
}


