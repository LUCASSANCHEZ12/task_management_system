package com.task.manager.demo.task.repository.mapper;

import com.task.manager.demo.task.dto.TaskDTO;
import com.task.manager.demo.task.dto.TaskUpdateDTO;
import com.task.manager.demo.task.entity.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    @Mapping(source = "user.id", target = "user_id")
    TaskDTO toDto(Task entity);
    void toEntity(TaskUpdateDTO dto, @MappingTarget Task entity);
}
