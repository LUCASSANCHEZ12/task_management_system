package com.task.manager.demo.service;

import com.task.manager.demo.dto.profile.ProfileDto;
import com.task.manager.demo.dto.profile.ProfileUpdateDTO;
import com.task.manager.demo.entity.Profile;
import com.task.manager.demo.entity.User;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.ProfileMapper;
import com.task.manager.demo.repository.ProfileRepository;
import com.task.manager.demo.repository.UserRepository;
import com.task.manager.demo.service.profile.ProfileServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {
    private UUID userId;
    private ProfileUpdateDTO profileUpdateDTO;
    private Profile existingProfile;
    private User existingUser;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileServiceImpl service;

    @BeforeEach
    public void init() {
        userId = UUID.randomUUID();
        existingUser = User.builder().id(userId).build();
        existingProfile = new Profile();
        existingProfile.setProfileId(UUID.randomUUID());
        existingProfile.setUser(existingUser);
        existingProfile.setCountry("Bolivia");
        existingProfile.setAddress("Calle Principal 123");
        existingProfile.setPhoneNumber("12345678");

        profileUpdateDTO = new ProfileUpdateDTO(
                "Bolivia",
                "Calle Principal 123",
                "12345678"
        );
    }

    @AfterEach
    public void tearDown() {
        existingProfile = null;
        existingUser = null;
    }

    @Test
    @DisplayName("Find profile by user ID successfully")
    void shouldFindProfileByUserId() {
        ProfileDto expectedDto = new ProfileDto(
                existingProfile.getProfileId(),
                "Bolivia",
                "Calle Principal 123",
                "12345678",
                existingProfile.getCreatedAt(),
                existingProfile.getUpdatedAt()
        );

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(existingProfile));
        when(profileMapper.toDto(existingProfile)).thenReturn(expectedDto);

        ProfileDto result = service.findByUserId(userId);

        assertNotNull(result);
        assertEquals(expectedDto.country(), result.country());
        assertEquals(expectedDto.address(), result.address());
        assertEquals(expectedDto.phoneNumber(), result.phoneNumber());

        verify(profileRepository).findByUserId(userId);
        verify(profileMapper).toDto(existingProfile);
    }

    @Test
    @DisplayName("Fail to find profile for non-existent user")
    void shouldNotFindProfileForNonExistentUser() {
        UUID randomUserId = UUID.randomUUID();
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.findByUserId(randomUserId));
        assertEquals("Profile not found for user", exception.getMessage());
    }

    @Test
    @DisplayName("Create new profile successfully")
    void shouldCreateNewProfile() {
        ProfileDto expectedDto = new ProfileDto(
                UUID.randomUUID(),
                "Bolivia",
                "Calle Principal 123",
                "12345678",
                null,
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(profileRepository.existsByUserId(userId)).thenReturn(false);
        when(profileRepository.save(ArgumentMatchers.any(Profile.class))).thenReturn(existingProfile);
        when(profileMapper.toDto(existingProfile)).thenReturn(expectedDto);

        ProfileDto result = service.createOrUpdateProfile(userId, profileUpdateDTO);

        assertNotNull(result);
        assertEquals(expectedDto.country(), result.country());
        assertEquals(expectedDto.address(), result.address());
        assertEquals(expectedDto.phoneNumber(), result.phoneNumber());

        verify(userRepository).findById(userId);
        verify(profileRepository).existsByUserId(userId);
        verify(profileRepository).save(ArgumentMatchers.any(Profile.class));
        verify(profileMapper).toDto(existingProfile);
    }

    @Test
    @DisplayName("Update existing profile successfully")
    void shouldUpdateExistingProfile() {
        ProfileDto expectedDto = new ProfileDto(
                existingProfile.getProfileId(),
                "Bolivia Updated",
                "Calle Principal 456",
                "87654321",
                existingProfile.getCreatedAt(),
                existingProfile.getUpdatedAt()
        );

        ProfileUpdateDTO updateRequest = new ProfileUpdateDTO(
                "Bolivia Updated",
                "Calle Principal 456",
                "87654321"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(profileRepository.existsByUserId(userId)).thenReturn(true);
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(existingProfile));

        doAnswer(invocation -> {
            ProfileUpdateDTO req = invocation.getArgument(0);
            Profile target = invocation.getArgument(1);
            target.setCountry(req.country());
            target.setAddress(req.address());
            target.setPhoneNumber(req.phoneNumber());
            return null;
        }).when(profileMapper).toEntity(updateRequest, existingProfile);

        when(profileRepository.save(existingProfile)).thenReturn(existingProfile);
        when(profileMapper.toDto(existingProfile)).thenReturn(expectedDto);

        ProfileDto result = service.createOrUpdateProfile(userId, updateRequest);

        assertNotNull(result);
        assertEquals(expectedDto.country(), result.country());
        assertEquals(expectedDto.address(), result.address());
        assertEquals(expectedDto.phoneNumber(), result.phoneNumber());

        verify(userRepository).findById(userId);
        verify(profileRepository).existsByUserId(userId);
        verify(profileRepository).findByUserId(userId);
        verify(profileMapper).toEntity(updateRequest, existingProfile);
        verify(profileRepository).save(existingProfile);
        verify(profileMapper).toDto(existingProfile);
    }

    @Test
    @DisplayName("Fail to create profile when user not found")
    void shouldFailWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.createOrUpdateProfile(userId, profileUpdateDTO));
        assertEquals("User not found", ex.getMessage());

        verify(userRepository).findById(userId);
        verify(profileRepository, never()).existsByUserId(any());
        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete profile successfully")
    void shouldDeleteProfile() {
        when(profileRepository.existsByUserId(userId)).thenReturn(true);

        assertDoesNotThrow(() -> service.deleteByUserId(userId));

        verify(profileRepository).existsByUserId(userId);
        verify(profileRepository).deleteByUserId(userId);
    }

    @Test
    @DisplayName("Fail to delete profile when profile not found")
    void shouldNotDeleteProfileWhenNotFound() {
        when(profileRepository.existsByUserId(userId)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.deleteByUserId(userId));
        assertEquals("Profile not found for user", ex.getMessage());

        verify(profileRepository).existsByUserId(userId);
        verify(profileRepository, never()).deleteByUserId(any());
    }

    @Test
    @DisplayName("Check if profile exists for user")
    void shouldCheckProfileExists() {
        when(profileRepository.existsByUserId(userId)).thenReturn(true);

        boolean result = service.existsByUserId(userId);

        assertEquals(true, result);
        verify(profileRepository).existsByUserId(userId);
    }

    @Test
    @DisplayName("Check if profile does not exist for user")
    void shouldCheckProfileDoesNotExist() {
        when(profileRepository.existsByUserId(userId)).thenReturn(false);

        boolean result = service.existsByUserId(userId);

        assertEquals(false, result);
        verify(profileRepository).existsByUserId(userId);
    }
}