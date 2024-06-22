package com.sinaev.services;

import com.sinaev.models.Booking;
import com.sinaev.models.Room;
import com.sinaev.models.User;

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
    private final List<Booking> bookings;
    private final List<Room> rooms;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");

    /**
     * Constructs a BookingService with the specified list of bookings and rooms.
     *
     * @param bookings the list of bookings to manage
     * @param rooms    the list of rooms to manage
     */
    public BookingService(List<Booking> bookings, List<Room> rooms) {
        this.bookings = bookings;
        this.rooms = rooms;
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
        if (isResourceAvailable(room, start, end)) {
            Booking booking = new Booking(user, room, start, end);
            bookings.add(booking);
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

            boolean isAvailable = bookings.stream()
                    .filter(booking -> booking.getRoom().equals(room))
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
     * @param newRoom           the new room to be booked
     */
    public void updateBooking(User user, String originalStartTime, String newStartTime, String newEndTime, Room newRoom) {
        LocalDateTime originalStart = LocalDateTime.parse(originalStartTime, dateFormatter);
        LocalDateTime newStart = LocalDateTime.parse(newStartTime, dateFormatter);
        LocalDateTime newEnd = LocalDateTime.parse(newEndTime, dateFormatter);

        Optional<Booking> optionalBooking = bookings.stream()
                .filter(booking -> booking.getUser().equals(user) && booking.getStartTime().equals(originalStart))
                .findFirst();

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();

            if (!booking.getUser().equals(user) && !user.isAdmin()) {
                System.out.println("You are not authorized to update this booking.");
                return;
            }

            if (isResourceAvailable(newRoom, newStart, newEnd)) {
                booking.setStartTime(newStart);
                booking.setEndTime(newEnd);
                booking.setRoom(newRoom);
                System.out.println("Booking has been updated.");
            } else {
                System.out.println("The new time or resource is not available. Try another time or resource.");
            }
        } else {
            System.out.println("Booking not found.");
        }
    }

    /**
     * Deletes a booking if it exists and if the user is an admin.
     *
     * @param user      the user attempting to delete the booking
     * @param room      the room of the booking to delete
     * @param startTime the start time of the booking to delete
     */
    public void deleteBooking(User user, Room room, LocalDateTime startTime) {
        Optional<Booking> optionalBooking = bookings.stream()
                .filter(booking -> booking.getRoom().getName().equals(room.getName()) &&
                        booking.getStartTime().equals(startTime))
                .findFirst();

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            if (user.isAdmin() || booking.getUser().equals(user)) {
                bookings.remove(booking);
                System.out.println("Booking has been deleted.");
            } else {
                System.out.println("You do not have the right to delete this booking.");
            }
        } else {
            System.out.println("No booking found at the specified time for the specified room.");
        }
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
            return bookings.stream()
                    .filter(booking -> booking.getStartTime().toLocalDate().equals(date.toLocalDate()) &&
                            booking.getStartTime().getHour() == date.getHour())
                    .collect(Collectors.toList());
        } else if (user != null) {
            return bookings.stream()
                    .filter(booking -> booking.getUser().equals(user))
                    .collect(Collectors.toList());
        } else if (room != null) {
            return bookings.stream()
                    .filter(booking -> booking.getRoom().equals(room))
                    .collect(Collectors.toList());
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
    private boolean isResourceAvailable(Room room, LocalDateTime startTime, LocalDateTime endTime) {
        return bookings.stream()
                .filter(booking -> booking.getRoom().getName().equals(room.getName()))
                .allMatch(booking -> booking.getEndTime().isBefore(startTime) || booking.getStartTime().isAfter(endTime)) ||
                bookings.stream().noneMatch(booking -> booking.getRoom().getName().equals(room.getName()));
    }
}
