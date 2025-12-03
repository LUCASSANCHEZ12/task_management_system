package com.task.manager.demo.dto.user;

import com.task.manager.demo.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String name,

        @Email(message = "El correo electrónico debe ser válido")
        @NotBlank(message = "El correo electrónico no puede estar vacío")
        String email,

        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
        String password,

        @NotBlank(message = "El rol no puede estar vacío")
        Role role
) {
}


