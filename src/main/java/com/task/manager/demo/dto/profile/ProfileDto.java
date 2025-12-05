package com.task.manager.demo.dto.profile;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileDto(
        UUID profileId,
        String country,
        String address,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}