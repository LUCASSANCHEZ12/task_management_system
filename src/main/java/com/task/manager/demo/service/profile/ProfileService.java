package com.task.manager.demo.service.profile;

import com.task.manager.demo.dto.profile.ProfileDto;
import com.task.manager.demo.dto.profile.ProfileUpdateDTO;

import java.util.UUID;

public interface ProfileService {
    ProfileDto findByUserId(UUID userId);
    ProfileDto createOrUpdateProfile(UUID userId, ProfileUpdateDTO request);
    void deleteByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}