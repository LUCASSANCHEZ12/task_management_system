package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.profile.ProfileDto;
import com.task.manager.demo.dto.profile.ProfileUpdateDTO;
import com.task.manager.demo.entity.Profile;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileMapper {
    ProfileDto toDto(Profile entity);

    @Mapping(target = "profileId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "user", ignore = true)
    void toEntity(ProfileUpdateDTO dto, @MappingTarget Profile entity);
}