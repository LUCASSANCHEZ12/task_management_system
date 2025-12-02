package com.task.manager.demo.service.user;

import com.task.manager.demo.dto.user.UserDto;
import com.task.manager.demo.dto.user.UserUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto findById(UUID id);
    List<UserDto> getAll();
    void deleteById(UUID id);
    UserDto update(UUID id, UserUpdateDTO request);
}


