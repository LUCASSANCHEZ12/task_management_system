package com.task.manager.demo.service.auth;

import com.task.manager.demo.dto.auth.LoginRequest;
import com.task.manager.demo.dto.auth.LoginResponse;
import com.task.manager.demo.dto.auth.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse register(RegisterRequest request);
}


