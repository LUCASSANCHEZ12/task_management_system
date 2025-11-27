package com.task.manager.demo.service.task;

import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskRequest;
import com.task.manager.demo.dto.task.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    TaskDTO create(TaskRequest request);
    TaskDTO findById(Long id);
    TaskDTO complete(Long id);
    List<TaskDTO> getAll();
    List<TaskDTO> getAllUserToDo(Long user_id);
    void deleteById(Long id);
    TaskDTO update(Long todo_id, TaskUpdateDTO request);
}


