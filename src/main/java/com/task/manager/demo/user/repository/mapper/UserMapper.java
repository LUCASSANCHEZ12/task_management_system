package com.task.manager.demo.user.repository.mapper;

import com.task.manager.demo.user.dto.UserDto;
import com.task.manager.demo.user.dto.UserUpdateDTO;
import com.task.manager.demo.user.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDto toDto(User entity);

    void toEntity(UserUpdateDTO dto, @MappingTarget User entity);
}
