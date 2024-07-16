package com.sinaev.services.impl;

import com.sinaev.annotations.Loggable;
import com.sinaev.exceptions.BookingIsNotAvailableException;
import com.sinaev.mappers.BookingMapper;
import com.sinaev.mappers.UserMapper;
import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.Booking;
import com.sinaev.models.entities.Room;
import com.sinaev.models.entities.User;
import com.sinaev.models.requests.booking.FilterBookingsRequest;
import com.sinaev.models.requests.booking.GetAvailableHoursRequest;
import com.sinaev.models.requests.booking.RemoveBookingRequest;
import com.sinaev.models.requests.booking.UpdateBookingRequest;
import com.sinaev.repositories.BookingRepository;
import com.sinaev.repositories.RoomRepository;
import com.sinaev.repositories.UserRepository;
import com.sinaev.services.BookingService;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Implementation of the {@link BookingService} interface.
 * <p>
 * This service manages bookings, including creating, updating, deleting, and filtering bookings.
 * It also checks room availability and retrieves available booking hours.
 * </p>
 */
@Service
@Loggable
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");

    /**
     * Creates a new booking.
     *
     * @param httpRequest the HTTP request containing user session information
     * @param bookingDTO  the booking data transfer object containing booking details
     */
    @Override
    public void createBooking(HttpServletRequest httpRequest, BookingDTO bookingDTO) {
        LocalDateTime start = bookingDTO.startTime();
        LocalDateTime end = bookingDTO.endTime();

        if (!isRoomAvailable(bookingDTO.roomName(), start, end)) {
            throw new BookingIsNotAvailableException("Booking this room and time is not available");
        } else {
            UserDTO userDTO = getUserDTOFromSession(httpRequest);
            Booking booking = BookingMapper.INSTANCE.toEntity(bookingDTO);
            User user = UserMapper.INSTANCE.toEntity(userDTO);
            booking.setUser(user);
            booking.setRoom(findRoomByName(bookingDTO.roomName()));
            bookingRepository.save(booking);
        }
    }

    /**
     * Retrieves available hours for booking a room on a specific date.
     *
     * @param request the request containing the date and room name
     * @return a list of available hours
     */
    @Override
    public List<LocalTime> getAvailableHours(GetAvailableHoursRequest request) {
        List<LocalTime> availableHours = new ArrayList<>();
        String roomName = request.roomName();
        if (findRoomByName(roomName) == null) {
            throw new NoSuchElementException("Room not found");
        }
        LocalDate date = request.date();
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime startTime = date.atTime(hour, 0);
            LocalDateTime endTime = startTime.plusHours(1);

            boolean isAvailable = bookingRepository.findByRoomName(roomName).stream()
                    .noneMatch(booking -> booking.getStartTime().isBefore(endTime) && booking.getEndTime().isAfter(startTime));

            if (isAvailable) {
                availableHours.add(startTime.toLocalTime());
            }
        }
        return availableHours;
    }

    /**
     * Updates an existing booking.
     *
     * @param httpRequest the HTTP request containing user session information
     * @param request     the request containing the original and new booking details
     */
    @Override
    public void updateBooking(HttpServletRequest httpRequest, UpdateBookingRequest request) {
        LocalDateTime originalStart = request.originalStartTime();
        String roomName = request.originalRoomName();

        Optional<Booking> optionalBooking = bookingRepository.findByRoomAndTime(roomName, originalStart);

        if (optionalBooking.isEmpty()) {
            throw new NoSuchElementException("Booking not found.");
        }

        Booking booking = optionalBooking.get();
        UserDTO userDTO = getUserDTOFromSession(httpRequest);
        User user = UserMapper.INSTANCE.toEntity(userDTO);

        if (!booking.getUser().equals(user) && !user.isAdmin()) {
            throw new SecurityException("Denied. Must be the creator of the booking or have admin access");
        }

        String newRoomName = request.newRoomName();
        LocalDateTime newStart = request.newStartTime();
        LocalDateTime newEnd = request.newEndTime();
        if (!isRoomAvailable(newRoomName, newStart, newEnd)) {
            throw new BookingIsNotAvailableException("The new time or room is not available. Try another time or resource.");
        }
        Room newRoom = findRoomByName(newRoomName);
        Booking newBooking = new Booking(user, newRoom, newStart, newEnd);
        bookingRepository.update(booking, newBooking);
    }

    /**
     * Deletes an existing booking.
     *
     * @param httpRequest the HTTP request containing user session information
     * @param request     the request containing the booking details to be deleted
     */
    @Override
    public void deleteBooking(HttpServletRequest httpRequest, RemoveBookingRequest request) {
        LocalDateTime startTime = request.startTime();
        String roomName = request.roomName();
        Optional<Booking> optionalBooking = bookingRepository.findByRoomAndTime(roomName, startTime);

        if (optionalBooking.isEmpty()) {
            throw new BookingIsNotAvailableException("No booking found at the specified time for the specified room.");
        }

        Booking booking = optionalBooking.get();
        UserDTO userDTO = getUserDTOFromSession(httpRequest);
        User user = UserMapper.INSTANCE.toEntity(userDTO);
        if (!user.isAdmin() && !booking.getUser().equals(user)) {
            throw new SecurityException("Denied. Must be the creator of the booking or have admin access");
        }

        bookingRepository.delete(booking);
    }

    /**
     * Filters bookings based on the specified criteria.
     *
     * @param request the request containing the filtering criteria
     * @return a list of filtered bookings
     */
    @Override
    public List<BookingDTO> filterBookings(FilterBookingsRequest request) {
        Optional<User> user;
        if (request.username() != null) {
            String username = request.username();
            user = userRepository.findByUsername(username);
        } else {
            user = Optional.empty();
        }
        Optional<Room> room;
        if (request.roomName() != null) {
            String roomName = request.roomName();
            room = roomRepository.findByName(roomName);
        } else {
            room = Optional.empty();
        }
        LocalDate date;
        if (request.date() != null) {
            date = request.date();
        } else {
            date = null;
        }


        List<Booking> filterResult = bookingRepository.findAll();
        if (user.isPresent()) {
            filterResult = filterResult.stream()
                    .filter(booking -> booking
                            .getUser()
                            .equals(user.get()))
                    .toList();
        }
        if (room.isPresent()) {
            filterResult = filterResult.stream()
                    .filter(booking -> booking
                            .getRoom()
                            .equals(room.get()))
                    .toList();
        }
        if (date != null) {
            filterResult = filterResult.stream()
                    .filter(booking -> booking
                            .getStartTime().toLocalDate()
                            .equals(date))
                    .toList();
        }

        return filterResult.stream().map(BookingMapper.INSTANCE::toDTO).toList();
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

    /**
     * Finds a room by its name.
     *
     * @param roomName the name of the room to find
     * @return the room with the specified name
     */
    Room findRoomByName(String roomName) {
        return bookingRepository.findRoomByName(roomName);
    }

    /**
     * Retrieves the user DTO from the session.
     *
     * @param req the HTTP request containing the session
     * @return the user DTO
     * @throws NoSuchElementException if the user is not found in the session
     */
    private UserDTO getUserDTOFromSession(HttpServletRequest req) {
        UserDTO userDTO = (UserDTO) req.getSession().getAttribute("loggedIn");
        if (userDTO == null) {
            throw new NoSuchElementException("Log in first");
        } else {
            return userDTO;
        }
    }
}