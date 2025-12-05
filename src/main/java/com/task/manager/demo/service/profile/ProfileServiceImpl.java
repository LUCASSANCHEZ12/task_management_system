package com.task.manager.demo.service.profile;

import com.task.manager.demo.dto.profile.ProfileDto;
import com.task.manager.demo.dto.profile.ProfileUpdateDTO;
import com.task.manager.demo.entity.Profile;
import com.task.manager.demo.entity.User;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.ProfileMapper;
import com.task.manager.demo.repository.ProfileRepository;
import com.task.manager.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository, ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.profileMapper = profileMapper;
    }

    @Override
    public ProfileDto findByUserId(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user"));
        return profileMapper.toDto(profile);
    }

    @Override
    @Transactional
    public ProfileDto createOrUpdateProfile(UUID userId, ProfileUpdateDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isNewProfile = !profileRepository.existsByUserId(userId);

        Profile profile = profileRepository.findByUserId(userId).orElseGet(() -> {
            Profile newProfile = new Profile();
            newProfile.setUser(user);
            return newProfile;
        });

        profileMapper.toEntity(request, profile);
        Profile savedProfile = profileRepository.save(profile);

        return profileMapper.toDto(savedProfile);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        if (!profileRepository.existsByUserId(userId)) {
            throw new ResourceNotFoundException("Profile not found for user");
        }
        profileRepository.deleteByUserId(userId);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return profileRepository.existsByUserId(userId);
    }
}