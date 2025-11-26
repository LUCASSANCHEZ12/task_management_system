package com.task.manager.demo.task.dto;

public record TaskRequest(
        String description,
        Long user_id
) {
}
