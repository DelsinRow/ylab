package com.sinaev.controllers;

import com.sinaev.exceptions.BookingIsNotAvailableException;
import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.entities.Booking;
import com.sinaev.models.requests.booking.FilterBookingsRequest;
import com.sinaev.models.requests.booking.GetAvailableHoursRequest;
import com.sinaev.models.requests.booking.RemoveBookingRequest;
import com.sinaev.models.requests.booking.UpdateBookingRequest;
import com.sinaev.services.BookingService;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private BookingController bookingController;

    private SoftAssertions softly;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        softly = new SoftAssertions();
    }

    @Test
    @DisplayName("Test successful booking creation")

    public void testCreateBookingSuccess() {
        BookingDTO bookingDTO = new BookingDTO("username", "roomName", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        doNothing().when(bookingService).createBooking(httpRequest, bookingDTO);

        ResponseEntity<?> response = bookingController.create(httpRequest, bookingDTO);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        softly.assertThat(response.getBody()).isEqualTo("Booking created");
        verify(bookingService, times(1)).createBooking(httpRequest, bookingDTO);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test booking creation with exception")
    public void testCreateBookingException() {
        BookingDTO bookingDTO = new BookingDTO("username", "roomName", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        doThrow(new BookingIsNotAvailableException("Booking is not available")).when(bookingService).createBooking(httpRequest, bookingDTO);

        ResponseEntity<?> response = bookingController.create(httpRequest, bookingDTO);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(400);
        softly.assertThat(response.getBody()).isEqualTo("Booking is not available");
        verify(bookingService, times(1)).createBooking(httpRequest, bookingDTO);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test get available hours")
    public void testGetAvailableHours() {
        GetAvailableHoursRequest request = new GetAvailableHoursRequest(LocalDate.now(), "roomName");
        List<LocalTime> hours = Arrays.asList(LocalTime.of(9, 0), LocalTime.of(10, 0));
        when(bookingService.getAvailableHours(request)).thenReturn(hours);

        ResponseEntity<?> response = bookingController.getAvailableHours(request);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        softly.assertThat(response.getBody()).isEqualTo(hours);
        verify(bookingService, times(1)).getAvailableHours(request);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test get available hours with exception")
    public void testGetAvailableHoursException() {
        GetAvailableHoursRequest request = new GetAvailableHoursRequest(LocalDate.now(), "roomName");
        doThrow(new NoSuchElementException("No available hours")).when(bookingService).getAvailableHours(request);

        ResponseEntity<?> response = bookingController.getAvailableHours(request);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(400);
        softly.assertThat(response.getBody()).isEqualTo("No available hours");
        verify(bookingService, times(1)).getAvailableHours(request);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test successful booking update")
    public void testUpdateBookingSuccess() {
        UpdateBookingRequest request = new UpdateBookingRequest(
                "OriginalRoom",
                LocalDateTime.of(2023, 7, 1, 10, 0),
                "NewRoom",
                LocalDateTime.of(2023, 7, 1, 11, 0),
                LocalDateTime.of(2023, 7, 1, 12, 0)
        );
        doNothing().when(bookingService).updateBooking(httpRequest, request);

        ResponseEntity<?> response = bookingController.update(httpRequest, request);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        softly.assertThat(response.getBody()).isEqualTo("Booking updated");
        verify(bookingService, times(1)).updateBooking(httpRequest, request);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test booking update with exception")
    public void testUpdateBookingException() {
        UpdateBookingRequest request = new UpdateBookingRequest(
                "OriginalRoom",
                LocalDateTime.of(2023, 7, 1, 10, 0),
                "NewRoom",
                LocalDateTime.of(2023, 7, 1, 11, 0),
                LocalDateTime.of(2023, 7, 1, 12, 0)
        );
        doThrow(new NoSuchElementException("Booking not found")).when(bookingService).updateBooking(httpRequest, request);

        ResponseEntity<?> response = bookingController.update(httpRequest, request);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(400);
        softly.assertThat(response.getBody()).isEqualTo("Booking not found");
        verify(bookingService, times(1)).updateBooking(httpRequest, request);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test successful booking deletion")
    public void testDeleteBookingSuccess() {
        RemoveBookingRequest request = new RemoveBookingRequest("roomName", LocalDateTime.now());
        doNothing().when(bookingService).deleteBooking(httpRequest, request);

        ResponseEntity<?> response = bookingController.delete(httpRequest, request);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        softly.assertThat(response.getBody()).isEqualTo("Booking deleted");
        verify(bookingService, times(1)).deleteBooking(httpRequest, request);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test booking deletion with exception")
    public void testDeleteBookingException() {
        RemoveBookingRequest request = new RemoveBookingRequest("roomName", LocalDateTime.now());
        doThrow(new BookingIsNotAvailableException("Booking not available")).when(bookingService).deleteBooking(httpRequest, request);

        ResponseEntity<?> response = bookingController.delete(httpRequest, request);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(400);
        softly.assertThat(response.getBody()).isEqualTo("Booking not available");
        verify(bookingService, times(1)).deleteBooking(httpRequest, request);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test filter bookings")
    public void testFilterBookings() {
        FilterBookingsRequest request = new FilterBookingsRequest(LocalDate.now(), "username", "roomName");
        BookingDTO booking1 = new BookingDTO("username1", "roomName1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        BookingDTO booking2 = new BookingDTO("username2", "roomName2", LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        List<BookingDTO> bookings = Arrays.asList(booking1, booking2);
        when(bookingService.filterBookings(request)).thenReturn(bookings);

        ResponseEntity<?> response = bookingController.filter(request);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        softly.assertThat(response.getBody()).isEqualTo(bookings);
        verify(bookingService, times(1)).filterBookings(request);
        softly.assertAll();
    }
}