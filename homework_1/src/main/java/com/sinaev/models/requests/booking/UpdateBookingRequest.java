package com.sinaev.models.requests.booking;

import java.time.LocalDateTime;

/**
 * Request object for updating a booking.
 * <p>
 * This record encapsulates the parameters required to update a booking, including the original booking details
 * and the new booking details.
 * </p>
 *
 * @param originalRoomName  the original name of the room for the booking
 * @param originalStartTime the original start time of the booking
 * @param newRoomName       the new name of the room for the booking
 * @param newStartTime      the new start time of the booking
 * @param newEndTime        the new end time of the booking
 */
public record UpdateBookingRequest(
        String originalRoomName,
        LocalDateTime originalStartTime,
        String newRoomName,
        LocalDateTime newStartTime,
        LocalDateTime newEndTime) {
}
