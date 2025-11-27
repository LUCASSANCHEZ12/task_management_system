package com.task.manager.demo.dto.task;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalTime;

public record TaskUpdateDTO(
        @NotBlank
        String description,
        boolean completed,
        LocalTime finishedTime
) {
}


