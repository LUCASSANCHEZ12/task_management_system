package com.task.manager.demo.service.project;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.epic.EpicRequest;
import com.task.manager.demo.dto.epic.EpicUpdateDTO;
import com.task.manager.demo.dto.project.ProjectDTO;
import com.task.manager.demo.dto.project.ProjectRequest;
import com.task.manager.demo.dto.project.ProjectUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    ProjectDTO create(ProjectRequest request);
    ProjectDTO findById(UUID project_Id);
    List<ProjectDTO> getAll();
    List<TaskDTO> getAllTasksInProject(UUID project_Id);
    List<EpicDTO> getAllEpicsInProject(UUID project_Id);
    void deleteById(UUID project_Id, UUID requester);
    ProjectDTO update(UUID project_Id, ProjectUpdateDTO request);
    List<ProjectDTO> searchProjectByTitle(String title);
}
