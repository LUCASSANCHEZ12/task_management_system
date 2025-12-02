package com.task.manager.demo.dto.epic;

import com.task.manager.demo.entity.Type_Enum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EpicRequest(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotNull
        int story_points,
        UUID project_id

) {
}
