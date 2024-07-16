package com.sinaev.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Data Transfer Object (DTO) for Room.
 * <p>
 * This record represents the data required to create or update a room. It includes
 * validation annotations to ensure that the necessary fields are provided and meet the specified constraints.
 * </p>
 *
 * @param name  the name of the room
 * @param type  the type of the room
 */
@Builder
public record RoomDTO(
        @NotNull(message = "Room's name can't be null")
        @Size(min = 1, max = 10, message = "Room's name must be between 1 and 10 characters")
        String name,
        @NotNull(message = "Room's type can't be null")
        String type
) {
}
