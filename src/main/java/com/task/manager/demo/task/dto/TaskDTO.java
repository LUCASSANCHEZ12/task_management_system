package com.task.manager.demo.task.dto;

import java.time.LocalTime;

public record TaskDTO(
        Long id,
        String description,
        boolean completed,
        LocalTime createdTime,
        LocalTime finishedTime,
        Long user_id
) {
}
