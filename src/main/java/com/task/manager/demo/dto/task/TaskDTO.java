package com.task.manager.demo.dto.task;

import com.task.manager.demo.entity.Type_Enum;
import com.task.manager.demo.validation.annotation.ValidTaskType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDTO(
        UUID id,
        String title,
        String description,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime finishedAt,
        int story_points,
        Type_Enum type,
        UUID epic_id,
        UUID parent_id,
        UUID user_id,
        UUID project_id
) {
}


