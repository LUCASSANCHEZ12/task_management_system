package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.epic.EpicUpdateDTO;
import com.task.manager.demo.entity.Epic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EpicMapper {
    @Mapping(source = "project.id", target = "project_id")
    EpicDTO toDto(Epic entity);

    void toEntity(EpicUpdateDTO dto, @MappingTarget Epic entity);
}
