package com.sinaev.models.requests.booking;

import java.time.LocalDate;

/**
 * Request object for filtering bookings.
 * <p>
 * This record encapsulates the parameters used to filter booking records, such as date, username, and room name.
 * </p>
 *
 * @param date     the date of the booking
 * @param username the username associated with the booking
 * @param roomName the name of the room being booked
 */
public record FilterBookingsRequest(
        LocalDate date,
        String username,
        String roomName) {
}
