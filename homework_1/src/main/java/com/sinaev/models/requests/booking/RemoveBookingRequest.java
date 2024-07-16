package com.sinaev.models.requests.booking;

import java.time.LocalDateTime;

/**
 * Request object for removing a booking.
 * <p>
 * This record encapsulates the parameters required to remove a booking, such as the room name and start time of the booking.
 * </p>
 *
 * @param roomName  the name of the room for which the booking is to be removed
 * @param startTime the start time of the booking to be removed
 */
public record RemoveBookingRequest(
        String roomName,
        LocalDateTime startTime) {
}
