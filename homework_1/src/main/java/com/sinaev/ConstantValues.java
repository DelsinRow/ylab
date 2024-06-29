package com.sinaev;

/**
 * This class contains constant values used throughout the application.
 */
public class ConstantValues {
    /**
     * Information about available commands in the application.
     */
    public static final String INFO =
            """
            ---User---
            register -> registers the specified user with unique username
            login -> logs in the specified user if they are registered
            ---Room---
            room -c -> creates a new room
            room -r -> returns the list of rooms
            room -u -> updates an existing room
            room -d -> deletes a room if the user is an admin and the room exists
            ---Booking---
            booking -c -> creates a booking for a room at a specific time
            slots -r-> get free slots for the booking for a specific room
            booking -u -> updates an existing booking
            booking -d -> deletes a booking if it exists
            booking -f -> filters bookings by a specified date, user, or room
            ---Other---
            info -> view all commands
            exit -> close the program
            """;

    /**
     * ANSI escape code for the color green.
     */
    public static final String GREEN = "\u001B[32m";

    /**
     * ANSI escape code to reset the color.
     */
    public static final String RESET = "\u001B[0m";
}