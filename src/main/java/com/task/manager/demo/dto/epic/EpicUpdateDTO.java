package com.task.manager.demo.dto.epic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import java.util.UUID;

public record EpicUpdateDTO(
        @NotBlank(message = "El título no puede estar vacío")
        @Size(min = 1, max = 256, message = "El título debe tener entre 1 y 256 caracteres")
        String title,
        @NotBlank(message = "La descripción no puede estar vacía")
        @Size(min = 1, max = 512, message = "La descripción debe tener entre 1 y 512 caracteres")
        String description,
        boolean completed,
        boolean deleted,
        UUID deletedBy,
        @Min(value = 0, message = "Los puntos de historia no pueden ser negativos")
        int story_points
) {
}
