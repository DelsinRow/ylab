package com.sinaev;

public class ConstantValues {
    public static final String INFO =
            "---User---" +
            "\nregister -> registers the specified user with unique username" +
            "\nlogin -> logs in the specified user if they are registered" +
            "\n---Room---" +
            "\nroom -c -> creates a new room" +
            "\nroom -r -> returns the list of rooms" +
            "\nroom -u -> updates an existing room" +
            "\nroom -d -> deletes a room if the user is an admin and the room exists" +
            "\n---Booking---" +
            "\nbooking -c -> creates a booking for a room at a specific time" +
            "\nslots -r-> get free slots for the booking for a specific room" +
            "\nbooking -u -> updates an existing booking" +
            "\nbooking -d -> deletes a booking if it exists" +
            "\nbooking -f -> filters bookings by a specified date, user, or room" +
            "\n---Other---" +
            "\ninfo -> view all commands" +
            "\nexit -> close the program";

    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";

}
