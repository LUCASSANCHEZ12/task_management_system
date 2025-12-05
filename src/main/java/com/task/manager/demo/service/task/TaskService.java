package com.task.manager.demo.service.task;

import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskRequest;
import com.task.manager.demo.dto.task.TaskUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskDTO create(TaskRequest request);
    TaskDTO findById(UUID id);
    TaskDTO complete(UUID id);
    List<TaskDTO> getAll();
    List<TaskDTO> getAllUserTasks(UUID user_id);
    void deleteById(UUID id, UUID user_id);
    TaskDTO update(UUID task_id, TaskUpdateDTO request);
    List<TaskDTO> searchByTaskByTitle(String title);
    TaskDTO assignToEpic(UUID task_id, UUID epic_id);
    TaskDTO assignToUser(UUID task_id, UUID user_id);
}


