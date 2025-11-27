package com.task.manager.demo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String name,

        @NotBlank
        String password
) {
}


