package com.task.manager.demo.dto.auth;

public record LoginResponse(
        String token,
        String type,
        long expiresIn
) {
    public LoginResponse(String token, long expiresIn) {
        this(token, "Bearer", expiresIn);
    }
}


