package com.task.manager.demo.service;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.epic.EpicRequest;
import com.task.manager.demo.dto.epic.EpicUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskUpdateDTO;
import com.task.manager.demo.entity.*;
import com.task.manager.demo.exception.BadRequestException;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.EpicMapper;
import com.task.manager.demo.mapper.TaskMapper;
import com.task.manager.demo.repository.EpicRepository;
import com.task.manager.demo.repository.ProjectRepository;
import com.task.manager.demo.repository.TaskRepository;
import com.task.manager.demo.repository.UserRepository;
import com.task.manager.demo.service.epic.EpicServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EpicServiceTest {
    private UUID id;
    private EpicRequest oldEpicRequest;
    private Epic oldEpic;
    private Project oldProject;

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EpicRepository repository;

    @Mock
    private EpicMapper mapper;

    @InjectMocks
    private EpicServiceImpl service;

    @BeforeEach
    public void init()
    {
        id = UUID.randomUUID();
        oldEpic = new Epic();
        oldEpic.setId(id);
        oldEpic.setEpic_title("Epic test");
        oldEpic.setEpic_description("Epic test description");
        oldEpic.setEpic_story_points(0);
        oldEpic.setProject(new Project());
        oldProject = new Project();
        oldProject.setId(UUID.randomUUID());
        oldEpicRequest = new EpicRequest(
                oldEpic.getEpic_title(),
                oldEpic.getEpic_description(),
                oldEpic.getEpic_story_points(),
                oldProject.getId()
        );
    }
    @AfterEach
    public void tearDown() {
    oldEpic = null;
    }

    @Test
    @DisplayName("Should create epic successfully")
    void shouldCreateEpicSuccessfully() {
        EpicDTO expectedDto = new EpicDTO(id,
                "Old Epic", "Old Epic for testing",
                false, null,null,null,
                null,null,0,oldProject.getId()
        );

        when(projectRepository.findById(oldProject.getId())).thenReturn(Optional.of(oldProject));
        when(repository.save(ArgumentMatchers.any(Epic.class))).thenReturn(oldEpic);
        when(mapper.toDto(oldEpic)).thenReturn(expectedDto);

        EpicDTO result = service.create(oldEpicRequest);

        assertEquals(expectedDto.title(), result.title());
        assertEquals(expectedDto.description(), result.description());
        assertEquals(expectedDto.story_points(), result.story_points());

        verify(repository).save(ArgumentMatchers.any(Epic.class));
        verify(mapper).toDto(oldEpic);
    }

    @Test
    @DisplayName("Fail to create task with empty title")
    void shouldNotCreateTaskTestWithEmptyTitle() {
        EpicRequest request = new EpicRequest(
                "","Old Epic for testing",0,
                oldProject.getId()
        );
        when(projectRepository.findById(oldProject.getId())).thenReturn(Optional.of(oldProject));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.create(request));
        assertEquals("Title must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Fail to create task with empty description")
    void shouldNotCreateTaskTestWithEmptyDescription() {
        EpicRequest request = new EpicRequest(
                "Old Epic","",0,
                oldProject.getId()
        );
        when(projectRepository.findById(oldProject.getId())).thenReturn(Optional.of(oldProject));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.create(request));
        assertEquals("Description must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Fail to create epic with false projectId")
    void shouldNotCreateEpicTestWithFalseProjectId() {
        EpicRequest request = new EpicRequest(
                "Old Epic","",0,
                oldProject.getId()
        );
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.create(request));
        assertEquals("Project not found", exception.getMessage());

    }

    @Test
    @DisplayName("Should fail if title already exists in project")
    void shouldFailIfTitleAlreadyExists() {
        UUID projectId = UUID.randomUUID();
        EpicRequest request = new EpicRequest(
                "Old Epic","Old epic test",0,
                projectId
        );

        Project project = new Project();
        project.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(repository.existsByTitleAndProjectId(request.title(), projectId)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.create(request));
        assertEquals("Title already exists in this project", ex.getMessage());

        verify(projectRepository).findById(projectId);
        verify(repository).existsByTitleAndProjectId(request.title(), projectId);
    }

    @Test
    @DisplayName("Successfully complete an epic")
    void shouldCompleteEpic() {

        EpicDTO expectedDto = new EpicDTO(
                oldEpic.getId(),
                "Old Epic",
                "Old Epic for testing",
                true,
                null, null, null,
                null, null, 0,
                oldProject.getId()
        );

        when(repository.findById(oldEpic.getId())).thenReturn(Optional.of(oldEpic));
        when(repository.save(oldEpic)).thenReturn(oldEpic);
        when(mapper.toDto(oldEpic)).thenReturn(expectedDto);

        EpicDTO result = service.complete(oldEpic.getId());
        assertNotNull(result);
        assertTrue(result.completed());
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.title(), result.title());

        verify(repository).findById(oldEpic.getId());
        verify(repository).save(oldEpic);
        verify(mapper).toDto(oldEpic);
    }

    @Test
    @DisplayName("Fail when receiving a false epic id")
    void shouldNotCompleteAEpic() {
        UUID random = UUID.randomUUID();
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.complete(random));
        assertEquals("Epic not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should successfully return a list of epics")
    void shouldReturnAllEpics() {
        EpicDTO expectedDto = new EpicDTO(
                oldEpic.getId(),
                "Old Epic",
                "Old Epic for testing",
                true,
                null, null, null,
                null, null, 0,
                oldProject.getId()
        );
        when(repository.findAll()).thenReturn(List.of(oldEpic));
        when(mapper.toDto(oldEpic)).thenReturn(expectedDto);

        List<EpicDTO> result_epics = service.getAll();
        assertNotNull(result_epics);
        assertEquals(1, result_epics.size());
        assertEquals(expectedDto.title(), result_epics.get(0).title());
        assertEquals(expectedDto.description(), result_epics.get(0).description());
        assertEquals(expectedDto.story_points(), result_epics.get(0).story_points());
        assertEquals(expectedDto.completed(), result_epics.get(0).completed());
    }

    @Test
    @DisplayName("Should return an empty list and not null object")
    void shouldEmptyList() {
        List<EpicDTO> result_epics = service.getAll();
        assertNotNull(result_epics);
        assertEquals(0, result_epics.size());
    }

    @Test
    @DisplayName("Should find an existing epic")
    void shouldFindExistingEpic() {
        EpicDTO expectedDto = new EpicDTO(
                oldEpic.getId(),
                "Old Epic",
                "Old Epic for testing",
                true,
                null, null, null,
                null, null, 0,
                oldProject.getId()
        );
        when(repository.findById(oldEpic.getId())).thenReturn(Optional.of(oldEpic));
        when(mapper.toDto(oldEpic)).thenReturn(expectedDto);

        EpicDTO result = service.findById(oldEpic.getId());
        assertNotNull(result);
        assertEquals(expectedDto.title(), result.title());
        assertEquals(expectedDto.description(), result.description());
        assertEquals(expectedDto.story_points(), result.story_points());
        assertEquals(expectedDto.completed(), result.completed());
    }

    @Test
    @DisplayName("Should not find a nonexisting epic")
    void shouldReturnNotFoundEpic() {
        UUID random = UUID.randomUUID();
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.findById(random));
        assertEquals("Epic not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should get all tasks assigned to an epic")
    void shouldGetAllTaskAssignedToEpic() {
        UUID epicId = oldEpic.getId();
        Task task1 = Task.builder()
                .id(id)
                .title("New title")
                .description("New description")
                .story_points(5)
                .epic(oldEpic)
                .project(oldProject)
                .type(Type_Enum.TASK)
                .build();

        TaskDTO expectedTaskDto = new TaskDTO(
                id,
                "New title",
                "New description",
                false,
                null, null, null,
                5,
                Type_Enum.TASK,
                epicId,
                null, null,
                oldProject.getId()
        );

        when(repository.findById(epicId)).thenReturn(Optional.of(oldEpic));
        when(taskRepository.findAllByEpic_Id(epicId)).thenReturn(List.of(task1));
        when(taskMapper.toDto(task1)).thenReturn(expectedTaskDto);

        List<TaskDTO> result_tasks = service.getAllTasksInEpic(epicId);

        assertNotNull(result_tasks);
        assertEquals(1, result_tasks.size());
        assertEquals(expectedTaskDto.id(), result_tasks.get(0).id());
        assertEquals(expectedTaskDto.title(), result_tasks.get(0).title());

        verify(repository).findById(epicId);
        verify(taskRepository).findAllByEpic_Id(epicId);
        verify(taskMapper).toDto(task1);
    }

    @Test
    @DisplayName("Should throw exception if epic not found")
    void shouldThrowWhenEpicNotFound() {
        UUID epicId = UUID.randomUUID();

        when(repository.findById(epicId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.findById(epicId));
        assertEquals("Epic not found", ex.getMessage());

        verify(repository).findById(epicId);
        verify(taskRepository, never()).findAllByEpic_Id(any());
        verify(taskMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should return list of EpicDTO when searching by title")
    void shouldReturnEpicsMatchingTitle() {
        String title = "Epic Test";

        Epic epic1 = Epic.builder().id(UUID.randomUUID()).epic_title("Epic Test A").build();
        Epic epic2 = Epic.builder().id(UUID.randomUUID()).epic_title("Epic Test B").build();

        EpicDTO dto1 = new EpicDTO(
                epic1.getId(),
                epic1.getEpic_title(),
                epic1.getEpic_description(),
                false,
                null, null, null,
                null, null, 0,
                oldProject.getId()
        );
        EpicDTO dto2 = new EpicDTO(
                epic2.getId(),
                epic2.getEpic_title(),
                epic2.getEpic_description(),
                false,
                null, null, null,
                null, null, 0,
                oldProject.getId()
        );

        List<Epic> epics = List.of(epic1, epic2);
        List<EpicDTO> expected = List.of(dto1, dto2);

        when(repository.searchByTitle(title)).thenReturn(epics);
        when(mapper.toDto(epic1)).thenReturn(dto1);
        when(mapper.toDto(epic2)).thenReturn(dto2);

        List<EpicDTO> result = service.searchEpicByTitle(title);

        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);

        verify(repository).searchByTitle(title);
        verify(mapper).toDto(epic1);
        verify(mapper).toDto(epic2);
    }

    @Test
    @DisplayName("Should return empty list if no epics match title")
    void shouldReturnEmptyListIfNoEpicFound() {
        String title = "Nonexistent";

        when(repository.searchByTitle(title)).thenReturn(List.of());

        List<EpicDTO> result = service.searchEpicByTitle(title);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).searchByTitle(title);
        verify(mapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should successfully delete an epic")
    void shouldDeleteEpicSuccessfully() {
        UUID requesterId = UUID.randomUUID();
        User user = User.builder().id(requesterId).build();

        when(repository.findById(oldEpic.getId()))
                .thenReturn(Optional.of(oldEpic));
        when(userRepository.findById(requesterId))
                .thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> service.deleteById(oldEpic.getId(), requesterId));
        assertEquals(requesterId, oldEpic.getDeletedBy());

        verify(repository).findById(oldEpic.getId());
        verify(repository).save(oldEpic);
        verify(repository).deleteById(oldEpic.getId());
    }

    @Test
    @DisplayName("Fail to delete a epic with false user_id")
    void shouldNotDeleteProjectByUserId() {
        UUID random = UUID.randomUUID();

        when(repository.findById(oldEpic.getId()))
                .thenReturn(Optional.of(oldEpic));
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.deleteById(oldEpic.getId(), random));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    @DisplayName("Fail to delete a epic with false epic_id")
    void shouldNotDeleteProjectByEpicId() {
        UUID random = UUID.randomUUID();
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.deleteById(random, random));
        assertEquals("Epic not found", ex.getMessage());
    }
    @Test
    @DisplayName("Successfully update an epic")
    void shouldUpdateEpic() {
        EpicUpdateDTO request = new EpicUpdateDTO(
                "New title",
                "New description",
                5
        );

        Epic updatedEpic = new Epic();
        updatedEpic.setId(id);
        updatedEpic.setEpic_title("New title");

        EpicDTO expectedDto = new EpicDTO(
                oldEpic.getId(),
                "New title",
                "New description",
                false,
                null, null, null,
                null, null, 5,
                oldProject.getId()
        );

        when(repository.findById(id)).thenReturn(Optional.of(oldEpic));
        doAnswer(invocation -> {
            EpicUpdateDTO req = invocation.getArgument(0);
            Epic target = invocation.getArgument(1);

            target.setEpic_title(req.title());
            target.setEpic_description(req.description());
            target.setEpic_story_points(req.story_points());
            return null;
        }).when(mapper).toEntity(request, oldEpic);

        when(repository.save(oldEpic)).thenReturn(oldEpic);
        when(mapper.toDto(oldEpic)).thenReturn(expectedDto);

        EpicDTO result = service.update(id, request);

        assertNotNull(result);
        assertEquals("New title", result.title());
        assertEquals("New description", result.description());
        assertEquals(5, result.story_points());

        verify(repository).findById(id);
        verify(mapper).toEntity(request, oldEpic);
        verify(repository).save(oldEpic);
        verify(mapper).toDto(oldEpic);
    }

    @Test
    @DisplayName("Fail to update nonexisting epic")
    void shouldNotUpdateEpic() {
        UUID random = UUID.randomUUID();
        EpicUpdateDTO request = new EpicUpdateDTO(
                "New title",
                "New description",
                0
        );
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.update(random, request));
        assertEquals("Epic not found", ex.getMessage());
    }
}
