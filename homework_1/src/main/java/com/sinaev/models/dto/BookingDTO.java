package com.sinaev.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for Booking.
 * <p>
 * This record represents the data required to create or update a booking. It includes
 * validation annotations to ensure that the necessary fields are provided and meet the specified constraints.
 * </p>
 *
 * @param username   the username associated with the booking
 * @param roomName   the name of the room being booked
 * @param startTime  the start time of the booking
 * @param endTime    the end time of the booking
 */
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
