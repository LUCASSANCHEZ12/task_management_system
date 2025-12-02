package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.project.ProjectDTO;
import com.task.manager.demo.dto.project.ProjectUpdateDTO;
import com.task.manager.demo.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {
    //@Mapping(source = "user.id", target = "user_id")
    ProjectDTO toDto(Project entity);

    void toEntity(ProjectUpdateDTO dto, @MappingTarget Project entity);
}