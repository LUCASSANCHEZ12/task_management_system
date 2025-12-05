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

/**
 * Implementation of the {@link ProfileService} interface that manages user profile
 * operations such as retrieval, creation, update, and deletion.
 * <p>
 * This service interacts with the persistence layer through repositories and uses
 * mappers to convert entities to their respective DTO representations. It also
 * ensures that profiles are correctly associated with existing users.
 * </p>
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    /**
     * Constructs a new {@code ProfileServiceImpl} with the required dependencies.
     *
     * @param profileRepository repository for profile persistence
     * @param userRepository    repository for user persistence
     * @param profileMapper     mapper for converting Profile entities and DTOs
     */
    public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository, ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.profileMapper = profileMapper;
    }

    /**
     * Retrieves a user profile using the user's unique identifier.
     *
     * @param userId the UUID of the user whose profile is requested
     * @return a {@link ProfileDto} representing the user's profile
     * @throws ResourceNotFoundException if no profile exists for the user
     */
    @Override
    public ProfileDto findByUserId(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user"));
        return profileMapper.toDto(profile);
    }

    /**
     * Retrieves a user profile using the user's unique identifier.
     *
     * @param userId the UUID of the user whose profile is requested
     * @return a {@link ProfileDto} representing the user's profile
     * @throws ResourceNotFoundException if no profile exists for the user
     */
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

    /**
     * Deletes a profile associated with the given user ID.
     *
     * @param userId the UUID of the user whose profile is being deleted
     * @throws ResourceNotFoundException if no profile exists for the given user
     */
    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        if (!profileRepository.existsByUserId(userId)) {
            throw new ResourceNotFoundException("Profile not found for user");
        }
        profileRepository.deleteByUserId(userId);
    }

    /**
     * Checks whether a profile exists for the specified user.
     *
     * @param userId the UUID of the user to check
     * @return true if the user has an associated profile, false otherwise
     */
    @Override
    public boolean existsByUserId(UUID userId) {
        return profileRepository.existsByUserId(userId);
    }
}