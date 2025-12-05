package com.task.manager.demo.dto.epic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import java.util.UUID;

public record EpicUpdateDTO(
        @NotBlank(message = "Title cannot be empty")
        @Size(min = 1, max = 256, message = "Title must be between 1 and 256 characters")
        String title,
        @NotBlank(message = "Description cannot be empty")
        @Size(min = 1, max = 512, message = "Description must be between 1 and 512 characters")
        String description,
        @Min(value = 0, message = "Story points cannot be negative")
        int story_points
) {
}
