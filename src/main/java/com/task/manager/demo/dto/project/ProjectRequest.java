package com.task.manager.demo.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "Title must not be blank")
        @Size(min = 1, max = 256, message = "Title must be between 1 and 256 characters")
        String title,
        @NotBlank(message = "Description must not be blank")
        @Size(min = 1, max = 512, message = "Description must be between 1 and 256 characters")
        String description
) {
}
