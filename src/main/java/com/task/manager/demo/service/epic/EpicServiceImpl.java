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

import static org.postgresql.shaded.com.ongres.scram.common.util.Preconditions.checkArgument;

@Service
public class EpicServiceImpl implements EpicService{

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final EpicRepository repository;
    private final EpicMapper mapper;
    private final UserRepository userRepository;

    public EpicServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, ProjectRepository projectRepository, ProjectMapper projectMapper, EpicRepository repository, EpicMapper mapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }


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

    @Override
    public EpicDTO findById(UUID epic_Id) {
        Epic epic = repository.findById(epic_Id)
                .orElseThrow(() -> new ResourceNotFoundException("Epic not found"));
        return mapper.toDto(epic);
    }

    @Override
    public EpicDTO complete(UUID epic_Id) {
        Epic epic = repository.findById(epic_Id)
                .orElseThrow(() -> new ResourceNotFoundException("Epic not found"));
        epic.setCompleted(true);
        epic.setFinishedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(epic));
    }

    @Override
    public List<EpicDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<TaskDTO> getAllTasksInEpic(UUID epic_Id) {
        if (repository.findById(epic_Id).isEmpty()) {
            throw new ResourceNotFoundException("Epic not found");
        }
        List<Task> tasks = taskRepository.findAllByEpic_Id(epic_Id);
        return tasks.stream().map(taskMapper::toDto).toList();
    }

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

    @Override
    public EpicDTO update(UUID epic_id, EpicUpdateDTO request) {
        Epic epic = repository.findById(epic_id)
                .orElseThrow(() -> new ResourceNotFoundException("Epic not found"));
        mapper.toEntity(request, epic);
        epic.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(epic));
    }

    @Override
    public List<EpicDTO> searchEpicByTitle(String title) {
        List<Epic> epics = repository.searchByTitle(title);
        return epics.stream().map(mapper::toDto).toList();
    }
}
