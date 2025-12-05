package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.epic.EpicUpdateDTO;
import com.task.manager.demo.entity.Epic;
import com.task.manager.demo.entity.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EpicMapper - Unit Tests")
class EpicMapperTest {

    private EpicMapper epicMapper;

    @BeforeEach
    void setUp() {
        epicMapper = Mappers.getMapper(EpicMapper.class);
    }

    @Test
    @DisplayName("Should map Epic with completed status")
    void shouldMapEpicWithCompletedStatus() {
        // Test completed epic
        Epic completedEpic = new Epic();
        completedEpic.setId(UUID.randomUUID());
        completedEpic.setEpicTitle("Completed Epic");
        completedEpic.setEpicDescription("Completed epic description");
        completedEpic.setCompleted(true);
        completedEpic.setFinishedAt(LocalDateTime.now());

        EpicDTO completedEpicDTO = epicMapper.toDto(completedEpic);
        assertTrue(completedEpicDTO.completed());
        assertNotNull(completedEpicDTO.finishedAt());

        // Test incomplete epic
        Epic incompleteEpic = new Epic();
        incompleteEpic.setId(UUID.randomUUID());
        incompleteEpic.setEpicTitle("Incomplete Epic");
        incompleteEpic.setEpicDescription("Incomplete epic description");
        incompleteEpic.setCompleted(false);
        incompleteEpic.setFinishedAt(null);

        EpicDTO incompleteEpicDTO = epicMapper.toDto(incompleteEpic);
        assertFalse(incompleteEpicDTO.completed());
        assertNull(incompleteEpicDTO.finishedAt());
    }

    @Test
    @DisplayName("Should map Epic with null values")
    void shouldMapEpicWithNullValues() {
        Epic epic = new Epic();
        epic.setId(UUID.randomUUID());
        epic.setEpicTitle(null);
        epic.setEpicDescription(null);
        epic.setEpicStoryPoints(0);
        epic.setCompleted(false);
        epic.setFinishedAt(null);
        epic.setProject(null);

        EpicDTO epicDTO = epicMapper.toDto(epic);

        assertNotNull(epicDTO);
        assertNotNull(epicDTO.id());
        assertNull(epicDTO.title());
        assertNull(epicDTO.description());
        assertEquals(0, epicDTO.story_points());
        assertFalse(epicDTO.completed());
        assertNull(epicDTO.finishedAt());
        assertNull(epicDTO.project_id());
    }
}