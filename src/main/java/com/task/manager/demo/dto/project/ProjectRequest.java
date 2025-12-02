package com.task.manager.demo.dto.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
        @NotBlank
        String title,
        @NotBlank
        String description
) {
}
