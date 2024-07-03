package com.sinaev.services;

import com.sinaev.models.Booking;
import com.sinaev.models.Room;
import com.sinaev.models.RoomType;
import com.sinaev.models.User;
import com.sinaev.repositories.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private DateTimeFormatter dateFormatter;
    private User adminUser;
    private User normalUser;
    private Room room1;
    private Room room2;

    /**
     * Set up the test environment.
     * This method is executed before each test.
     */
    @BeforeEach
    public void setUp() {
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");
        bookingRepository = Mockito.mock(BookingRepository.class);
        bookingService = Mockito.spy(new BookingService(bookingRepository));
        adminUser = new User("admin", "adminpass", true);
        normalUser = new User("user1", "password", false);
        room1 = new Room("Room1", RoomType.MEETING_ROOM);
        room2 = new Room("Room2", RoomType.MEETING_ROOM);
    }

    /**
     * Tests the creation of a booking.
     * Steps:
     * 1. Mock the repository to return an empty list for room availability.
     * 2. Call the createBooking method.
     * 3. Verify that the booking is saved in the repository.
     * Expected result: The booking is created and saved in the repository.
     */
    @Test
    public void testCreateBooking() {
        String startTime = "2024-06-20T10";
        String endTime = "2024-06-20T11";
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);
        LocalDateTime end = LocalDateTime.parse(endTime, dateFormatter);

        when(bookingRepository.findByRoom(room1)).thenReturn(Collections.emptyList());

        bookingService.createBooking(normalUser, room1, startTime, endTime);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingRepository, times(1)).findByRoom(room1);
    }

    /**
     * Tests the creation of a booking when the time is not available.
     * Steps:
     * 1. Mock the repository to return an existing booking.
     * 2. Call the createBooking method.
     * 3. Verify that the booking is not saved in the repository.
     * Expected result: The booking is not created and not saved in the repository.
     */
    @Test
    public void testCreateBookingTimeNotAvailable() {
        String startTime = "2024-06-20T10";
        String endTime = "2024-06-20T11";
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);
        LocalDateTime end = LocalDateTime.parse(endTime, dateFormatter);

        Booking existingBooking = new Booking(normalUser, room1, start, end);
        when(bookingRepository.findByRoom(room1)).thenReturn(List.of(existingBooking));

        bookingService.createBooking(normalUser, room1, startTime, endTime);

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    /**
     * Tests the retrieval of available hours for a room on a specific date.
     * Steps:
     * 1. Mock the repository to return an existing booking.
     * 2. Call the getAvailableHours method.
     * 3. Verify the available hours.
     * Expected result: The available hours do not include the booked time.
     */
    @Test
    public void testGetAvailableHours() {
        String startTime = "2024-06-20T10";
        String endTime = "2024-06-20T11";
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);
        LocalDateTime end = LocalDateTime.parse(endTime, dateFormatter);

        Booking existingBooking = new Booking(normalUser, room1, start, end);
        when(bookingRepository.findByRoom(room1)).thenReturn(List.of(existingBooking));

        List<LocalTime> availableHours = bookingService.getAvailableHours(LocalDate.parse("2024-06-20"), room1);

        assertThat(availableHours).doesNotContain(LocalTime.of(10, 0));
        assertThat(availableHours).contains(LocalTime.of(9, 0), LocalTime.of(11, 0));
    }

    /**
     * Tests the update of a booking by the booking creator.
     * Steps:
     * 1. Mock the repository to return an existing booking.
     * 2. Call the updateBooking method.
     * 3. Verify that the booking is updated with the new times.
     * Expected result: The booking is updated with the new times.
     */
    @Test
    public void testUpdateBooking() {
        String originalStartTime = "2024-06-20T10";
        String newStartTime = "2024-06-20T12";
        String newEndTime = "2024-06-20T13";
        LocalDateTime originalStart = LocalDateTime.parse(originalStartTime, dateFormatter);
        LocalDateTime newStart = LocalDateTime.parse(newStartTime, dateFormatter);
        LocalDateTime newEnd = LocalDateTime.parse(newEndTime, dateFormatter);

        Booking booking = new Booking(normalUser, room1, originalStart, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        when(bookingRepository.findByRoomAndTime(originalStart, room1)).thenReturn(Optional.of(booking));
        when(bookingService.isRoomAvailable(room2, newStart, newEnd)).thenReturn(true);

        bookingService.updateBooking(normalUser, room1, originalStartTime, room2, newStartTime, newEndTime);

        ArgumentCaptor<Booking> oldBookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<Booking> newBookingCaptor = ArgumentCaptor.forClass(Booking.class);

        verify(bookingRepository, times(1)).update(oldBookingCaptor.capture(), newBookingCaptor.capture());

        Booking capturedOldBooking = oldBookingCaptor.getValue();
        Booking capturedNewBooking = newBookingCaptor.getValue();

        assertThat(capturedOldBooking).isEqualTo(booking);
        assertThat(capturedNewBooking.getStartTime()).isEqualTo(newStart);
        assertThat(capturedNewBooking.getEndTime()).isEqualTo(newEnd);
        assertThat(capturedNewBooking.getRoom()).isEqualTo(room2);
    }

    /**
     * Tests the update of a booking by an admin user.
     * Steps:
     * 1. Mock the repository to return an existing booking.
     * 2. Call the updateBooking method.
     * 3. Verify that the booking is updated with the new times.
     * Expected result: The booking is updated with the new times.
     */
    @Test
    public void testUpdateBookingAsAdmin() {
        String originalStartTime = "2024-06-20T10";
        String newStartTime = "2024-06-20T12";
        String newEndTime = "2024-06-20T13";
        LocalDateTime originalStart = LocalDateTime.parse(originalStartTime, dateFormatter);
        LocalDateTime newStart = LocalDateTime.parse(newStartTime, dateFormatter);
        LocalDateTime newEnd = LocalDateTime.parse(newEndTime, dateFormatter);

        Booking booking = new Booking(adminUser, room1, originalStart, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        when(bookingRepository.findByRoomAndTime(originalStart, room1)).thenReturn(Optional.of(booking));
        when(bookingService.isRoomAvailable(room2, newStart, newEnd)).thenReturn(true);

        bookingService.updateBooking(adminUser, room1, originalStartTime, room2, newStartTime, newEndTime);

        ArgumentCaptor<Booking> oldBookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<Booking> newBookingCaptor = ArgumentCaptor.forClass(Booking.class);

        verify(bookingRepository, times(1)).update(oldBookingCaptor.capture(), newBookingCaptor.capture());

        Booking capturedOldBooking = oldBookingCaptor.getValue();
        Booking capturedNewBooking = newBookingCaptor.getValue();

        assertThat(capturedOldBooking).isEqualTo(booking);
        assertThat(capturedNewBooking.getStartTime()).isEqualTo(newStart);
        assertThat(capturedNewBooking.getEndTime()).isEqualTo(newEnd);
        assertThat(capturedNewBooking.getRoom()).isEqualTo(room2);
    }

    /**
     * Tests the update of a booking that does not exist.
     * Steps:
     * 1. Mock the repository to return an empty Optional.
     * 2. Call the updateBooking method.
     * 3. Verify that the booking is not updated.
     * Expected result: The booking is not found and not updated.
     */
    @Test
    public void testUpdateBookingBookingNotFound() {
        String originalStartTime = "2024-06-20T10";
        String newStartTime = "2024-06-20T12";
        String newEndTime = "2024-06-20T13";
        LocalDateTime originalStart = LocalDateTime.parse(originalStartTime, dateFormatter);
        when(bookingRepository.findByRoomAndTime(originalStart, room1)).thenReturn(Optional.empty());

        bookingService.updateBooking(normalUser, room1, originalStartTime, room2, newStartTime, newEndTime);

        verify(bookingRepository, times(1)).findByRoomAndTime(originalStart, room1);
    }

    /**
     * Tests the deletion of a booking by an admin user.
     * Steps:
     * 1. Mock the repository to return an existing booking.
     * 2. Call the deleteBooking method.
     * 3. Verify that the booking is deleted from the repository.
     * Expected result: The booking is deleted from the repository.
     */
    @Test
    public void testDeleteBookingAsAdmin() {
        String startTime = "2024-06-20T10";
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);

        Booking existingBooking = new Booking(normalUser, room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        when(bookingRepository.findByRoomAndTime(start, room1)).thenReturn(Optional.of(existingBooking));

        bookingService.deleteBooking(adminUser, room1, start);

        verify(bookingRepository, times(1)).delete(existingBooking);
    }

    /**
     * Tests the deletion of a booking by the booking creator.
     * Steps:
     * 1. Mock the repository to return an existing booking.
     * 2. Call the deleteBooking method.
     * 3. Verify that the booking is deleted from the repository.
     * Expected result: The booking is deleted from the repository.
     */
    @Test
    public void testDeleteBookingByCreator() {
        String startTime = "2024-06-20T10";
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);

        Booking existingBooking = new Booking(normalUser, room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        when(bookingRepository.findByRoomAndTime(start, room1)).thenReturn(Optional.of(existingBooking));

        bookingService.deleteBooking(normalUser, room1, start);

        verify(bookingRepository, times(1)).delete(existingBooking);
    }

    /**
     * Tests the deletion of a booking by an unauthorized user.
     * Steps:
     * 1. Mock the repository to return an existing booking.
     * 2. Call the deleteBooking method.
     * 3. Verify that the booking is not deleted from the repository.
     * Expected result: The booking is not deleted from the repository.
     */
    @Test
    public void testDeleteBookingUnauthorizedUser() {
        String startTime = "2024-06-20T10";
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);

        Booking existingBooking = new Booking(normalUser, room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        when(bookingRepository.findByRoomAndTime(start, room1)).thenReturn(Optional.of(existingBooking));

        bookingService.deleteBooking(new User("user2", "password2", false), room1, start);

        verify(bookingRepository, never()).delete(existingBooking);
    }

    /**
     * Tests the filtering of bookings by date.
     * Steps:
     * 1. Mock the repository to return a list of bookings.
     * 2. Call the filterBookings method.
     * 3. Verify the filtered bookings.
     * Expected result: The bookings are filtered by date.
     */
    @Test
    public void testFilterBookingsByDate() {
        String startTime1 = "2024-06-20T10";
        String endTime1 = "2024-06-20T11";
        String startTime2 = "2024-06-21T10";
        String endTime2 = "2024-06-21T11";
        LocalDateTime filterDate = LocalDateTime.parse("2024-06-20T10", dateFormatter);

        Booking booking1 = new Booking(normalUser, room1, LocalDateTime.parse(startTime1, dateFormatter), LocalDateTime.parse(endTime1, dateFormatter));
        Booking booking2 = new Booking(normalUser, room1, LocalDateTime.parse(startTime2, dateFormatter), LocalDateTime.parse(endTime2, dateFormatter));
        when(bookingRepository.findByDate(filterDate)).thenReturn(List.of(booking1));

        List<Booking> filteredBookings = bookingService.filterBookings(filterDate, null, null);

        assertThat(filteredBookings).hasSize(1);
        assertThat(filteredBookings.get(0).getStartTime().format(dateFormatter)).isEqualTo(startTime1);
    }

    /**
     * Tests the filtering of bookings by user.
     * Steps:
     * 1. Mock the repository to return a list of bookings.
     * 2. Call the filterBookings method.
     * 3. Verify the filtered bookings.
     * Expected result: The bookings are filtered by user.
     */
    @Test
    public void testFilterBookingsByUser() {
        String startTime1 = "2024-06-20T10";
        String endTime1 = "2024-06-20T11";
        LocalDateTime start = LocalDateTime.parse(startTime1, dateFormatter);
        LocalDateTime end = LocalDateTime.parse(endTime1, dateFormatter);

        Booking booking1 = new Booking(normalUser, room1, start, end);
        when(bookingRepository.findByUser(normalUser)).thenReturn(List.of(booking1));

        List<Booking> filteredBookings = bookingService.filterBookings(null, normalUser, null);

        assertThat(filteredBookings).hasSize(1);
        assertThat(filteredBookings.get(0).getUser()).isEqualTo(normalUser);
    }

    /**
     * Tests the filtering of bookings by room.
     * Steps:
     * 1. Mock the repository to return a list of bookings.
     * 2. Call the filterBookings method.
     * 3. Verify the filtered bookings.
     * Expected result: The bookings are filtered by room.
     */
    @Test
    public void testFilterBookingsByRoom() {
        String startTime1 = "2024-06-20T10";
        String endTime1 = "2024-06-20T11";
        LocalDateTime start = LocalDateTime.parse(startTime1, dateFormatter);
        LocalDateTime end = LocalDateTime.parse(endTime1, dateFormatter);

        Booking booking1 = new Booking(normalUser, room1, start, end);
        when(bookingRepository.findByRoom(room1)).thenReturn(List.of(booking1));

        List<Booking> filteredBookings = bookingService.filterBookings(null, null, room1);

        assertThat(filteredBookings).hasSize(1);
        assertThat(filteredBookings.get(0).getRoom()).isEqualTo(room1);
    }
}