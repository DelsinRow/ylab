package com.sinaev.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a booking in the system.
 */
@Getter
@Setter
@AllArgsConstructor
public class Booking {
    /**
     * The user who made the booking.
     */
    private User user;

    /**
     * The room that is booked.
     */
    private Room room;

    /**
     * The start time of the booking.
     */
    private LocalDateTime startTime;

    /**
     * The end time of the booking.
     */
    private LocalDateTime endTime;

    /**
     * Returns a string representation of the booking.
     *
     * @return a string representation of the booking.
     */
    @Override
    public String toString() {
        return "Booking: [" +
                "user:" + user.getUsername() +
                ", room:" + room.getName() +
                ", startTime:" + startTime +
                ", endTime:" + endTime +
                ']';
    }
}
