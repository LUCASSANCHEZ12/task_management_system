package com.task.manager.demo.service.project;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.project.ProjectDTO;
import com.task.manager.demo.dto.project.ProjectRequest;
import com.task.manager.demo.dto.project.ProjectUpdateDTO;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.postgresql.shaded.com.ongres.scram.common.util.Preconditions.checkArgument;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final EpicRepository epicRepository;
    private final EpicMapper epicMapper;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    public ProjectServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, EpicRepository epicRepository, EpicMapper epicMapper, ProjectRepository projectRepository, ProjectMapper projectMapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.epicRepository = epicRepository;
        this.epicMapper = epicMapper;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.userRepository = userRepository;
    }

    @Override
    public ProjectDTO create(ProjectRequest request) {
        if (request.title().isBlank()) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        if (request.description().isBlank()) {
            throw new IllegalArgumentException("Description must not be blank");
        }

        if (projectRepository.existsByProjectTitle(request.title())) {
            throw new BadRequestException("Title already exists");
        }
        Project project = Project.builder()
                .projectTitle(request.title())
                .projectDescription(request.description())
                .build();
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public ProjectDTO findById(UUID project_Id) {
        Project project = projectRepository.findById(project_Id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
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
        Optional<Project> proj = projectRepository.findById(project_Id);
        if (proj.isEmpty()) {
            throw new ResourceNotFoundException("Project not found");
        }
        Optional<User> user = userRepository.findById(requester);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        proj.get().setDeletedBy(requester);
        projectRepository.save(proj.get());
        projectRepository.deleteById(project_Id);
    }

    @Override
    public ProjectDTO update(UUID project_Id, ProjectUpdateDTO request) {
        Project project = projectRepository.findById(project_Id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        projectMapper.toEntity(request, project);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public List<ProjectDTO> searchProjectByTitle(String title) {
        List<Project> projects = projectRepository.searchByTitle(title);
        return projects.stream().map(projectMapper::toDto).toList();
    }
}
