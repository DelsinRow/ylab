package com.sinaev.services;

import com.sinaev.models.Booking;
import com.sinaev.models.Room;
import com.sinaev.models.User;
import com.sinaev.repositories.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Booking service class that manages bookings for resources.
 */
public class BookingService {
    private final BookingRepository bookingRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");

    /**
     * Constructs a BookingService with the specified repository.
     *
     * @param bookingRepository the repository for managing bookings
     */
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Creates a booking for a room at a specific time.
     *
     * @param user      the user making the booking
     * @param room      the room to be booked
     * @param startTime the start time of the booking in the format "yyyy-MM-dd'T'HH"
     * @param endTime   the end time of the booking in the format "yyyy-MM-dd'T'HH"
     */
    public void createBooking(User user, Room room, String startTime, String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);
        LocalDateTime end = LocalDateTime.parse(endTime, dateFormatter);
        if (isRoomAvailable(room, start, end)) {
            Booking booking = new Booking(user, room, start, end);
            bookingRepository.save(booking);
            System.out.println(booking.toString() + " successfully created.");
        } else {
            System.out.println("This time is not available for bookings. Try another time.");
        }
    }

    /**
     * Gets the list of available hours for a specified room on a specific date.
     *
     * @param date the date to check for available hours
     * @param room the room to check for availability
     * @return a list of available hours on the specified date for the specified room
     */
    public List<LocalTime> getAvailableHours(LocalDate date, Room room) {
        List<LocalTime> availableHours = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime startTime = date.atTime(hour, 0);
            LocalDateTime endTime = startTime.plusHours(1);

            boolean isAvailable = bookingRepository.findByRoom(room).stream()
                    .noneMatch(booking -> booking.getStartTime().isBefore(endTime) && booking.getEndTime().isAfter(startTime));

            if (isAvailable) {
                availableHours.add(startTime.toLocalTime());
            }
        }

        return availableHours;
    }

    /**
     * Updates an existing booking if it exists and if the user is the one who created the booking.
     *
     * @param user              the user making the booking
     * @param originalStartTime the original start time of the booking in the format "yyyy-MM-dd'T'HH"
     * @param newStartTime      the new start time of the booking in the format "yyyy-MM-dd'T'HH"
     * @param newEndTime        the new end time of the booking in the format "yyyy-MM-dd'T'HH"
     * @param room              the booked room
     */
    public void updateBooking(User user, Room room, String originalStartTime, Room newRoom, String newStartTime, String newEndTime) {
        LocalDateTime originalStart = LocalDateTime.parse(originalStartTime, dateFormatter);
        LocalDateTime newStart = LocalDateTime.parse(newStartTime, dateFormatter);
        LocalDateTime newEnd = LocalDateTime.parse(newEndTime, dateFormatter);

        Optional<Booking> optionalBooking = bookingRepository.findByRoomAndTime(originalStart, room);

        if (!optionalBooking.isPresent()) {
            System.out.println("Booking not found.");
            return;
        }

        Booking booking = optionalBooking.get();

        if (!booking.getUser().equals(user) && !user.isAdmin()) {
            System.out.println("You are not authorized to update this booking.");
            return;
        }

        if (!isRoomAvailable(newRoom, newStart, newEnd)) {
            System.out.println("The new time or room is not available. Try another time or resource.");
            return;
        }

        booking.setStartTime(newStart);
        booking.setEndTime(newEnd);
        booking.setRoom(newRoom);
        System.out.println("Booking has been updated.");
    }

    /**
     * Deletes a booking if it exists and if the user is an admin or the creator of the booking.
     *
     * @param user      the user attempting to delete the booking
     * @param room      the room of the booking to delete
     * @param startTime the start time of the booking to delete
     */
    public void deleteBooking(User user, Room room, LocalDateTime startTime) {
        Optional<Booking> optionalBooking = bookingRepository.findByRoomAndTime(startTime, room);

        if (!optionalBooking.isPresent()) {
            System.out.println("No booking found at the specified time for the specified room.");
            return;
        }

        Booking booking = optionalBooking.get();
        if (!user.isAdmin() && !booking.getUser().equals(user)) {
            System.out.println("You do not have the right to delete this booking.");
            return;
        }

        bookingRepository.delete(booking);
        System.out.println("Booking has been deleted.");
    }

    /**
     * Filters bookings by a specified date, user, or room.
     *
     * @param date the date to filter bookings by
     * @param user the user to filter bookings by
     * @param room the room to filter bookings by
     * @return a list of bookings filtered by the specified argument
     */
    public List<Booking> filterBookings(LocalDateTime date, User user, Room room) {
        if (date != null) {
            return bookingRepository.findByDate(date);
        } else if (user != null) {
            return bookingRepository.findByUser(user);
        } else if (room != null) {
            return bookingRepository.findByRoom(room);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Checks if a room is available in the specified time interval.
     *
     * @param room      the room to check
     * @param startTime the start time of the interval
     * @param endTime   the end time of the interval
     * @return true if the room is available in the specified time interval, false otherwise
     */
    private boolean isRoomAvailable(Room room, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findByRoom(room).stream()
                .allMatch(booking -> booking.getEndTime().isBefore(startTime) || booking.getStartTime().isAfter(endTime));
    }
}