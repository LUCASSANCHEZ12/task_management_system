package com.task.manager.demo.user.dto;

public record UserDto(
        Long id,
        String name,
        String email,
        String role
) {
}