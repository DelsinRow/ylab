package com.sinaev.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.requests.booking.GetAvailableHoursRequest;
import com.sinaev.models.requests.booking.UpdateBookingRequest;
import com.sinaev.services.BookingService;
import com.sinaev.validators.DTOValidator;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingServletTest {

    private BookingServlet bookingServlet;
    private BookingService mockBookingService;
    private DTOValidator mockValidator;
    private ObjectMapper objectMapper;

    /**
     * Set up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        bookingServlet = new BookingServlet();
        mockBookingService = mock(BookingService.class);
        mockValidator = mock(DTOValidator.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        bookingServlet.setBookingService(mockBookingService);
        bookingServlet.setValidator(mockValidator);
        bookingServlet.setObjectMapper(objectMapper);
    }

    @Test
    @DisplayName("Test doPost method - create booking successfully")
    public void testDoPostCreateBookingSuccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        BookingDTO bookingDTO = new BookingDTO("username", "room1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(bookingDTO);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getSession()).thenReturn(session);
        when(request.getInputStream()).thenReturn(inputStream);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(writer);
        when(mockBookingService.createBooking(any(UserDTO.class), any(BookingDTO.class))).thenReturn(Optional.of(bookingDTO));

        bookingServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedResponse = objectMapper.writeValueAsString(bookingDTO);
        assertEquals(expectedResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test doPost method - booking conflict")
    public void testDoPostCreateBookingConflict() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        BookingDTO bookingDTO = new BookingDTO("username", "room1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(bookingDTO);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getSession()).thenReturn(session);
        when(request.getInputStream()).thenReturn(inputStream);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(writer);
        when(mockBookingService.createBooking(any(UserDTO.class), any(BookingDTO.class))).thenReturn(Optional.empty());

        bookingServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedErrorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "This time is not available for bookings. Try another time."));
        assertEquals(expectedErrorResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test doGet method - available hours found")
    public void testDoGetAvailableHoursFound() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        GetAvailableHoursRequest getAvailableHoursRequest = new GetAvailableHoursRequest(LocalDate.now(), "room1");
        String json = objectMapper.writeValueAsString(getAvailableHoursRequest);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));
        List<LocalTime> availableHours = List.of(LocalTime.of(10, 0), LocalTime.of(11, 0));

        when(request.getPathInfo()).thenReturn("/available-hours");
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getSession()).thenReturn(mock(HttpSession.class));
        when(response.getWriter()).thenReturn(writer);
        when(mockBookingService.getAvailableHours(any(LocalDate.class), any(String.class))).thenReturn(Optional.of(availableHours));

        bookingServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedResponse = objectMapper.writeValueAsString(availableHours);
        assertEquals(expectedResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test doGet method - available hours not found")
    public void testDoGetAvailableHoursNotFound() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        GetAvailableHoursRequest getAvailableHoursRequest = new GetAvailableHoursRequest(LocalDate.now(), "room1");
        String json = objectMapper.writeValueAsString(getAvailableHoursRequest);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getPathInfo()).thenReturn("/available-hours");
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getSession()).thenReturn(mock(HttpSession.class));
        when(response.getWriter()).thenReturn(writer);
        when(mockBookingService.getAvailableHours(any(LocalDate.class), any(String.class))).thenReturn(Optional.empty());

        bookingServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedErrorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "No available hours found for the given date and room."));
        assertEquals(expectedErrorResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test doPut method - booking updated successfully")
    public void testDoPutBookingUpdateSuccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest("room1", LocalDateTime.now(), "newRoomName",
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(updateBookingRequest);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getSession()).thenReturn(session);
        when(request.getInputStream()).thenReturn(inputStream);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(writer);
        when(mockBookingService.updateBooking(any(UserDTO.class), eq("room1"), any(LocalDateTime.class), eq("newRoomName"),
                any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);

        bookingServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("Booking updated");
    }

    @Test
    @DisplayName("Test doPut method - booking update conflict")
    public void testDoPutBookingUpdateConflict() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest("room1", LocalDateTime.now(), "newRoomName",
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(updateBookingRequest);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getSession()).thenReturn(session);
        when(request.getInputStream()).thenReturn(inputStream);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(writer);
        when(mockBookingService.updateBooking(any(UserDTO.class), eq("room1"), any(LocalDateTime.class), eq("newRoomName"),
                any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
        bookingServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedErrorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The operation was not completed. Please, check logs"));
        assertEquals(expectedErrorResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test doDelete method - booking deleted successfully")
    public void testDoDeleteBookingSuccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UserDTO userDTO = new UserDTO("username", "password", false);
        BookingDTO bookingDTO = new BookingDTO("username", "room1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        String json = objectMapper.writeValueAsString(bookingDTO);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getSession()).thenReturn(session);
        when(request.getInputStream()).thenReturn(inputStream);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(writer);
        when(mockBookingService.deleteBooking(any(UserDTO.class), eq("room1"), any(LocalDateTime.class))).thenReturn(true);

        bookingServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("Booking deleted");
    }

    @Test
    @DisplayName("Test doDelete method - booking deletion conflict")
    public void testDoDeleteBookingConflict() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        BookingDTO bookingDTO = new BookingDTO("username", "room1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(bookingDTO);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getSession()).thenReturn(session);
        when(request.getInputStream()).thenReturn(inputStream);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(writer);
        when(mockBookingService.deleteBooking(any(UserDTO.class), eq("room1"), any(LocalDateTime.class))).thenReturn(false);

        bookingServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedErrorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The operation was not completed. Please, check logs"));
        assertEquals(expectedErrorResponse, argumentCaptor.getValue());
    }


    private static class MockServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public MockServletInputStream(ByteArrayInputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // No implementation needed
        }
    }
}