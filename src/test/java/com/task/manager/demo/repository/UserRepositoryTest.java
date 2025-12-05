package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Role;
import com.task.manager.demo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
@DisplayName("UserRepository - Integration Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        testRole = new Role();
        testRole.setName("ROLE_USER");
        testRole = roleRepository.save(testRole);

        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);
        testUser.setRoles(roles);
    }

    @Test
    @DisplayName("Should save a user successfully")
    void shouldSaveUserSuccessfully() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("Test User", savedUser.getName());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("password123", savedUser.getPassword());
        assertNotNull(savedUser.getRoles());
        assertEquals(1, savedUser.getRoles().size());
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        User savedUser = userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("Test User", foundUser.get().getName());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty Optional when user not found")
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<User> foundUser = userRepository.findById(nonExistentId);

        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("Test User", foundUser.get().getName());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty Optional when user email not found")
    void shouldReturnEmptyOptionalWhenUserEmailNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find user by email case-sensitive")
    void shouldFindUserByEmailCaseSensitive() {
        userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findByEmail("TEST@EXAMPLE.COM");

        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should delete user by ID")
    void shouldDeleteUserById() {
        User savedUser = userRepository.save(testUser);
        UUID userId = savedUser.getId();

        userRepository.deleteById(userId);
        Optional<User> deletedUser = userRepository.findById(userId);

        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        User savedUser = userRepository.save(testUser);

        savedUser.setName("Updated User");
        savedUser.setEmail("updated@example.com");
        savedUser.setPassword("newpassword123");

        User updatedUser = userRepository.save(savedUser);

        assertEquals("Updated User", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("newpassword123", updatedUser.getPassword());
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() {
        userRepository.save(testUser);

        User secondUser = new User();
        secondUser.setName("Second User");
        secondUser.setEmail("second@example.com");
        secondUser.setPassword("password456");
        Set<Role> roles = new HashSet<>();
        roles.add(testRole);
        secondUser.setRoles(roles);
        userRepository.save(secondUser);

        List<User> allUsers = userRepository.findAll();

        assertEquals(2, allUsers.size());
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsersExist() {
        List<User> allUsers = userRepository.findAll();

        assertNotNull(allUsers);
        assertEquals(0, allUsers.size());
    }

    @Test
    @DisplayName("Should save multiple users and retrieve them")
    void shouldSaveMultipleUsersAndRetrieveThem() {
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setName("User " + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword("password" + i);
            Set<Role> roles = new HashSet<>();
            roles.add(testRole);
            user.setRoles(roles);
            userRepository.save(user);
        }

        List<User> allUsers = userRepository.findAll();

        assertEquals(5, allUsers.size());
    }

    @Test
    @DisplayName("Should handle user with multiple roles")
    void shouldHandleUserWithMultipleRoles() {
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);
        roles.add(adminRole);
        testUser.setRoles(roles);

        User savedUser = userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(2, foundUser.get().getRoles().size());
        assertTrue(foundUser.get().getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_USER")));
        assertTrue(foundUser.get().getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Should handle user with special characters in email")
    void shouldHandleUserWithSpecialCharactersInEmail() {
        User specialUser = new User();
        specialUser.setName("Special User");
        specialUser.setEmail("special.user+test@example.com");
        specialUser.setPassword("password");
        Set<Role> roles = new HashSet<>();
        roles.add(testRole);
        specialUser.setRoles(roles);

        User savedUser = userRepository.save(specialUser);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("special.user+test@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should handle user with empty roles set")
    void shouldHandleUserWithEmptyRolesSet() {
        testUser.setRoles(new HashSet<>());

        User savedUser = userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getRoles());
        assertEquals(0, foundUser.get().getRoles().size());
    }

    @Test
    @DisplayName("Should handle multiple users with different emails")
    void shouldHandleMultipleUsersWithDifferentEmails() {
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        user1.setPassword("pass1");
        user1.setRoles(Set.of(testRole));
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        user2.setPassword("pass2");
        user2.setRoles(Set.of(testRole));
        userRepository.save(user2);

        List<User> allUsers = userRepository.findAll();

        assertEquals(2, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user1@example.com")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user2@example.com")));
    }

    @Test
    @DisplayName("Should find recently updated user")
    void shouldFindRecentlyUpdatedUser() {
        User savedUser = userRepository.save(testUser);
        UUID userId = savedUser.getId();

        savedUser.setName("Updated Name");
        userRepository.save(savedUser);

        Optional<User> foundUser = userRepository.findById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals("Updated Name", foundUser.get().getName());
    }
}