package com.task.manager.demo.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "El título no puede estar vacío")
        @Size(min = 1, max = 256, message = "El título debe tener entre 1 y 256 caracteres")
        String title,
        @NotBlank(message = "La descripción no puede estar vacía")
        @Size(min = 1, max = 512, message = "La descripción debe tener entre 1 y 512 caracteres")
        String description
) {
}
