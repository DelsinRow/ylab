package com.sinaev.services;

import com.sinaev.exceptions.BookingIsNotAvailableException;
import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.Booking;
import com.sinaev.models.entities.Room;
import com.sinaev.models.entities.User;
import com.sinaev.models.enums.RoomType;
import com.sinaev.models.requests.booking.FilterBookingsRequest;
import com.sinaev.models.requests.booking.RemoveBookingRequest;
import com.sinaev.models.requests.booking.UpdateBookingRequest;
import com.sinaev.repositories.BookingRepository;
import com.sinaev.repositories.RoomRepository;
import com.sinaev.repositories.UserRepository;
import com.sinaev.services.impl.BookingServiceImpl;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingServiceTest {

    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private HttpServletRequest httpRequest;
    private HttpSession httpSession;
    private DateTimeFormatter dateFormatter;
    private UserDTO adminUserDTO;
    private UserDTO normalUserDTO;
    private Room room1;
    private Room room2;

    @BeforeEach
    public void setUp() {
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");
        bookingRepository = Mockito.mock(BookingRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        roomRepository = Mockito.mock(RoomRepository.class);
        httpRequest = Mockito.mock(HttpServletRequest.class);
        httpSession = Mockito.mock(HttpSession.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, roomRepository);
        adminUserDTO = new UserDTO("admin", "adminpass", true);
        normalUserDTO = new UserDTO("user1", "password", false);
        room1 = new Room("Room1", RoomType.MEETING_ROOM);
        room2 = new Room("Room2", RoomType.MEETING_ROOM);

        when(httpRequest.getSession()).thenReturn(httpSession);
    }

    @Test
    @DisplayName("Should create booking if room is available")
    public void testCreateBooking() {
        BookingDTO bookingDTO = new BookingDTO("User", "Room1", LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(bookingRepository.findByRoomName("Room1")).thenReturn(Collections.emptyList());
        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room1));
        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);

        bookingService.createBooking(httpRequest, bookingDTO);

        SoftAssertions softly = new SoftAssertions();
        verify(bookingRepository, times(1)).save(any(Booking.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not create booking if room is not available")
    public void testCreateBookingTimeNotAvailable() {
        BookingDTO bookingDTO = new BookingDTO("User", "Room1", LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));
        Booking existingBooking = new Booking(new User("user1", "password"), room1, bookingDTO.startTime(), bookingDTO.endTime());

        when(bookingRepository.findByRoomName("Room1")).thenReturn(List.of(existingBooking));
        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatThrownBy(() -> bookingService.createBooking(httpRequest, bookingDTO))
                .isInstanceOf(BookingIsNotAvailableException.class)
                .hasMessageContaining("Booking this room and time is not available");
        verify(bookingRepository, never()).save(any(Booking.class));
        softly.assertAll();
    }


    @Test
    @DisplayName("Should update booking by creator")
    public void testUpdateBooking() {
        LocalDateTime originalStart = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        LocalDateTime newStart = LocalDateTime.parse("2024-06-20T12", dateFormatter);
        LocalDateTime newEnd = LocalDateTime.parse("2024-06-20T13", dateFormatter);
        UpdateBookingRequest request = new UpdateBookingRequest("Room1", originalStart, "Room2", newStart, newEnd);

        Booking booking = new Booking(new User("user1", "password"), room1, originalStart, LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);
        when(bookingRepository.findByRoomAndTime("Room1", originalStart)).thenReturn(Optional.of(booking));
        when(roomRepository.findByName("Room2")).thenReturn(Optional.of(room2));

        bookingService.updateBooking(httpRequest, request);

        SoftAssertions softly = new SoftAssertions();
        ArgumentCaptor<Booking> oldBookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<Booking> newBookingCaptor = ArgumentCaptor.forClass(Booking.class);

        verify(bookingRepository).update(oldBookingCaptor.capture(), newBookingCaptor.capture());

        Booking capturedOldBooking = oldBookingCaptor.getValue();
        Booking capturedNewBooking = newBookingCaptor.getValue();

        softly.assertThat(capturedOldBooking).isEqualTo(booking);
        softly.assertThat(capturedNewBooking.getStartTime()).isEqualTo(newStart);
        softly.assertThat(capturedNewBooking.getEndTime()).isEqualTo(newEnd);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should update booking by admin")
    public void testUpdateBookingAsAdmin() {
        LocalDateTime originalStart = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        LocalDateTime newStart = LocalDateTime.parse("2024-06-20T12", dateFormatter);
        LocalDateTime newEnd = LocalDateTime.parse("2024-06-20T13", dateFormatter);
        UpdateBookingRequest request = new UpdateBookingRequest("Room1", originalStart, "Room2", newStart, newEnd);

        Booking booking = new Booking(new User("admin", "adminpass"), room1, originalStart, LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        when(bookingRepository.findByRoomAndTime("Room1", originalStart)).thenReturn(Optional.of(booking));
        when(roomRepository.findByName("Room2")).thenReturn(Optional.of(room2));

        bookingService.updateBooking(httpRequest, request);

        SoftAssertions softly = new SoftAssertions();
        ArgumentCaptor<Booking> oldBookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<Booking> newBookingCaptor = ArgumentCaptor.forClass(Booking.class);

        verify(bookingRepository).update(oldBookingCaptor.capture(), newBookingCaptor.capture());

        Booking capturedOldBooking = oldBookingCaptor.getValue();
        Booking capturedNewBooking = newBookingCaptor.getValue();

        softly.assertThat(capturedOldBooking).isEqualTo(booking);
        softly.assertThat(capturedNewBooking.getStartTime()).isEqualTo(newStart);
        softly.assertThat(capturedNewBooking.getEndTime()).isEqualTo(newEnd);
        softly.assertAll();
    }


    @Test
    @DisplayName("Should delete booking by admin")
    public void testDeleteBookingAsAdmin() {
        LocalDateTime start = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        RemoveBookingRequest request = new RemoveBookingRequest("Room1", start);

        Booking existingBooking = new Booking(new User("user1", "password"), room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        when(bookingRepository.findByRoomAndTime("Room1", start)).thenReturn(Optional.of(existingBooking));

        bookingService.deleteBooking(httpRequest, request);

        SoftAssertions softly = new SoftAssertions();
        verify(bookingRepository).delete(existingBooking);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should delete booking by creator")
    public void testDeleteBookingByCreator() {
        LocalDateTime start = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        RemoveBookingRequest request = new RemoveBookingRequest("Room1", start);

        Booking existingBooking = new Booking(new User("user1", "password"), room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);
        when(bookingRepository.findByRoomAndTime("Room1", start)).thenReturn(Optional.of(existingBooking));

        bookingService.deleteBooking(httpRequest, request);

        SoftAssertions softly = new SoftAssertions();
        verify(bookingRepository).delete(existingBooking);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not delete booking by unauthorized user")
    public void testDeleteBookingUnauthorizedUser() {
        LocalDateTime start = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        RemoveBookingRequest request = new RemoveBookingRequest("Room1", start);

        Booking existingBooking = new Booking(new User("user1", "password"), room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(httpSession.getAttribute("loggedIn")).thenReturn(new UserDTO("user2", "password2", false));
        when(bookingRepository.findByRoomAndTime("Room1", start)).thenReturn(Optional.of(existingBooking));

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatThrownBy(() -> bookingService.deleteBooking(httpRequest, request))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Denied. Must be the creator of the booking or have admin access");
        verify(bookingRepository, never()).delete(existingBooking);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should filter bookings by date")
    public void testFilterBookingsByDate() {
        LocalDate filterDate = LocalDate.parse("2024-06-20", DateTimeFormatter.ISO_DATE);
        FilterBookingsRequest request = new FilterBookingsRequest(filterDate, null, null);
        Booking booking = new Booking(new User("user1", "password"), room1, LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDTO> filteredBookings = bookingService.filterBookings(request);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(filteredBookings).hasSize(1);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should filter bookings by user")
    public void testFilterBookingsByUser() {
        FilterBookingsRequest request = new FilterBookingsRequest(null, "user1", null);
        Booking booking = new Booking(new User("user1", "password"), room1, LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(new User("user1", "password")));
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDTO> filteredBookings = bookingService.filterBookings(request);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(filteredBookings).hasSize(1);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should filter bookings by room")
    public void testFilterBookingsByRoom() {
        FilterBookingsRequest request = new FilterBookingsRequest(null, null, "Room1");
        Booking booking = new Booking(new User("user1", "password"), room1, LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room1));
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDTO> filteredBookings = bookingService.filterBookings(request);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(filteredBookings).hasSize(1);
        softly.assertAll();
    }
}