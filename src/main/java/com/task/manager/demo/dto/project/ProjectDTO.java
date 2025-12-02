package com.task.manager.demo.dto.project;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectDTO(
        UUID id,
        String title,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        UUID deletedBy
) {
}
