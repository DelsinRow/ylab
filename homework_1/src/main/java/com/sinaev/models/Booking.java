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
public class Booking {
    /**
     * The id of the booking.
     */
    private Long id;
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
     * Constructs a Booking with the specified user, room, start time, and end time.
     *
     * @param user      the user who made the booking
     * @param room      the room that is booked
     * @param startTime the start time of the booking
     * @param endTime   the end time of the booking
     */
    public Booking(User user, Room room, LocalDateTime startTime, LocalDateTime endTime) {
        this.user = user;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
    }

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
