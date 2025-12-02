package com.task.manager.demo.dto.user;

import java.util.List;
import java.util.UUID;

public record UserDto(
        UUID id,
        String name,
        String email,
        List<String> roles
) {
}


