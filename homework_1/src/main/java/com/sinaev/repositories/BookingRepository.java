package com.sinaev.repositories;

import com.sinaev.models.Booking;
import com.sinaev.models.Room;
import com.sinaev.models.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for managing bookings.
 */
public class BookingRepository {
    private final List<Booking> bookings = new ArrayList<>();

    /**
     * Finds a booking by the room and start time.
     *
     * @param startTime the start time of the booking
     * @param room      the room of the booking
     * @return an Optional containing the found booking, or an empty Optional if no booking was found
     */
    public Optional<Booking> findByRoomAndTime(LocalDateTime startTime, Room room) {
        return bookings.stream()
                .filter(booking -> booking.getStartTime().equals(startTime) && booking.getRoom().equals(room))
                .findFirst();
    }

    /**
     * Saves a new booking to the repository.
     *
     * @param booking the booking to save
     */
    public void save(Booking booking) {
        bookings.add(booking);
    }

    /**
     * Deletes a booking from the repository.
     *
     * @param booking the booking to delete
     */
    public void delete(Booking booking) {
        bookings.remove(booking);
    }

    /**
     * Finds all bookings for a specific room.
     *
     * @param room the room to find bookings for
     * @return a list of bookings for the specified room
     */
    public List<Booking> findByRoom(Room room) {
        return bookings.stream()
                .filter(booking -> booking.getRoom().equals(room))
                .collect(Collectors.toList());
    }

    /**
     * Finds all bookings for a specific user.
     *
     * @param user the user to find bookings for
     * @return a list of bookings for the specified user
     */
    public List<Booking> findByUser(User user) {
        return bookings.stream()
                .filter(booking -> booking.getUser().equals(user))
                .collect(Collectors.toList());
    }

    /**
     * Finds all bookings for a specific date and hour.
     *
     * @param date the date and hour to find bookings for
     * @return a list of bookings for the specified date and hour
     */
    public List<Booking> findByDate(LocalDateTime date) {
        return bookings.stream()
                .filter(booking -> booking.getStartTime().toLocalDate().equals(date.toLocalDate()) &&
                        booking.getStartTime().getHour() == date.getHour())
                .collect(Collectors.toList());
    }
}