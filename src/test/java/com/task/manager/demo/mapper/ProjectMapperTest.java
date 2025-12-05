package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.project.ProjectDTO;
import com.task.manager.demo.dto.project.ProjectUpdateDTO;
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
@DisplayName("ProjectMapper - Unit Tests")
class ProjectMapperTest {

    private ProjectMapper projectMapper;

    @BeforeEach
    void setUp() {
        projectMapper = Mappers.getMapper(ProjectMapper.class);
    }

    @Test
    @DisplayName("Should map Project with null values")
    void shouldMapProjectWithNullValues() {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setProjectTitle(null);
        project.setProjectDescription(null);
        project.setCreatedAt(null);
        project.setUpdatedAt(null);
        project.setDeletedAt(null);
        project.setDeletedBy(null);

        ProjectDTO projectDTO = projectMapper.toDto(project);

        assertNotNull(projectDTO);
        assertNotNull(projectDTO.id());
        assertNull(projectDTO.title());
        assertNull(projectDTO.description());
        assertNull(projectDTO.createdAt());
        assertNull(projectDTO.updatedAt());
        assertNull(projectDTO.deletedAt());
        assertNull(projectDTO.deletedBy());
    }
}