package com.task.manager.demo.dto.project;

import java.util.UUID;

public record ProjectUpdateDTO(
        String title,
        String description,
        boolean deleted,
        UUID deletedBy
) {
}
