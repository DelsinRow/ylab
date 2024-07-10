package com.sinaev.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RoomDTO(
        @NotNull(message = "Room's name can't be null")
        @Size(min = 1, max = 10, message = "Room's name must be between 1 and 10 characters")
        String name,
        @NotNull(message = "Room's type can't be null")
        String type
) {
}
