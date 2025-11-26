package com.task.manager.demo.user.services;

import com.task.manager.demo.user.dto.UserDto;
import com.task.manager.demo.user.dto.UserRequest;
import com.task.manager.demo.user.dto.UserUpdateDTO;

import java.util.List;

public interface UserService {
    UserDto register(UserRequest request);
    UserDto findById(Long id);
    List<UserDto> getAll();
    void deleteById(Long id);
    UserDto update(Long id, UserUpdateDTO request);
}
