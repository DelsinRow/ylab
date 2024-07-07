package com.sinaev.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record BookingDTO(
        String username,
        @NotNull(message = "Room cannot be null")
        @Size(min = 1, max = 10, message = "Room's name must be between 1 and 10 characters")
        String roomName,
        @NotNull(message = "Start time cannot be null")
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
