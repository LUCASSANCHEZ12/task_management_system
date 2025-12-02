package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.user.UserDto;
import com.task.manager.demo.dto.user.UserUpdateDTO;
import com.task.manager.demo.entity.User;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStringList")
    UserDto toDto(User entity);

    void toEntity(UserUpdateDTO dto, @MappingTarget User entity);

    @Named("rolesToStringList")
    default List<String> rolesToStringList(Set<com.task.manager.demo.entity.Role> roles) {
        return roles != null ? roles.stream()
                .map(com.task.manager.demo.entity.Role::getName)
                .collect(Collectors.toList()) : null;
    }
}

