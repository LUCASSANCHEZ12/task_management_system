package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.project.ProjectDTO;
import com.task.manager.demo.dto.project.ProjectUpdateDTO;
import com.task.manager.demo.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {
    @Mapping(source = "projectTitle", target = "title")
    @Mapping(source = "projectDescription", target = "description")
    ProjectDTO toDto(Project entity);

    void toEntity(ProjectUpdateDTO dto, @MappingTarget Project entity);
}