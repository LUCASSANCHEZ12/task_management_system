package com.task.manager.demo.task.services;

import com.task.manager.demo.task.dto.TaskDTO;
import com.task.manager.demo.task.dto.TaskRequest;
import com.task.manager.demo.task.dto.TaskUpdateDTO;
import com.task.manager.demo.task.entity.Task;
import com.task.manager.demo.task.repository.TaskRepository;
import com.task.manager.demo.task.repository.mapper.TaskMapper;
import com.task.manager.demo.user.controller.advice.exceptions.NullIDException;
import com.task.manager.demo.user.controller.advice.exceptions.UserException;
import com.task.manager.demo.user.entity.User;
import com.task.manager.demo.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class ImplTaskService implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final UserRepository userRepository;

    public ImplTaskService(TaskRepository repository, TaskMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    public TaskDTO create(TaskRequest request) {
        if(userRepository.findById(request.user_id()).isEmpty()) {
            throw new UserException("User not found");
        }
        User u = userRepository.findById(request.user_id()).get();
        var todo = Task.builder()
                .description(request.description())
                .user(u)
                .completed(false)
                .createdTime(LocalTime.now())
                .finishedTime(null)
                .build();
        repository.save(todo);
        return mapper.toDto(todo);
    }

    @Override
    public TaskDTO findById(Long id) {
        return null;
    }

    @Override
    public TaskDTO complete(Long id) {
        if(repository.findById(id).isEmpty()) {
            throw new NullIDException("Task not found");
        }
        Task todo = repository.findById(id).get();
        todo.setCompleted(true);
        todo.setFinishedTime(LocalTime.now());
        return mapper.toDto(todo);
    }

    @Override
    public List<TaskDTO> getAll() {
        return List.of();
    }

    @Override
    public List<TaskDTO> getAllUserToDo(Long user_id) {
        if(userRepository.findById(user_id).isEmpty()) {
            throw new UserException("User not found");
        }
        User u = userRepository.findById(user_id).get();
        List<Task> tasks = repository.findAllByUser_Id(user_id);
        return tasks.stream().map(mapper::toDto).toList();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public TaskDTO update(Long todo_id, TaskUpdateDTO request) {
        return null;
    }
}
