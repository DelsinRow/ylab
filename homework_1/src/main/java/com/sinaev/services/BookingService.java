package com.sinaev.services;

import com.sinaev.annotations.Loggable;
import com.sinaev.mappers.BookingMapper;
import com.sinaev.mappers.UserMapper;
import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.Booking;
import com.sinaev.models.entities.Room;
import com.sinaev.models.entities.User;
import com.sinaev.repositories.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Booking service class that manages bookings for resources.
 */
@Loggable
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
     * @param userDTO    the DTO of user making the booking
     * @param bookingDTO the booking's DTO
     */
    public Optional<BookingDTO> createBooking(UserDTO userDTO, BookingDTO bookingDTO) {
        LocalDateTime start = bookingDTO.startTime();
        LocalDateTime end = bookingDTO.endTime();

        if (isRoomAvailable(bookingDTO.roomName(), start, end)) {
            Booking booking = BookingMapper.INSTANCE.toEntity(bookingDTO);
            User user = UserMapper.INSTANCE.toEntity(userDTO);
            booking.setUser(user);
            booking.setRoom(findRoomByName(bookingDTO.roomName()));
            bookingRepository.save(booking);
            return Optional.of(BookingMapper.INSTANCE.toDTO(booking));
        } else {
            return Optional.empty();
        }
    }


    /**
     * Gets the list of available hours for a specified room on a specific date.
     *
     * @param date     the date to check for available hours
     * @param roomName the room's name to check for availability
     * @return a list of available hours on the specified date for the specified room
     */
    public Optional<List<LocalTime>> getAvailableHours(LocalDate date, String roomName) {
        List<LocalTime> availableHours = new ArrayList<>();
        if (findRoomByName(roomName) == null) {
            return Optional.empty();
        }
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime startTime = date.atTime(hour, 0);
            LocalDateTime endTime = startTime.plusHours(1);

            boolean isAvailable = bookingRepository.findByRoomName(roomName).stream()
                    .noneMatch(booking -> booking.getStartTime().isBefore(endTime) && booking.getEndTime().isAfter(startTime));

            if (isAvailable) {
                availableHours.add(startTime.toLocalTime());
            }
        }

        return availableHours.isEmpty() ? Optional.empty() : Optional.of(availableHours);
    }

    /**
     * Updates an existing booking with new details.
     *
     * @param userDTO       the DTO of user attempting to update the booking
     * @param roomName      the name of current room of the booking
     * @param originalStart the original start time of the booking
     * @param newRoomName   the new room's name for the booking
     * @param newStart      the new start time of the booking
     * @param newEnd        the new end time of the booking
     */
    public boolean updateBooking(UserDTO userDTO, String roomName, LocalDateTime originalStart,
                                 String newRoomName, LocalDateTime newStart, LocalDateTime newEnd) {

        Optional<Booking> optionalBooking = bookingRepository.findByRoomAndTime(originalStart, roomName);

        if (optionalBooking.isEmpty()) {
            System.out.println("Booking not found.");
            return false;
        }

        Booking booking = optionalBooking.get();
        User user = UserMapper.INSTANCE.toEntity(userDTO);

        if (!booking.getUser().equals(user) && !user.isAdmin()) {
            System.out.println("Denied. Must be the creator of the booking or have admin access");
            return false;
        }
        if (!isRoomAvailable(roomName, newStart, newEnd)) {
            System.out.println("The new time or room is not available. Try another time or resource.");
            return false;
        }
        Room newRoom = findRoomByName(newRoomName);
        Booking newBooking = new Booking(user, newRoom, newStart, newEnd);
        bookingRepository.update(booking, newBooking);
        System.out.println("Booking has been updated.");
        return true;
    }

    /**
     * Deletes a booking if it exists and if the user is an admin or the creator of the booking.
     *
     * @param userDTO   the DTO of user attempting to delete the booking
     * @param roomName  the room's name of the booking to delete
     * @param startTime the start time of the booking to delete
     */
    public boolean deleteBooking(UserDTO userDTO, String roomName, LocalDateTime startTime) {
        Optional<Booking> optionalBooking = bookingRepository.findByRoomAndTime(startTime, roomName);

        if (optionalBooking.isEmpty()) {
            System.out.println("No booking found at the specified time for the specified room.");
            return false;
        }

        Booking booking = optionalBooking.get();
        User user = UserMapper.INSTANCE.toEntity(userDTO);
        if (!user.isAdmin() && !booking.getUser().equals(user)) {
            System.out.println("Denied. Must be the creator of the booking or have admin access");
            return false;
        }

        bookingRepository.delete(booking);
        return true;
    }

    /**
     * Filters bookings by a specified date, user, or room.
     *
     * @param date     the date to filter bookings by
     * @param username the user's name to filter bookings by
     * @param roomName the room's name to filter bookings by
     * @return a list of bookings filtered by the specified argument
     */
    public Optional<List<BookingDTO>> filterBookings(LocalDate date, String username, String roomName) {
        if (date != null) {
            return Optional.of(bookingRepository.findByDate(date).stream().map(BookingMapper.INSTANCE::toDTO).toList());
        } else if (username != null) {
            return Optional.of(bookingRepository.findByUserName(username).stream().map(BookingMapper.INSTANCE::toDTO).toList());
        } else if (roomName != null) {
            return Optional.of(bookingRepository.findByRoomName(roomName).stream().map(BookingMapper.INSTANCE::toDTO).toList());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Checks if a room is available in the specified time interval.
     *
     * @param roomName  the room's name to check
     * @param startTime the start time of the interval
     * @param endTime   the end time of the interval
     * @return true if the room is available in the specified time interval, false otherwise
     */
    boolean isRoomAvailable(String roomName, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findByRoomName(roomName).stream()
                .allMatch(booking -> booking.getEndTime().isBefore(startTime) || booking.getStartTime().isAfter(endTime));
    }

    Room findRoomByName(String roomName) {
        return bookingRepository.findRoomByName(roomName);
    }

    private boolean userIsAdmin(UserDTO userDTO) {
        return userDTO.admin();
    }

}