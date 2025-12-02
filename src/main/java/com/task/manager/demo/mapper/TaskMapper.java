package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskUpdateDTO;
import com.task.manager.demo.entity.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    @Mapping(source = "user.id", target = "user_id")
    @Mapping(source = "epic.id", target = "epic_id")
    @Mapping(source = "project.id", target = "project_id")
    TaskDTO toDto(Task entity);

    void toEntity(TaskUpdateDTO dto, @MappingTarget Task entity);
}


