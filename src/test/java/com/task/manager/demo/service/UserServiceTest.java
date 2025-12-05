package com.task.manager.demo.service;

import com.task.manager.demo.dto.user.UserDto;
import com.task.manager.demo.dto.user.UserUpdateDTO;
import com.task.manager.demo.entity.Role;
import com.task.manager.demo.entity.User;
import com.task.manager.demo.exception.BadRequestException;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.UserMapper;
import com.task.manager.demo.repository.UserRepository;
import com.task.manager.demo.service.user.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UUID id;
    private UserDto oldUser;
    private User user;

    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl service;

    @BeforeEach
    public void init(){
        id=UUID.randomUUID();
        oldUser = new UserDto(
                id,
                "Test user",
                "test@email.com",
                List.of("USER")
        );
        user = User.builder()
                .id(id)
                .name("Test user")
                .email("test@email.com")
                .password("Test password")
                .build();
    }

    @AfterEach
    public void tearDown(){
        oldUser=null;
        id=null;
    }

    @Test
    @DisplayName("Should find user by id")
    void shouldFindUserById() {
        UserDto expectedDto = new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                List.of()
        );
        when(repository.findById(id)).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(expectedDto);

        UserDto actualDto = service.findById(id);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("FindById should return error with false user id")
    void shouldNotFindFalseUserById() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.findById(id));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Return a list of all users")
    void shouldFindAllUsers() {
        UserDto expectedDto = new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                List.of()
        );
        List<UserDto> expectedList = List.of(expectedDto);
        when(repository.findAll()).thenReturn(List.of(user));
        when(mapper.toDto(user)).thenReturn(expectedDto);

        List<UserDto> actualList = service.getAll();
        assertNotNull(actualList);
        assertEquals(expectedList, actualList);
    }

    @Test
    @DisplayName("Return an empty list of all users")
    void shouldReturnEmptyListOfAllUsers() {
        List<UserDto> expectedList = List.of();
        List<UserDto> actualList = service.getAll();
        assertNotNull(actualList);
        assertEquals(expectedList, actualList);
    }

    @Test
    @DisplayName("Should successfully delete a user when it exists")
    void shouldDeleteUserSuccessfully() {
        when(repository.findById(id)).thenReturn(Optional.of(new User()));
        assertDoesNotThrow(() -> service.deleteById(id));
        verify(repository).findById(id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Should fail to delete a user when it doesn't exists")
    void shouldNotDeleteUserSuccessfully() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.deleteById(id));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        UUID userId = UUID.randomUUID();
        UserUpdateDTO request = new UserUpdateDTO("New Name", "new@mail.com", "123456", List.of("USER"));

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@mail.com");

        UserDto expectedDto = new UserDto(userId, "New Name", "new@mail.com", List.of("USER"));

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        doAnswer(invocation -> {
            UserUpdateDTO dto = invocation.getArgument(0);
            User user = invocation.getArgument(1);
            user.setName(dto.name());
            user.setEmail(dto.email());
            return null;
        }).when(mapper).toEntity(request, existingUser);

        when(repository.save(existingUser)).thenReturn(existingUser);
        when(mapper.toDto(existingUser)).thenReturn(expectedDto);

        UserDto result = service.update(userId, request);

        assertNotNull(result);
        assertEquals(expectedDto.id(), result.id());
        assertEquals(expectedDto.name(), result.name());
        assertEquals(expectedDto.email(), result.email());

        verify(repository).findById(userId);
        verify(mapper).toEntity(request, existingUser);
        verify(repository).save(existingUser);
        verify(mapper).toDto(existingUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowWhenUserNotFound() {
        UserUpdateDTO request = new UserUpdateDTO("New Name", "new@mail.com", "123456", List.of("USER"));
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.update(id, request));

        assertEquals("User not found", exception.getMessage());
    }

}
