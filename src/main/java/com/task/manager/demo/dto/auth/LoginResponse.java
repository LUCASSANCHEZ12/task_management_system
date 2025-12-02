package com.task.manager.demo.dto.auth;

import java.util.List;

public record LoginResponse(
        String token,
        String type,
        long expiresIn,
        List<String> roles
) {
    public LoginResponse(String token, long expiresIn, List<String> roles) {
        this(token, "Bearer", expiresIn, roles);
    }
}


