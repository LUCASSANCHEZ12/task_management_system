package com.task.manager.demo.dto.epic;

import java.time.LocalDateTime;
import java.util.UUID;

public record EpicUpdateDTO(
        String title,
        String description,
        boolean completed,
        boolean deleted,
        UUID deletedBy,
        int story_points
) {
}
