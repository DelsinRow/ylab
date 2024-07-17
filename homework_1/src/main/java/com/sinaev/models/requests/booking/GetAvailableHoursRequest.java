package com.sinaev.models.requests.booking;

import java.time.LocalDate;

/**
 * Request object for getting available booking hours.
 * <p>
 * This record encapsulates the parameters used to request available booking hours for a specific date and room.
 * </p>
 *
 * @param date     the date for which available hours are requested
 * @param roomName the name of the room for which available hours are requested
 */
public record GetAvailableHoursRequest(
        LocalDate date,
        String roomName) {
}
