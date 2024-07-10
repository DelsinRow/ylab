package com.sinaev.services;

import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.Booking;
import com.sinaev.models.entities.Room;
import com.sinaev.models.entities.User;
import com.sinaev.models.enums.RoomType;
import com.sinaev.repositories.BookingRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.Mockito.*;

public class BookingServiceTest {

    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private DateTimeFormatter dateFormatter;
    private UserDTO adminUserDTO;
    private UserDTO normalUserDTO;
    private Room room1;
    private Room room2;

    @BeforeEach
    public void setUp() {
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");
        bookingRepository = Mockito.mock(BookingRepository.class);
        bookingService = Mockito.spy(new BookingService(bookingRepository));
        adminUserDTO = new UserDTO("admin", "adminpass", true);
        normalUserDTO = new UserDTO("user1", "password", false);
        room1 = new Room("Room1", RoomType.MEETING_ROOM);
        room2 = new Room("Room2", RoomType.MEETING_ROOM);
    }

    @Test
    @DisplayName("Should create booking if room is available")
    public void testCreateBooking() {
        BookingDTO bookingDTO = new BookingDTO("User", "Room1", LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(bookingRepository.findByRoomName("Room1")).thenReturn(Collections.emptyList());
        when(bookingService.isRoomAvailable("Room1", bookingDTO.startTime(), bookingDTO.endTime())).thenReturn(true);
        when(bookingService.findRoomByName("Room1")).thenReturn(room1);

        Optional<BookingDTO> createdBooking = bookingService.createBooking(normalUserDTO, bookingDTO);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(createdBooking).isPresent();
        softly.assertThat(createdBooking.get().roomName()).isEqualTo("Room1");
        verify(bookingRepository, times(1)).save(any(Booking.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not create booking if room is not available")
    public void testCreateBookingTimeNotAvailable() {
        BookingDTO bookingDTO = new BookingDTO("User", "Room1", LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));
        Booking existingBooking = new Booking(new User("user1", "password"), room1, bookingDTO.startTime(), bookingDTO.endTime());

        when(bookingRepository.findByRoomName("Room1")).thenReturn(List.of(existingBooking));
        when(bookingService.isRoomAvailable("Room1", bookingDTO.startTime(), bookingDTO.endTime())).thenReturn(false);

        Optional<BookingDTO> createdBooking = bookingService.createBooking(normalUserDTO, bookingDTO);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(createdBooking).isNotPresent();
        verify(bookingRepository, never()).save(any(Booking.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should get available hours for a room")
    public void testGetAvailableHours() {
        String startTime = "2024-06-20T10";
        String endTime = "2024-06-20T11";
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);
        LocalDateTime end = LocalDateTime.parse(endTime, dateFormatter);

        Booking existingBooking = new Booking(new User("user1", "password"), room1, start, end);
        when(bookingRepository.findByRoomName("Room1")).thenReturn(List.of(existingBooking));
        when(bookingService.findRoomByName("Room1")).thenReturn(room1);

        Optional<List<LocalTime>> availableHours = bookingService.getAvailableHours(LocalDate.parse("2024-06-20", DateTimeFormatter.ISO_DATE), "Room1");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(availableHours).isPresent();
        softly.assertThat(availableHours.get()).doesNotContain(LocalTime.of(10, 0));
        softly.assertThat(availableHours.get()).contains(LocalTime.of(9, 0), LocalTime.of(11, 0));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should update booking by creator")
    public void testUpdateBooking() {
        String originalStartTime = "2024-06-20T10";
        String newStartTime = "2024-06-20T12";
        String newEndTime = "2024-06-20T13";
        LocalDateTime originalStart = LocalDateTime.parse(originalStartTime, dateFormatter);
        LocalDateTime newStart = LocalDateTime.parse(newStartTime, dateFormatter);
        LocalDateTime newEnd = LocalDateTime.parse(newEndTime, dateFormatter);

        Booking booking = new Booking(new User("user1", "password"), room1, originalStart, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        when(bookingRepository.findByRoomAndTime(originalStart, "Room1")).thenReturn(Optional.of(booking));
        when(bookingService.isRoomAvailable("Room2", newStart, newEnd)).thenReturn(true);
        when(bookingService.findRoomByName("Room2")).thenReturn(room2);

        boolean updated = bookingService.updateBooking(normalUserDTO, "Room1", originalStart, "Room2", newStart, newEnd);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(updated).isTrue();

        ArgumentCaptor<Booking> oldBookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<Booking> newBookingCaptor = ArgumentCaptor.forClass(Booking.class);

        verify(bookingRepository).update(oldBookingCaptor.capture(), newBookingCaptor.capture());

        Booking capturedOldBooking = oldBookingCaptor.getValue();
        Booking capturedNewBooking = newBookingCaptor.getValue();

        softly.assertThat(capturedOldBooking).isEqualTo(booking);
        softly.assertThat(capturedNewBooking.getStartTime()).isEqualTo(newStart);
        softly.assertThat(capturedNewBooking.getEndTime()).isEqualTo(newEnd);
        softly.assertThat(capturedNewBooking.getRoom()).isEqualTo(room2);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should update booking by admin")
    public void testUpdateBookingAsAdmin() {
        String originalStartTime = "2024-06-20T10";
        String newStartTime = "2024-06-20T12";
        String newEndTime = "2024-06-20T13";
        LocalDateTime originalStart = LocalDateTime.parse(originalStartTime, dateFormatter);
        LocalDateTime newStart = LocalDateTime.parse(newStartTime, dateFormatter);
        LocalDateTime newEnd = LocalDateTime.parse(newEndTime, dateFormatter);

        Booking booking = new Booking(new User("admin", "adminpass"), room1, originalStart, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        when(bookingRepository.findByRoomAndTime(originalStart, "Room1")).thenReturn(Optional.of(booking));
        when(bookingService.isRoomAvailable("Room2", newStart, newEnd)).thenReturn(true);
        when(bookingService.findRoomByName("Room2")).thenReturn(room2);

        boolean updated = bookingService.updateBooking(adminUserDTO, "Room1", originalStart, "Room2", newStart, newEnd);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(updated).isTrue();

        ArgumentCaptor<Booking> oldBookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<Booking> newBookingCaptor = ArgumentCaptor.forClass(Booking.class);

        verify(bookingRepository).update(oldBookingCaptor.capture(), newBookingCaptor.capture());

        Booking capturedOldBooking = oldBookingCaptor.getValue();
        Booking capturedNewBooking = newBookingCaptor.getValue();

        softly.assertThat(capturedOldBooking).isEqualTo(booking);
        softly.assertThat(capturedNewBooking.getStartTime()).isEqualTo(newStart);
        softly.assertThat(capturedNewBooking.getEndTime()).isEqualTo(newEnd);
        softly.assertThat(capturedNewBooking.getRoom()).isEqualTo(room2);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not update non-existing booking")
    public void testUpdateBookingBookingNotFound() {
        String originalStartTime = "2024-06-20T10";
        String newStartTime = "2024-06-20T12";
        String newEndTime = "2024-06-20T13";
        LocalDateTime originalStart = LocalDateTime.parse(originalStartTime, dateFormatter);

        when(bookingRepository.findByRoomAndTime(originalStart, "Room1")).thenReturn(Optional.empty());

        boolean updated = bookingService.updateBooking(normalUserDTO, "Room1", originalStart, "Room2", LocalDateTime.parse("2024-06-20T16", dateFormatter), LocalDateTime.parse("2024-06-20T17", dateFormatter));
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(updated).isFalse();
        verify(bookingRepository, never()).update(any(Booking.class), any(Booking.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should delete booking by admin")
    public void testDeleteBookingAsAdmin() {
        String startTime = "2024-06-20T10";
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);

        Booking existingBooking = new Booking(new User("user1", "password"), room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        when(bookingRepository.findByRoomAndTime(start, "Room1")).thenReturn(Optional.of(existingBooking));

        boolean deleted = bookingService.deleteBooking(adminUserDTO, "Room1", start);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(deleted).isTrue();
        verify(bookingRepository).delete(existingBooking);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should delete booking by creator")
    public void testDeleteBookingByCreator() {
        String startTime = "2024-06-20T10";
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);

        Booking existingBooking = new Booking(new User("user1", "password"), room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        when(bookingRepository.findByRoomAndTime(start, "Room1")).thenReturn(Optional.of(existingBooking));

        boolean deleted = bookingService.deleteBooking(normalUserDTO, "Room1", start);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(deleted).isTrue();
        verify(bookingRepository).delete(existingBooking);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not delete booking by unauthorized user")
    public void testDeleteBookingUnauthorizedUser() {
        String startTime = "2024-06-20T10";
        LocalDateTime start = LocalDateTime.parse(startTime, dateFormatter);

        Booking existingBooking = new Booking(new User("user1", "password"), room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        when(bookingRepository.findByRoomAndTime(start, "Room1")).thenReturn(Optional.of(existingBooking));

        boolean deleted = bookingService.deleteBooking(new UserDTO("user2", "password2", false), "Room1", start);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(deleted).isFalse();
        verify(bookingRepository, never()).delete(existingBooking);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should filter bookings by date")
    public void testFilterBookingsByDate() {
        LocalDate filterDate = LocalDate.parse("2024-06-20", DateTimeFormatter.ISO_DATE);
        Booking booking = new Booking(new User("user1", "password"), room1, LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(bookingRepository.findByDate(filterDate)).thenReturn(List.of(booking));

        Optional<List<BookingDTO>> filteredBookings = bookingService.filterBookings(filterDate, null, null);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(filteredBookings).isPresent();
        softly.assertThat(filteredBookings.get()).hasSize(1);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should filter bookings by user")
    public void testFilterBookingsByUser() {
        Booking booking = new Booking(new User("user1", "password"), room1, LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(bookingRepository.findByUserName("user1")).thenReturn(List.of(booking));

        Optional<List<BookingDTO>> filteredBookings = bookingService.filterBookings(null, "user1", null);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(filteredBookings).isPresent();
        softly.assertThat(filteredBookings.get()).hasSize(1);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should filter bookings by room")
    public void testFilterBookingsByRoom() {
        Booking booking = new Booking(new User("user1", "password"), room1, LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(bookingRepository.findByRoomName("Room1")).thenReturn(List.of(booking));

        Optional<List<BookingDTO>> filteredBookings = bookingService.filterBookings(null, null, "Room1");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(filteredBookings).isPresent();
        softly.assertThat(filteredBookings.get()).hasSize(1);
        softly.assertAll();
    }
}