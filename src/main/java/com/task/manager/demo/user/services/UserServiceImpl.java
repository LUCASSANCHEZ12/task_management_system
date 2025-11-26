package com.task.manager.demo.user.services;

import com.task.manager.demo.user.controller.advice.exceptions.UserException;
import com.task.manager.demo.user.controller.advice.exceptions.UserNotFoundException;
import com.task.manager.demo.user.dto.UserDto;
import com.task.manager.demo.user.dto.UserRequest;
import com.task.manager.demo.user.dto.UserUpdateDTO;
import com.task.manager.demo.user.entity.Role;
import com.task.manager.demo.user.entity.User;
import com.task.manager.demo.user.repository.UserRepository;
import com.task.manager.demo.user.repository.mapper.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserDto register(UserRequest request) {

        if (repository.findByEmail(request.email()).isPresent()) {
            throw new UserException("Email is registered to another account.");
        }

        if (request.password().length() < 6) {
            throw new UserException("Password should be at least 6 characters");
        }
        if (request.name().isEmpty()) {
            throw new UserException("Name is required");
        }
        if (request.email().isEmpty()) {
            throw new UserException("Email is required");
        }

        var user = User.builder()
                .name(request.name())
                .password(request.password())
                .email(request.email())
                .role(Role.USER) // by default all users are USER role
                .build();
        return mapper.toDto(repository.save(user));
    }

    @Override
    public UserDto findById(Long id) {
        if(repository.findById(id).isEmpty()) {
            throw new UserException("User not found");
        }
        return mapper.toDto(repository.findById(id).get());
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = repository.findAll();
        return users.stream().map(mapper::toDto).toList();
    }

    @Override
    public void deleteById(Long id) {
        if(repository.findById(id).isEmpty()) {
            throw new UserException("User not found");
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserUpdateDTO request) {
        User u = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));

        mapper.toEntity(request, u);
        return mapper.toDto(repository.save(u));
    }

}
