package com.task.manager.demo.service.user;

import com.task.manager.demo.dto.user.UserDto;
import com.task.manager.demo.dto.user.UserUpdateDTO;
import com.task.manager.demo.entity.User;
import com.task.manager.demo.exception.ResourceNotFoundException;
import com.task.manager.demo.mapper.UserMapper;
import com.task.manager.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link UserService} interface that manages CRUD operations
 * and retrieval of users within the system.
 * <p>
 * This service interacts with the {@link UserRepository} for persistence and
 * uses {@link UserMapper} for converting between {@link User} entities and
 * {@link UserDto} objects.
 * </p>
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    /**
     * Constructs a new {@code UserServiceImpl} with the required dependencies.
     *
     * @param repository the repository for user persistence
     * @param mapper     the mapper for converting User entities to DTOs
     */
    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the UUID of the user to retrieve
     * @return a {@link UserDto} representing the user
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    public UserDto findById(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapper.toDto(user);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a list of {@link UserDto} representing all users
     */
    @Override
    public List<UserDto> getAll() {
        List<User> users = repository.findAll();
        return users.stream().map(mapper::toDto).toList();
    }

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the UUID of the user to delete
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    public void deleteById(UUID id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        repository.deleteById(id);
    }

    /**
     * Updates an existing user's information.
     * <p>
     * Applies the fields from {@link UserUpdateDTO} to the entity and persists
     * the changes.
     * </p>
     *
     * @param id      the UUID of the user to update
     * @param request the update data encapsulated in {@link UserUpdateDTO}
     * @return an updated {@link UserDto} representing the user
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    @Transactional
    public UserDto update(UUID id, UserUpdateDTO request) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        mapper.toEntity(request, user);
        return mapper.toDto(repository.save(user));
    }
}


