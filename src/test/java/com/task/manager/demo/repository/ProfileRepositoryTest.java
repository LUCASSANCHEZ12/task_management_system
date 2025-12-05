package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Profile;
import com.task.manager.demo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.config.import=",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
@DisplayName("ProfileRepository - Integration Tests")
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    private Profile testProfile;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        profileRepository.deleteAll();
        userRepository.deleteAll();

        // Create a test user
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
        testUser.setPassword("password");
        userRepository.save(testUser);

        // Create a test profile
        testProfile = new Profile();
        testProfile.setCountry("Bolivia");
        testProfile.setAddress("Calle Principal 123");
        testProfile.setPhoneNumber("12345678");
        testProfile.setUser(testUser);
    }

    @Test
    @DisplayName("Should save a profile successfully")
    void shouldSaveProfileSuccessfully() {
        Profile savedProfile = profileRepository.save(testProfile);

        assertNotNull(savedProfile);
        assertNotNull(savedProfile.getProfileId());
        assertEquals("Bolivia", savedProfile.getCountry());
        assertEquals("Calle Principal 123", savedProfile.getAddress());
        assertEquals("12345678", savedProfile.getPhoneNumber());
        assertNotNull(savedProfile.getUser());
    }

    @Test
    @DisplayName("Should find profile by user ID")
    void shouldFindProfileByUserId() {
        Profile savedProfile = profileRepository.save(testProfile);

        Optional<Profile> foundProfile = profileRepository.findByUserId(testUser.getId());

        assertTrue(foundProfile.isPresent());
        assertEquals("Bolivia", foundProfile.get().getCountry());
        assertEquals(testUser.getId(), foundProfile.get().getUser().getId());
    }

    @Test
    @DisplayName("Should return empty Optional when profile not found")
    void shouldReturnEmptyOptionalWhenProfileNotFound() {
        UUID nonExistentUserId = UUID.randomUUID();

        Optional<Profile> foundProfile = profileRepository.findByUserId(nonExistentUserId);

        assertFalse(foundProfile.isPresent());
    }

    @Test
    @DisplayName("Should verify profile exists by user ID")
    void shouldVerifyProfileExistsByUserId() {
        profileRepository.save(testProfile);

        boolean exists = profileRepository.existsByUserId(testUser.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should verify profile does not exist by user ID")
    void shouldVerifyProfileDoesNotExistByUserId() {
        boolean exists = profileRepository.existsByUserId(UUID.randomUUID());

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete profile by user ID")
    void shouldDeleteProfileByUserId() {
        Profile savedProfile = profileRepository.save(testProfile);

        profileRepository.deleteByUserId(testUser.getId());
        Optional<Profile> deletedProfile = profileRepository.findByUserId(testUser.getId());

        assertFalse(deletedProfile.isPresent());
    }

    @Test
    @DisplayName("Should update profile successfully")
    void shouldUpdateProfileSuccessfully() {
        Profile savedProfile = profileRepository.save(testProfile);

        savedProfile.setCountry("Bolivia Updated");
        savedProfile.setAddress("Calle Principal 456");
        savedProfile.setPhoneNumber("87654321");

        Profile updatedProfile = profileRepository.save(savedProfile);

        assertEquals("Bolivia Updated", updatedProfile.getCountry());
        assertEquals("Calle Principal 456", updatedProfile.getAddress());
        assertEquals("87654321", updatedProfile.getPhoneNumber());
    }

    @Test
    @DisplayName("Should get all profiles")
    void shouldGetAllProfiles() {
        profileRepository.save(testProfile);

        // Create a second user and profile
        User secondUser = new User();
        secondUser.setName("Second User");
        secondUser.setEmail("second_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
        secondUser.setPassword("password");
        userRepository.save(secondUser);

        Profile secondProfile = new Profile();
        secondProfile.setCountry("Argentina");
        secondProfile.setAddress("Avenida Principal 456");
        secondProfile.setPhoneNumber("87654321");
        secondProfile.setUser(secondUser);
        profileRepository.save(secondProfile);

        List<Profile> allProfiles = profileRepository.findAll();

        assertEquals(2, allProfiles.size());
    }

    @Test
    @DisplayName("Should return empty list when no profiles exist")
    void shouldReturnEmptyListWhenNoProfilesExist() {
        List<Profile> allProfiles = profileRepository.findAll();

        assertNotNull(allProfiles);
        assertEquals(0, allProfiles.size());
    }

    @Test
    @DisplayName("Should save multiple profiles and retrieve them")
    void shouldSaveMultipleProfilesAndRetrieveThem() {
        for (int i = 1; i <= 3; i++) {
            User profileUser = new User();
            profileUser.setName("User " + i);
            profileUser.setEmail("user" + i + "_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
            profileUser.setPassword("password");
            userRepository.save(profileUser);

            Profile profile = new Profile();
            profile.setCountry("Country " + i);
            profile.setAddress("Address " + i);
            profile.setPhoneNumber("1234567" + i);
            profile.setUser(profileUser);
            profileRepository.save(profile);
        }

        List<Profile> allProfiles = profileRepository.findAll();

        assertEquals(3, allProfiles.size());
    }

    @Test
    @DisplayName("Should handle profile with special characters")
    void shouldHandleProfileWithSpecialCharacters() {
        Profile specialProfile = new Profile();
        specialProfile.setCountry("Country @#$ & Test");
        specialProfile.setAddress("Address @#$ & Test");
        specialProfile.setPhoneNumber("123@#$&");
        specialProfile.setUser(testUser);

        Profile savedProfile = profileRepository.save(specialProfile);

        Optional<Profile> foundProfile = profileRepository.findByUserId(testUser.getId());

        assertTrue(foundProfile.isPresent());
        assertEquals("Country @#$ & Test", foundProfile.get().getCountry());
        assertEquals("Address @#$ & Test", foundProfile.get().getAddress());
    }

    @Test
    @DisplayName("Should handle very long profile fields")
    void shouldHandleVeryLongProfileFields() {
        String longCountry = "A".repeat(100);
        String longAddress = "A".repeat(255);
        String longPhone = "1".repeat(20);

        Profile longProfile = new Profile();
        longProfile.setCountry(longCountry);
        longProfile.setAddress(longAddress);
        longProfile.setPhoneNumber(longPhone);
        longProfile.setUser(testUser);

        Profile savedProfile = profileRepository.save(longProfile);

        Optional<Profile> foundProfile = profileRepository.findByUserId(testUser.getId());

        assertTrue(foundProfile.isPresent());
        assertEquals(longCountry, foundProfile.get().getCountry());
        assertEquals(longAddress, foundProfile.get().getAddress());
        assertEquals(longPhone, foundProfile.get().getPhoneNumber());
    }

    @Test
    @DisplayName("Should find recently updated profile")
    void shouldFindRecentlyUpdatedProfile() {
        Profile savedProfile = profileRepository.save(testProfile);

        savedProfile.setCountry("Recently Updated Country");
        profileRepository.save(savedProfile);

        Optional<Profile> foundProfile = profileRepository.findByUserId(testUser.getId());

        assertTrue(foundProfile.isPresent());
        assertEquals("Recently Updated Country", foundProfile.get().getCountry());
    }
}