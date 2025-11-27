package com.task.manager.demo.service.user;

import com.task.manager.demo.dto.user.UserDto;
import com.task.manager.demo.dto.user.UserUpdateDTO;

import java.util.List;

public interface UserService {
    UserDto findById(Long id);
    List<UserDto> getAll();
    void deleteById(Long id);
    UserDto update(Long id, UserUpdateDTO request);
}


