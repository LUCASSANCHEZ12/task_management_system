package com.task.manager.demo.service.project;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.project.ProjectDTO;
import com.task.manager.demo.dto.project.ProjectRequest;
import com.task.manager.demo.dto.project.ProjectUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.entity.Epic;
import com.task.manager.demo.entity.Project;
import com.task.manager.demo.entity.Task;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.EpicMapper;
import com.task.manager.demo.mapper.ProjectMapper;
import com.task.manager.demo.mapper.TaskMapper;
import com.task.manager.demo.repository.EpicRepository;
import com.task.manager.demo.repository.ProjectRepository;
import com.task.manager.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final EpicRepository epicRepository;
    private final EpicMapper epicMapper;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, EpicRepository epicRepository, EpicMapper epicMapper, ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.epicRepository = epicRepository;
        this.epicMapper = epicMapper;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectDTO create(ProjectRequest request) {
        Project project = Project.builder()
                .project_title(request.title())
                .project_description(request.description())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public ProjectDTO findById(UUID project_Id) {
        Project project = projectRepository.findById(project_Id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrada"));
        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDTO> getAll() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(projectMapper::toDto).toList();
    }

    @Override
    public List<TaskDTO> getAllTasksInProject(UUID projectId) {
        List<Task> tasks = taskRepository.findAllByProject_Id(projectId);
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    @Override
    public List<EpicDTO> getAllEpicsInProject(UUID project_Id){
        List<Epic> epics = epicRepository.findAllByProject_Id(project_Id);
        return epics.stream().map(epicMapper::toDto).toList();
    }


    @Override
    public void deleteById(UUID project_Id, UUID requester) {
        if (projectRepository.findById(project_Id).isEmpty()) {
            throw new ResourceNotFoundException("Proyecto no encontrada");
        }
        projectRepository.deleteById(project_Id);
    }

    @Override
    public ProjectDTO update(UUID project_Id, ProjectUpdateDTO request) {
        Project project = projectRepository.findById(project_Id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrada"));
        projectMapper.toEntity(request, project);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public List<ProjectDTO> searchProjectByTitle(String title) {
        List<Project> projects = projectRepository.searchByTitle(title);
        return projects.stream().map(projectMapper::toDto).toList();
    }
}
