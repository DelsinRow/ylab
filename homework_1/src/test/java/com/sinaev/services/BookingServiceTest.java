package com.sinaev.services;

import com.sinaev.exceptions.BookingIsNotAvailableException;
import com.sinaev.mappers.BookingMapper;
import com.sinaev.mappers.UserMapper;
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
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
public class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpSession httpSession;

    private DateTimeFormatter dateFormatter;
    private User normalUser;
    private UserDTO adminUserDTO;
    private UserDTO normalUserDTO;
    private Room room1;
    private Room room2;


    @BeforeEach
    public void setUp() {
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");
        adminUserDTO = new UserDTO("admin", "adminpass", true);
        normalUserDTO = new UserDTO("user1", "password", false);
        normalUser = new User("user1", "password", false);
        room1 = new Room("Room1", RoomType.MEETING_ROOM);
        room2 = new Room("Room2", RoomType.MEETING_ROOM);

        lenient().when(httpRequest.getSession()).thenReturn(httpSession);
    }

    @Test
    @DisplayName("Should create booking if room is available")
    public void testCreateBooking() {
        BookingDTO bookingDTO = new BookingDTO("user1", "Room1", LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));
        Booking booking = new Booking(normalUser, room1, bookingDTO.startTime(), bookingDTO.endTime());


        when(bookingRepository.findByRoomName("Room1")).thenReturn(Collections.emptyList());
        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);
        when(userMapper.toEntity(normalUserDTO)).thenReturn(normalUser);
        when(bookingMapper.toEntity(bookingDTO)).thenReturn(booking);

        bookingService.createBooking(httpRequest, bookingDTO);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should not create booking if room is not available")
    public void testCreateBookingTimeNotAvailable(SoftAssertions softly) {
        BookingDTO bookingDTO = new BookingDTO("User", "Room1", LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));
        Booking existingBooking = new Booking(normalUser, room1, bookingDTO.startTime(), bookingDTO.endTime());

        when(bookingRepository.findByRoomName("Room1")).thenReturn(List.of(existingBooking));

        softly.assertThatThrownBy(() -> bookingService.createBooking(httpRequest, bookingDTO))
                .isInstanceOf(BookingIsNotAvailableException.class)
                .hasMessageContaining("Booking this room and time is not available");
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    @Test
    @DisplayName("Should update booking by creator")
    public void testUpdateBooking(SoftAssertions softly) {
        LocalDateTime originalStart = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        LocalDateTime newStart = LocalDateTime.parse("2024-06-20T12", dateFormatter);
        LocalDateTime newEnd = LocalDateTime.parse("2024-06-20T13", dateFormatter);
        UpdateBookingRequest request = new UpdateBookingRequest("Room1", originalStart, "Room2", newStart, newEnd);

        Booking booking = new Booking(normalUser, room1, originalStart, LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);
        when(bookingRepository.findByRoomAndTime("Room1", originalStart)).thenReturn(Optional.of(booking));
        when(userMapper.toEntity(normalUserDTO)).thenReturn(normalUser);

        bookingService.updateBooking(httpRequest, request);

        ArgumentCaptor<Booking> oldBookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<Booking> newBookingCaptor = ArgumentCaptor.forClass(Booking.class);

        verify(bookingRepository).update(oldBookingCaptor.capture(), newBookingCaptor.capture());

        Booking capturedOldBooking = oldBookingCaptor.getValue();
        Booking capturedNewBooking = newBookingCaptor.getValue();

        softly.assertThat(capturedOldBooking).isEqualTo(booking);
        softly.assertThat(capturedNewBooking.getStartTime()).isEqualTo(newStart);
        softly.assertThat(capturedNewBooking.getEndTime()).isEqualTo(newEnd);
    }

    @Test
    @DisplayName("Should update booking by admin")
    public void testUpdateBookingAsAdmin(SoftAssertions softly) {
        LocalDateTime originalStart = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        LocalDateTime newStart = LocalDateTime.parse("2024-06-20T12", dateFormatter);
        LocalDateTime newEnd = LocalDateTime.parse("2024-06-20T13", dateFormatter);
        UpdateBookingRequest request = new UpdateBookingRequest("Room1", originalStart, "Room2", newStart, newEnd);

        Booking booking = new Booking(normalUser, room1, originalStart, LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        when(bookingRepository.findByRoomAndTime("Room1", originalStart)).thenReturn(Optional.of(booking));
        when(userMapper.toEntity(adminUserDTO)).thenReturn(new User("admin", "adminpass", true));

        bookingService.updateBooking(httpRequest, request);

        ArgumentCaptor<Booking> oldBookingCaptor = ArgumentCaptor.forClass(Booking.class);
        ArgumentCaptor<Booking> newBookingCaptor = ArgumentCaptor.forClass(Booking.class);

        verify(bookingRepository).update(oldBookingCaptor.capture(), newBookingCaptor.capture());

        Booking capturedOldBooking = oldBookingCaptor.getValue();
        Booking capturedNewBooking = newBookingCaptor.getValue();

        softly.assertThat(capturedOldBooking).isEqualTo(booking);
        softly.assertThat(capturedNewBooking.getStartTime()).isEqualTo(newStart);
        softly.assertThat(capturedNewBooking.getEndTime()).isEqualTo(newEnd);
    }


    @Test
    @DisplayName("Should delete booking by admin")
    public void testDeleteBookingAsAdmin() {
        LocalDateTime start = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        RemoveBookingRequest request = new RemoveBookingRequest("Room1", start);

        Booking existingBooking = new Booking(normalUser, room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));
        User adminUser = new User("admin", "adminpass", true);

        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        when(bookingRepository.findByRoomAndTime("Room1", start)).thenReturn(Optional.of(existingBooking));
        when(userMapper.toEntity(adminUserDTO)).thenReturn(adminUser);

        bookingService.deleteBooking(httpRequest, request);

        verify(bookingRepository).delete(existingBooking);
    }

    @Test
    @DisplayName("Should delete booking by creator")
    public void testDeleteBookingByCreator() {
        LocalDateTime start = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        RemoveBookingRequest request = new RemoveBookingRequest("Room1", start);

        Booking existingBooking = new Booking(normalUser, room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);
        when(bookingRepository.findByRoomAndTime("Room1", start)).thenReturn(Optional.of(existingBooking));
        when(userMapper.toEntity(normalUserDTO)).thenReturn(normalUser);

        bookingService.deleteBooking(httpRequest, request);

        verify(bookingRepository).delete(existingBooking);
    }

    @Test
    @DisplayName("Should not delete booking by unauthorized user")
    public void testDeleteBookingUnauthorizedUser(SoftAssertions softly) {
        LocalDateTime start = LocalDateTime.parse("2024-06-20T10", dateFormatter);
        RemoveBookingRequest request = new RemoveBookingRequest("Room1", start);
        User unauthorizedUser = new User("user2", "password2", false);
        UserDTO unauthorizedUserDTO = new UserDTO("user2", "password2", false);


        Booking existingBooking = new Booking(normalUser, room1, start, LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(httpSession.getAttribute("loggedIn")).thenReturn(new UserDTO("user2", "password2", false));
        when(bookingRepository.findByRoomAndTime("Room1", start)).thenReturn(Optional.of(existingBooking));
        when(userMapper.toEntity(unauthorizedUserDTO)).thenReturn(unauthorizedUser);

        softly.assertThatThrownBy(() -> bookingService.deleteBooking(httpRequest, request))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Denied. Must be the creator of the booking or have admin access");
        verify(bookingRepository, never()).delete(existingBooking);
    }

    @Test
    @DisplayName("Should filter bookings by date")
    public void testFilterBookingsByDate(SoftAssertions softly) {
        LocalDate filterDate = LocalDate.parse("2024-06-20", DateTimeFormatter.ISO_DATE);
        FilterBookingsRequest request = new FilterBookingsRequest(filterDate, null, null);
        Booking booking = new Booking(new User("user1", "password"), room1, LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDTO> filteredBookings = bookingService.filterBookings(request);

        softly.assertThat(filteredBookings).hasSize(1);
    }

    @Test
    @DisplayName("Should filter bookings by user")
    public void testFilterBookingsByUser(SoftAssertions softly) {
        FilterBookingsRequest request = new FilterBookingsRequest(null, "user1", null);
        Booking booking = new Booking(new User("user1", "password"), room1, LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(new User("user1", "password")));
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDTO> filteredBookings = bookingService.filterBookings(request);

        softly.assertThat(filteredBookings).hasSize(1);
    }

    @Test
    @DisplayName("Should filter bookings by room")
    public void testFilterBookingsByRoom(SoftAssertions softly) {
        FilterBookingsRequest request = new FilterBookingsRequest(null, null, "Room1");
        Booking booking = new Booking(new User("user1", "password"), room1, LocalDateTime.parse("2024-06-20T10", dateFormatter), LocalDateTime.parse("2024-06-20T11", dateFormatter));

        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room1));
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDTO> filteredBookings = bookingService.filterBookings(request);

        softly.assertThat(filteredBookings).hasSize(1);
    }
}