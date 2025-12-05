package com.task.manager.demo.dto.profile;

import jakarta.validation.constraints.Size;

public record ProfileUpdateDTO(
        @Size(max = 100, message = "Country must be less than 100 characters")
        String country,

        @Size(max = 255, message = "Address must be less than 255 characters")
        String address,

        @Size(max = 20, message = "Phone number must be less than 20 characters")
        String phoneNumber
) {
}