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

import static org.postgresql.shaded.com.ongres.scram.common.util.Preconditions.checkArgument;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final EpicRepository epicRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper mapper;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository repository, EpicRepository epicRepository, ProjectRepository projectRepository, TaskMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.epicRepository = epicRepository;
        this.projectRepository = projectRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

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

    @Override
    public TaskDTO findById(UUID id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return mapper.toDto(task);
    }

    @Override
    public TaskDTO complete(UUID id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setCompleted(true);
        task.setFinishedAt(LocalDateTime.now());

        return mapper.toDto(repository.save(task));
    }

    @Override
    public List<TaskDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<TaskDTO> getAllUserTasks(UUID user_id) {
        if (userRepository.findById(user_id).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        List<Task> tasks = repository.findAllByUser_Id(user_id);
        return tasks.stream().map(mapper::toDto).toList();
    }

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

    @Override
    public TaskDTO update(UUID task_id, TaskUpdateDTO request) {
        Task task = repository.findById(task_id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        mapper.toEntity(request, task);
        task.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(task));
    }

    @Override
    public List<TaskDTO> searchByTaskByTitle(String title) {
        List<Task> tasks = repository.searchByTitle(title);
        return tasks.stream().map(mapper::toDto).toList();
    }

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


