package com.task.manager.demo.dto.task;

import com.task.manager.demo.entity.Type_Enum;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TaskUpdateDTO(
        String title,
        String description,
        int story_points,
        Type_Enum type,
        boolean completed
) {
}


