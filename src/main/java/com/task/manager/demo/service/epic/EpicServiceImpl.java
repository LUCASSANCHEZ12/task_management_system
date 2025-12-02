package com.task.manager.demo.service.epic;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.epic.EpicRequest;
import com.task.manager.demo.dto.epic.EpicUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.entity.Epic;
import com.task.manager.demo.entity.Project;
import com.task.manager.demo.entity.Task;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.EpicMapper;
import com.task.manager.demo.mapper.TaskMapper;
import com.task.manager.demo.repository.EpicRepository;
import com.task.manager.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EpicServiceImpl implements EpicService{

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final EpicRepository repository;
    private final EpicMapper mapper;

    public EpicServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, EpicRepository repository, EpicMapper mapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public EpicDTO create(EpicRequest request) {
        Epic epic = Epic.builder()
                .epic_title(request.title())
                .epic_description(request.description())
                .epic_story_points(request.story_points())
                .completed(false)
                .deleted(false)
                .deletedAt(null)
                .deletedBy(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .finishedAt(null)
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
        return null;
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
        Epic epic = repository.findById(epic_id)
                .orElseThrow(() -> new ResourceNotFoundException("Epic not found"));
        epic.setDeletedBy(requester);
        repository.save(epic);
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
        List<Epic> projects = repository.searchByTitle(title);
        return projects.stream().map(mapper::toDto).toList();
    }
}
