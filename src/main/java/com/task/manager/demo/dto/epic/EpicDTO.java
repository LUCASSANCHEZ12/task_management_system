package com.task.manager.demo.dto.epic;

import com.task.manager.demo.entity.Type_Enum;

import java.time.LocalDateTime;
import java.util.UUID;

public record EpicDTO(
        UUID id,
        String title,
        String description,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime finishedAt,
        LocalDateTime deletedAt,
        UUID deletedBy,
        int story_points
) {
}
