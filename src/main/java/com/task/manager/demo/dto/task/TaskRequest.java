package com.task.manager.demo.dto.task;

public record TaskRequest(
        String description,
        Long user_id
) {
}


