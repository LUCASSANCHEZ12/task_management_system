package com.task.manager.demo.dto.task;

import com.task.manager.demo.entity.Type_Enum;
import com.task.manager.demo.validation.annotation.ValidTaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TaskRequest(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotNull
        int story_points,
        @ValidTaskType
        String type,
        UUID project_id
) {
}


