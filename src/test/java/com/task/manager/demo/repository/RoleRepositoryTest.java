package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@DisplayName("RoleRepository - Integration Tests")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private Role testRole;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        testRole = new Role();
        testRole.setName("ROLE_USER");
    }

    @Test
    @DisplayName("Should save a role successfully")
    void shouldSaveRoleSuccessfully() {
        Role savedRole = roleRepository.save(testRole);

        assertNotNull(savedRole);
        assertNotNull(savedRole.getId());
        assertEquals("ROLE_USER", savedRole.getName());
    }

    @Test
    @DisplayName("Should find role by ID")
    void shouldFindRoleById() {
        Role savedRole = roleRepository.save(testRole);

        Optional<Role> foundRole = roleRepository.findById(savedRole.getId());

        assertTrue(foundRole.isPresent());
        assertEquals("ROLE_USER", foundRole.get().getName());
    }

    @Test
    @DisplayName("Should return empty Optional when role not found")
    void shouldReturnEmptyOptionalWhenRoleNotFound() {
        Long nonExistentId = 999L;

        Optional<Role> foundRole = roleRepository.findById(nonExistentId);

        assertFalse(foundRole.isPresent());
    }

    @Test
    @DisplayName("Should find role by name")
    void shouldFindRoleByName() {
        roleRepository.save(testRole);

        Optional<Role> foundRole = roleRepository.findByName("ROLE_USER");

        assertTrue(foundRole.isPresent());
        assertEquals("ROLE_USER", foundRole.get().getName());
    }

    @Test
    @DisplayName("Should return empty Optional when role name not found")
    void shouldReturnEmptyOptionalWhenRoleNameNotFound() {
        Optional<Role> foundRole = roleRepository.findByName("NON_EXISTENT_ROLE");

        assertFalse(foundRole.isPresent());
    }

    @Test
    @DisplayName("Should find role by name case-sensitive")
    void shouldFindRoleByNameCaseSensitive() {
        roleRepository.save(testRole);

        Optional<Role> foundRole = roleRepository.findByName("role_user");

        assertFalse(foundRole.isPresent());
    }

    @Test
    @DisplayName("Should delete role by ID")
    void shouldDeleteRoleById() {
        Role savedRole = roleRepository.save(testRole);
        Long roleId = savedRole.getId();

        roleRepository.deleteById(roleId);
        Optional<Role> deletedRole = roleRepository.findById(roleId);

        assertFalse(deletedRole.isPresent());
    }

    @Test
    @DisplayName("Should update role successfully")
    void shouldUpdateRoleSuccessfully() {
        Role savedRole = roleRepository.save(testRole);

        savedRole.setName("ROLE_ADMIN");

        Role updatedRole = roleRepository.save(savedRole);

        assertEquals("ROLE_ADMIN", updatedRole.getName());
    }

    @Test
    @DisplayName("Should get all roles")
    void shouldGetAllRoles() {
        roleRepository.save(testRole);

        Role secondRole = new Role();
        secondRole.setName("ROLE_ADMIN");
        roleRepository.save(secondRole);

        List<Role> allRoles = roleRepository.findAll();

        assertEquals(2, allRoles.size());
    }

    @Test
    @DisplayName("Should return empty list when no roles exist")
    void shouldReturnEmptyListWhenNoRolesExist() {
        List<Role> allRoles = roleRepository.findAll();

        assertNotNull(allRoles);
        assertEquals(0, allRoles.size());
    }

    @Test
    @DisplayName("Should save multiple roles and retrieve them")
    void shouldSaveMultipleRolesAndRetrieveThem() {
        for (int i = 1; i <= 5; i++) {
            Role role = new Role();
            role.setName("ROLE_TEST_" + i);
            roleRepository.save(role);
        }

        List<Role> allRoles = roleRepository.findAll();

        assertEquals(5, allRoles.size());
    }

    @Test
    @DisplayName("Should handle role with special characters in name")
    void shouldHandleRoleWithSpecialCharactersInName() {
        Role specialRole = new Role();
        specialRole.setName("ROLE_TEST@#$");

        Role savedRole = roleRepository.save(specialRole);

        Optional<Role> foundRole = roleRepository.findById(savedRole.getId());

        assertTrue(foundRole.isPresent());
        assertEquals("ROLE_TEST@#$", foundRole.get().getName());
    }

    @Test
    @DisplayName("Should handle multiple roles with different names")
    void shouldHandleMultipleRolesWithDifferentNames() {
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        roleRepository.save(userRole);

        Role moderatorRole = new Role();
        moderatorRole.setName("ROLE_MODERATOR");
        roleRepository.save(moderatorRole);

        List<Role> allRoles = roleRepository.findAll();

        assertEquals(3, allRoles.size());
        assertTrue(allRoles.stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN")));
        assertTrue(allRoles.stream().anyMatch(r -> r.getName().equals("ROLE_USER")));
        assertTrue(allRoles.stream().anyMatch(r -> r.getName().equals("ROLE_MODERATOR")));
    }

    @Test
    @DisplayName("Should find recently updated role")
    void shouldFindRecentlyUpdatedRole() {
        Role savedRole = roleRepository.save(testRole);
        Long roleId = savedRole.getId();

        savedRole.setName("ROLE_UPDATED");
        roleRepository.save(savedRole);

        Optional<Role> foundRole = roleRepository.findById(roleId);

        assertTrue(foundRole.isPresent());
        assertEquals("ROLE_UPDATED", foundRole.get().getName());
    }
}