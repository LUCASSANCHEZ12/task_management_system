package com.task.manager.demo.service.epic;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.epic.EpicRequest;
import com.task.manager.demo.dto.epic.EpicUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;

import java.util.List;
import java.util.UUID;

public interface EpicService {
    EpicDTO create(EpicRequest request);
    EpicDTO findById(UUID epic_Id);
    EpicDTO complete(UUID epic_Id);
    List<EpicDTO> getAll();
    List<TaskDTO> getAllTasksInEpic(UUID epic_Id);
    List<EpicDTO> getAllEpicsInProject(UUID project_Id);
    void deleteById(UUID epic_id);
    EpicDTO update(UUID epic_id, EpicUpdateDTO request);
    List<EpicDTO> searchByTaskByTitle(String title);
}
