package com.task.manager.demo.service.epic;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.epic.EpicRequest;
import com.task.manager.demo.dto.epic.EpicUpdateDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link EpicService} interface that manages CRUD and
 * business operations related to Epics within a project management system.
 * <p>
 * This service handles creation, retrieval, updating, deletion, task listing,
 * and search functionalities associated with epics. It uses mappers and
 * repositories to interact with the persistence layer and convert entities
 * to DTO representations.
 * </p>
 */
@Service
public class EpicServiceImpl implements EpicService{

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final EpicRepository repository;
    private final EpicMapper mapper;
    private final UserRepository userRepository;

    /**
     * Constructs a new {@code EpicServiceImpl} with all required dependencies.
     *
     * @param taskRepository    repository for task persistence
     * @param taskMapper        mapper for converting Task entities to DTOs
     * @param projectRepository repository for project persistence
     * @param projectMapper     mapper for converting Project entities to DTOs
     * @param repository        repository for epic persistence
     * @param mapper            mapper for converting Epic entities and DTOs
     * @param userRepository    repository for user persistence
     */
    public EpicServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, ProjectRepository projectRepository, ProjectMapper projectMapper, EpicRepository repository, EpicMapper mapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new epic based on the provided {@link EpicRequest}.
     * <p>
     * Validates title and description, checks for project existence,
     * ensures uniqueness of the epic title within the project, and
     * persists the new epic.
     * </p>
     *
     * @param request the epic creation request containing details such as title,
     *                description, and story points
     * @return an {@link EpicDTO} representing the created epic
     * @throws ResourceNotFoundException if the referenced project does not exist
     * @throws IllegalArgumentException  if required fields are blank
     * @throws BadRequestException       if the epic title already exists in the project
     */
    @Override
    public EpicDTO create(EpicRequest request) {
        Project project = projectRepository.findById(request.project_id())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (request.title().isBlank()) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        if (request.description().isBlank()) {
            throw new IllegalArgumentException("Description must not be blank");
        }
        if(repository.existsByEpicTitleAndProjectId(request.title(), request.project_id())) {
            throw new BadRequestException("Title already exists in this project");
        }

        Epic epic = Epic.builder()
                .epic_title(request.title())
                .epic_description(request.description())
                .epic_story_points(request.story_points())
                .completed(false)
                .deleted(false)
                .deletedAt(null)
                .deletedBy(null)
                .finishedAt(null)
                .project(project)
                .build();
        return mapper.toDto(repository.save(epic));
    }

    /**
     * Retrieves an epic by its unique identifier.
     *
     * @param epic_Id the UUID of the epic to retrieve
     * @return an {@link EpicDTO} containing epic data
     * @throws ResourceNotFoundException if the epic does not exist
     */
    @Override
    public EpicDTO findById(UUID epic_Id) {
        Epic epic = repository.findById(epic_Id)
                .orElseThrow(() -> new ResourceNotFoundException("Epic not found"));
        return mapper.toDto(epic);
    }

    /**
     * Marks the specified epic as completed and sets the finish timestamp.
     *
     * @param epic_Id the UUID of the epic to complete
     * @return an updated {@link EpicDTO} representing the completed epic
     * @throws ResourceNotFoundException if the epic does not exist
     */
    @Override
    public EpicDTO complete(UUID epic_Id) {
        Epic epic = repository.findById(epic_Id)
                .orElseThrow(() -> new ResourceNotFoundException("Epic not found"));
        epic.setCompleted(true);
        epic.setFinishedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(epic));
    }

    /**
     * Retrieves all epics stored in the system.
     *
     * @return a list of {@link EpicDTO} objects representing all epics
     */
    @Override
    public List<EpicDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Retrieves all tasks associated with a specific epic.
     *
     * @param epic_Id the UUID of the epic whose tasks are requested
     * @return a list of {@link TaskDTO} representing tasks within the epic
     * @throws ResourceNotFoundException if the epic does not exist
     */
    @Override
    public List<TaskDTO> getAllTasksInEpic(UUID epic_Id) {
        if (repository.findById(epic_Id).isEmpty()) {
            throw new ResourceNotFoundException("Epic not found");
        }
        List<Task> tasks = taskRepository.findAllByEpic_Id(epic_Id);
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    /**
     * Deletes an epic by its ID and registers which user performed the deletion.
     *
     * @param epic_id   the UUID of the epic to delete
     * @param requester the UUID of the user performing the deletion
     * @throws ResourceNotFoundException if the epic or user does not exist
     */
    @Override
    public void deleteById(UUID epic_id, UUID requester) {
        Optional<Epic> epic = repository.findById(epic_id);
        if (epic.isEmpty()) {
            throw new ResourceNotFoundException("Epic not found");
        }
        Optional<User> user = userRepository.findById(requester);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        epic.get().setDeletedBy(user.get().getId());
        repository.save(epic.get());
        repository.deleteById(epic_id);
    }

    /**
     * Updates the specified epic using the provided update data.
     *
     * @param epic_id the UUID of the epic to update
     * @param request the update details encapsulated in {@link EpicUpdateDTO}
     * @return an updated {@link EpicDTO} after applying changes
     * @throws ResourceNotFoundException if the epic does not exist
     */
    @Override
    public EpicDTO update(UUID epic_id, EpicUpdateDTO request) {
        Epic epic = repository.findById(epic_id)
                .orElseThrow(() -> new ResourceNotFoundException("Epic not found"));
        mapper.toEntity(request, epic);
        epic.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(epic));
    }

    /**
     * Searches for epics whose title matches the given text.
     *
     * @param title the title or partial title to search for
     * @return a list of {@link EpicDTO} matching the search criteria
     */
    @Override
    public List<EpicDTO> searchEpicByTitle(String title) {
        List<Epic> epics = repository.searchByTitle(title);
        return epics.stream().map(mapper::toDto).toList();
    }
}
