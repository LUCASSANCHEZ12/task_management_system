package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.user.UserDto;
import com.task.manager.demo.dto.user.UserUpdateDTO;
import com.task.manager.demo.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UserDto toDto(User entity);

    void toEntity(UserUpdateDTO dto, @MappingTarget User entity);

    @Named("roleToString")
    default String roleToString(com.task.manager.demo.entity.Role role) {
        return role != null ? role.name() : null;
    }
}

