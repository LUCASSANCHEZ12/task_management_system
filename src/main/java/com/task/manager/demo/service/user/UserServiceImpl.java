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

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserDto findById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return mapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = repository.findAll();
        return users.stream().map(mapper::toDto).toList();
    }

    @Override
    public void deleteById(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserUpdateDTO request) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id: " + id + " no encontrado"));

        mapper.toEntity(request, user);
        return mapper.toDto(repository.save(user));
    }
}


