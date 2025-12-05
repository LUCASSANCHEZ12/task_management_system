package com.task.manager.demo.dto.task;

import com.task.manager.demo.entity.Type_Enum;
import com.task.manager.demo.validation.annotation.ValidTaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record TaskRequest(
        @NotBlank(message = "Title cannot be empty")
        @Size(min = 1, max = 256, message = "Title must be between 1 and 256 characters")
        String title,
        @NotBlank(message = "Description cannot be empty")
        @Size(min = 1, max = 512, message = "Description must be between 1 and 512 characters")
        String description,
        @NotNull(message = "Story points cannot be null")
        @Min(value = 0, message = "Story points cannot be negative")
        int story_points,
        @ValidTaskType
        String type,
        @NotNull(message = "Project ID is required")
        UUID project_id
) {
}


