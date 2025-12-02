package com.task.manager.demo.dto.user;

import java.util.UUID;

public record UserDto(
        UUID id,
        String name,
        String email,
        String role
) {
}


