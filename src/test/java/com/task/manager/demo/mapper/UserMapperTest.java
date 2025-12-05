package com.task.manager.demo.mapper;

import com.task.manager.demo.dto.user.UserDto;
import com.task.manager.demo.dto.user.UserUpdateDTO;
import com.task.manager.demo.entity.Role;
import com.task.manager.demo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserMapper - Unit Tests")
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    @DisplayName("Should map User entity to UserDto")
    void shouldMapUserEntityToUserDto() {
        // Create test data
        UUID userId = UUID.randomUUID();
        String name = "Test User";
        String email = "test@example.com";

        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, "ROLE_USER"));
        roles.add(new Role(2L, "ROLE_ADMIN"));

        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setRoles(roles);

        // Map to DTO
        UserDto userDto = userMapper.toDto(user);

        // Verify mapping
        assertNotNull(userDto);
        assertEquals(userId, userDto.id());
        assertEquals(name, userDto.name());
        assertEquals(email, userDto.email());
        assertNotNull(userDto.roles());
        assertEquals(2, userDto.roles().size());
        assertTrue(userDto.roles().contains("ROLE_USER"));
        assertTrue(userDto.roles().contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Should map User entity with empty roles to UserDto")
    void shouldMapUserEntityWithEmptyRolesToUserDto() {
        // Create test data
        UUID userId = UUID.randomUUID();
        String name = "Test User";
        String email = "test@example.com";

        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setRoles(new HashSet<>());

        // Map to DTO
        UserDto userDto = userMapper.toDto(user);

        // Verify mapping
        assertNotNull(userDto);
        assertEquals(userId, userDto.id());
        assertEquals(name, userDto.name());
        assertEquals(email, userDto.email());
        assertNotNull(userDto.roles());
        assertTrue(userDto.roles().isEmpty());
    }

    @Test
    @DisplayName("Should map User entity with null roles to UserDto")
    void shouldMapUserEntityWithNullRolesToUserDto() {
        // Create test data
        UUID userId = UUID.randomUUID();
        String name = "Test User";
        String email = "test@example.com";

        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setRoles(null);

        // Map to DTO
        UserDto userDto = userMapper.toDto(user);

        // Verify mapping
        assertNotNull(userDto);
        assertEquals(userId, userDto.id());
        assertEquals(name, userDto.name());
        assertEquals(email, userDto.email());
        assertNull(userDto.roles());
    }

    @Test
    @DisplayName("Should map UserUpdateDTO to existing User entity")
    void shouldMapUserUpdateDTOToExistingUserEntity() {
        // Create existing user
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldpassword");

        Set<Role> oldRoles = new HashSet<>();
        oldRoles.add(new Role(1L, "ROLE_USER"));
        existingUser.setRoles(oldRoles);

        // Create update DTO
        UserUpdateDTO updateDTO = new UserUpdateDTO(
                "New Name",
                "new@example.com",
                "newpassword",
                List.of("ROLE_ADMIN", "ROLE_MANAGER")
        );

        // Map to existing entity
        userMapper.toEntity(updateDTO, existingUser);

        // Verify mapping
        assertEquals("New Name", existingUser.getName());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("newpassword", existingUser.getPassword());
        assertNotNull(existingUser.getRoles());
        assertEquals(2, existingUser.getRoles().size());

        // Check that roles were mapped correctly
        boolean hasAdminRole = existingUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        boolean hasManagerRole = existingUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_MANAGER"));

        assertTrue(hasAdminRole);
        assertTrue(hasManagerRole);
    }

    @Test
    @DisplayName("Should map UserUpdateDTO with empty roles to existing User entity")
    void shouldMapUserUpdateDTOWithEmptyRolesToExistingUserEntity() {
        // Create existing user
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldpassword");

        Set<Role> oldRoles = new HashSet<>();
        oldRoles.add(new Role(1L, "ROLE_USER"));
        existingUser.setRoles(oldRoles);

        // Create update DTO with empty roles
        UserUpdateDTO updateDTO = new UserUpdateDTO(
                "New Name",
                "new@example.com",
                "newpassword",
                List.of()
        );

        // Map to existing entity
        userMapper.toEntity(updateDTO, existingUser);

        // Verify mapping
        assertEquals("New Name", existingUser.getName());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("newpassword", existingUser.getPassword());
        assertNotNull(existingUser.getRoles());
        assertTrue(existingUser.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Should test rolesToStringList mapping method")
    void shouldTestRolesToStringListMappingMethod() {
        // Create test roles
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, "ROLE_USER"));
        roles.add(new Role(2L, "ROLE_ADMIN"));
        roles.add(new Role(3L, "ROLE_MANAGER"));

        // Test the mapping method directly
        List<String> roleStrings = userMapper.rolesToStringList(roles);

        // Verify mapping
        assertNotNull(roleStrings);
        assertEquals(3, roleStrings.size());
        assertTrue(roleStrings.contains("ROLE_USER"));
        assertTrue(roleStrings.contains("ROLE_ADMIN"));
        assertTrue(roleStrings.contains("ROLE_MANAGER"));
    }

    @Test
    @DisplayName("Should test rolesToStringList with null input")
    void shouldTestRolesToStringListWithNullInput() {
        // Test the mapping method with null input
        List<String> roleStrings = userMapper.rolesToStringList(null);

        // Verify mapping
        assertNull(roleStrings);
    }

    @Test
    @DisplayName("Should test rolesToHashSet mapping method")
    void shouldTestRolesToHashSetMappingMethod() {
        // Create test role strings
        List<String> roleStrings = List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER");

        // Test the mapping method directly
        Set<Role> roles = userMapper.rolesToHashSet(roleStrings);

        // Verify mapping
        assertNotNull(roles);
        assertEquals(3, roles.size());

        // Check that all roles were created correctly
        boolean hasUserRole = roles.stream()
                .anyMatch(role -> role.getName().equals("ROLE_USER"));
        boolean hasAdminRole = roles.stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        boolean hasManagerRole = roles.stream()
                .anyMatch(role -> role.getName().equals("ROLE_MANAGER"));

        assertTrue(hasUserRole);
        assertTrue(hasAdminRole);
        assertTrue(hasManagerRole);
    }

    @Test
    @DisplayName("Should test rolesToHashSet with empty input")
    void shouldTestRolesToHashSetWithEmptyInput() {
        // Test the mapping method with empty input
        Set<Role> roles = userMapper.rolesToHashSet(List.of());

        // Verify mapping
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }
}