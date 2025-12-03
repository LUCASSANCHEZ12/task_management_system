package com.task.manager.demo.dto.task;

import com.task.manager.demo.entity.Type_Enum;
import com.task.manager.demo.validation.annotation.ValidTaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record TaskRequest(
        @NotBlank(message = "El título no puede estar vacío")
        @Size(min = 1, max = 256, message = "El título debe tener entre 1 y 256 caracteres")
        String title,
        @NotBlank(message = "La descripción no puede estar vacía")
        @Size(min = 1, max = 512, message = "La descripción debe tener entre 1 y 512 caracteres")
        String description,
        @NotNull(message = "Los puntos de historia no pueden ser nulos")
        @Min(value = 0, message = "Los puntos de historia no pueden ser negativos")
        int story_points,
        @ValidTaskType
        String type,
        @NotNull(message = "El ID del proyecto es obligatorio")
        UUID project_id
) {
}


