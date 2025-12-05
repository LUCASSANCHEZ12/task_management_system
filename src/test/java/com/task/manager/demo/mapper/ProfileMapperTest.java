package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.profile.ProfileDto;
import com.task.manager.demo.dto.profile.ProfileUpdateDTO;
import com.task.manager.demo.entity.Profile;
import com.task.manager.demo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileMapper - Unit Tests")
class ProfileMapperTest {

    private ProfileMapper profileMapper;

    @BeforeEach
    void setUp() {
        profileMapper = Mappers.getMapper(ProfileMapper.class);
    }

    @Test
    @DisplayName("Should map Profile with null values")
    void shouldMapProfileWithNullValues() {
        Profile profile = new Profile();
        profile.setProfileId(UUID.randomUUID());
        profile.setCountry(null);
        profile.setAddress(null);
        profile.setPhoneNumber(null);
        profile.setCreatedAt(null);
        profile.setUpdatedAt(null);
        profile.setDeletedAt(null);
        profile.setDeletedBy(null);
        profile.setUser(null);

        ProfileDto profileDto = profileMapper.toDto(profile);

        assertNotNull(profileDto);
        assertNotNull(profileDto.profileId());
        assertNull(profileDto.country());
        assertNull(profileDto.address());
        assertNull(profileDto.phoneNumber());
        assertNull(profileDto.createdAt());
        assertNull(profileDto.updatedAt());
    }

    @Test
    @DisplayName("Should map Profile with valid values")
    void shouldMapProfileWithValidValues() {
        UUID profileId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Profile profile = new Profile();
        profile.setProfileId(profileId);
        profile.setCountry("Bolivia");
        profile.setAddress("Calle Principal 123");
        profile.setPhoneNumber("12345678");
        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);

        ProfileDto profileDto = profileMapper.toDto(profile);

        assertNotNull(profileDto);
        assertEquals(profileId, profileDto.profileId());
        assertEquals("Bolivia", profileDto.country());
        assertEquals("Calle Principal 123", profileDto.address());
        assertEquals("12345678", profileDto.phoneNumber());
        assertEquals(now, profileDto.createdAt());
        assertEquals(now, profileDto.updatedAt());
    }

    @Test
    @DisplayName("Should map ProfileUpdateDTO to existing Profile entity")
    void shouldMapProfileUpdateDTOToExistingProfile() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Profile existingProfile = new Profile();
        existingProfile.setProfileId(UUID.randomUUID());
        existingProfile.setCountry("Old Country");
        existingProfile.setAddress("Old Address");
        existingProfile.setPhoneNumber("11111111");
        existingProfile.setUser(user);

        ProfileUpdateDTO updateDTO = new ProfileUpdateDTO(
                "New Country",
                "New Address",
                "22222222"
        );

        profileMapper.toEntity(updateDTO, existingProfile);

        assertEquals("New Country", existingProfile.getCountry());
        assertEquals("New Address", existingProfile.getAddress());
        assertEquals("22222222", existingProfile.getPhoneNumber());
        assertEquals(user, existingProfile.getUser());
    }

    @Test
    @DisplayName("Should map ProfileUpdateDTO with null values to existing Profile")
    void shouldMapProfileUpdateDTOWithNullValuesToExistingProfile() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Profile existingProfile = new Profile();
        existingProfile.setProfileId(UUID.randomUUID());
        existingProfile.setCountry("Old Country");
        existingProfile.setAddress("Old Address");
        existingProfile.setPhoneNumber("11111111");
        existingProfile.setUser(user);

        ProfileUpdateDTO updateDTO = new ProfileUpdateDTO(
                null,
                null,
                null
        );

        profileMapper.toEntity(updateDTO, existingProfile);

        assertEquals("Old Country", existingProfile.getCountry());
        assertEquals("Old Address", existingProfile.getAddress());
        assertEquals("11111111", existingProfile.getPhoneNumber());
        assertEquals(user, existingProfile.getUser());
    }

    @Test
    @DisplayName("Should map ProfileUpdateDTO with partial null values")
    void shouldMapProfileUpdateDTOWithPartialNullValues() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Profile existingProfile = new Profile();
        existingProfile.setProfileId(UUID.randomUUID());
        existingProfile.setCountry("Old Country");
        existingProfile.setAddress("Old Address");
        existingProfile.setPhoneNumber("11111111");
        existingProfile.setUser(user);

        ProfileUpdateDTO updateDTO = new ProfileUpdateDTO(
                "New Country",
                null,
                "22222222"
        );

        profileMapper.toEntity(updateDTO, existingProfile);

        assertEquals("New Country", existingProfile.getCountry());
        assertEquals("Old Address", existingProfile.getAddress());
        assertEquals("22222222", existingProfile.getPhoneNumber());
        assertEquals(user, existingProfile.getUser());
    }
}