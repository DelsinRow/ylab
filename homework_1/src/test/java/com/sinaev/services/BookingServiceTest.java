package com.sinaev.services;

import com.sinaev.models.Booking;
import com.sinaev.models.Room;
import com.sinaev.models.RoomType;
import com.sinaev.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingServiceTest {

    private BookingService bookingService;
    private DateTimeFormatter dateFormatter;
    private List<Booking> bookings;
    private List<Room> rooms;
    private User adminUser;
    private User normalUser;
    private Room room1;

    @BeforeEach
    public void setUp() {
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");
        bookings = new ArrayList<>();
        rooms = new ArrayList<>();
        room1 = new Room("Room1", RoomType.MEETING_ROOM);
        rooms.add(room1);
        bookingService = new BookingService(bookings, rooms);
        adminUser = new User("admin", "adminpass", true);
        normalUser = new User("user1", "password", false);
    }

    @Test
    public void testCreateBooking() {
        String startTime = "2024-06-20T10";
        String endTime = "2024-06-20T11";
        bookingService.createBooking(normalUser, room1, startTime, endTime);

        assert bookings.size() == 1;
        Booking booking = bookings.get(0);
        assert booking.getRoom().equals(room1);
        assert booking.getUser().equals(normalUser);
        assert booking.getStartTime().equals(LocalDateTime.parse(startTime, dateFormatter));
        assert booking.getEndTime().equals(LocalDateTime.parse(endTime, dateFormatter));
    }

    @Test
    public void testCreateBookingTimeNotAvailable() {
        String startTime = "2024-06-20T10";
        String endTime = "2024-06-20T11";
        bookingService.createBooking(normalUser, room1, startTime, endTime);
        bookingService.createBooking(normalUser, room1, startTime, endTime);

        assert bookings.size() == 1;
    }

    @Test
    public void testGetAvailableHours() {
        String startTime = "2024-06-20T10";
        String endTime = "2024-06-20T11";
        bookingService.createBooking(normalUser, room1, startTime, endTime);

        List<LocalTime> availableHours = bookingService.getAvailableHours(LocalDate.parse("2024-06-20"), room1);
        assert !availableHours.contains(LocalTime.of(10, 0));
        assert availableHours.contains(LocalTime.of(9, 0));
        assert availableHours.contains(LocalTime.of(11, 0));
    }

    @Test
    public void testUpdateBooking() {
        String originalStartTime = "2024-06-20T10";
        String newStartTime = "2024-06-20T12";
        String newEndTime = "2024-06-20T13";
        bookingService.createBooking(normalUser, room1, originalStartTime, "2024-06-20T11");

        bookingService.updateBooking(normalUser, originalStartTime, newStartTime, newEndTime, room1);

        assert bookings.size() == 1;
        Booking booking = bookings.get(0);
        assert booking.getStartTime().equals(LocalDateTime.parse(newStartTime, dateFormatter));
        assert booking.getEndTime().equals(LocalDateTime.parse(newEndTime, dateFormatter));
    }

    @Test
    public void testUpdateBookingUnauthorizedUser() {
        String originalStartTime = "2024-06-20T10";
        String newStartTime = "2024-06-20T12";
        String newEndTime = "2024-06-20T13";
        bookingService.createBooking(normalUser, room1, originalStartTime, "2024-06-20T11");

        bookingService.updateBooking(adminUser, originalStartTime, newStartTime, newEndTime, room1);

        assert bookings.size() == 1;
        Booking booking = bookings.get(0);
        assert booking.getStartTime().equals(LocalDateTime.parse(originalStartTime, dateFormatter));
        assert booking.getEndTime().equals(LocalDateTime.parse("2024-06-20T11", dateFormatter));
    }

    @Test
    public void testDeleteBookingAsAdmin() {
        String startTime = "2024-06-20T10";
        bookingService.createBooking(normalUser, room1, startTime, "2024-06-20T11");

        bookingService.deleteBooking(adminUser, room1, LocalDateTime.parse(startTime, dateFormatter));
        assert bookings.size() == 0;
    }

    @Test
    public void testDeleteBookingByCreator() {
        String startTime = "2024-06-20T10";
        bookingService.createBooking(normalUser, room1, startTime, "2024-06-20T11");

        bookingService.deleteBooking(normalUser, room1, LocalDateTime.parse(startTime, dateFormatter));
        assert bookings.size() == 0;
    }

    @Test
    public void testDeleteBookingUnauthorizedUser() {
        String startTime = "2024-06-20T10";
        bookingService.createBooking(normalUser, room1, startTime, "2024-06-20T11");

        bookingService.deleteBooking(new User("user2", "password2", false), room1, LocalDateTime.parse(startTime, dateFormatter));
        assert bookings.size() == 1;
    }

    @Test
    public void testFilterBookingsByDate() {
        String startTime1 = "2024-06-20T10";
        String endTime1 = "2024-06-20T11";
        String startTime2 = "2024-06-21T10";
        String endTime2 = "2024-06-21T11";
        bookingService.createBooking(normalUser, room1, startTime1, endTime1);
        bookingService.createBooking(normalUser, room1, startTime2, endTime2);

        LocalDateTime filterDate = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        List<Booking> filteredBookings = bookingService.filterBookings(filterDate, null, null);

        assert filteredBookings.size() == 1;
        assert filteredBookings.get(0).getStartTime().format(dateFormatter).equals(startTime1);
    }

    @Test
    public void testFilterBookingsByUser() {
        String startTime1 = "2024-06-20T10";
        String endTime1 = "2024-06-20T11";
        bookingService.createBooking(normalUser, room1, startTime1, endTime1);

        List<Booking> filteredBookings = bookingService.filterBookings(null, normalUser, null);

        assert filteredBookings.size() == 1;
        assert filteredBookings.get(0).getUser().equals(normalUser);
    }

    @Test
    public void testFilterBookingsByRoom() {
        String startTime1 = "2024-06-20T10";
        String endTime1 = "2024-06-20T11";
        bookingService.createBooking(normalUser, room1, startTime1, endTime1);

        List<Booking> filteredBookings = bookingService.filterBookings(null, null, room1);

        assert filteredBookings.size() == 1;
        assert filteredBookings.get(0).getRoom().equals(room1);
    }
}