package com.task.manager.demo.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "El email debe ser válido")
        @NotBlank(message = "El email es requerido")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        String password
) {
}


