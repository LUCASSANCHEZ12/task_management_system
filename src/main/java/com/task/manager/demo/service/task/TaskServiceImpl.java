package com.task.manager.demo.service.task;

import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskRequest;
import com.task.manager.demo.dto.task.TaskUpdateDTO;
import com.task.manager.demo.entity.Task;
import com.task.manager.demo.entity.User;
import com.task.manager.demo.exception.BadRequestException;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.TaskMapper;
import com.task.manager.demo.repository.TaskRepository;
import com.task.manager.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository repository, TaskMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    public TaskDTO create(TaskRequest request) {
        Task task = Task.builder()
                .title(request.title())
                .type(request.type())
                .description(request.description())
                .user(null)
                .epic(null)
                .task_parent(null)
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return mapper.toDto(repository.save(task));
    }

    @Override
    public TaskDTO findById(UUID id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
        return mapper.toDto(task);
    }

    @Override
    public TaskDTO complete(UUID id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

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
    public List<TaskDTO> getAllUserToDo(UUID user_id) {
        if (userRepository.findById(user_id).isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        List<Task> tasks = repository.findAllByUser_Id(user_id);
        return tasks.stream().map(mapper::toDto).toList();
    }

    @Override
    public void deleteById(UUID id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Tarea no encontrada");
        }
        repository.deleteById(id);
    }

    @Override
public TaskDTO update(UUID task_id, TaskUpdateDTO request) {
        Task task = repository.findById(task_id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        mapper.toEntity(request, task);
        task.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(task));
    }

    @Override
    public List<TaskDTO> searchByTaskByTitle(String title) {
        List<Task> tasks = repository.searchByTitle(title);
        return tasks.stream().map(mapper::toDto).toList();
    }
}


