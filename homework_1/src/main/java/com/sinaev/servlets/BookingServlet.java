package com.sinaev.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sinaev.annotations.Loggable;
import com.sinaev.configs.AppConfig;
import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.requests.booking.FilterBookingsRequest;
import com.sinaev.models.requests.booking.GetAvailableHoursRequest;
import com.sinaev.models.requests.booking.UpdateBookingRequest;
import com.sinaev.repositories.BookingRepository;
import com.sinaev.services.BookingService;
import com.sinaev.validators.DTOValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Setter
@Loggable
@WebServlet("/booking/*")
public class BookingServlet extends HttpServlet {
    private AppConfig config;
    private BookingService bookingService;
    private DTOValidator validator;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.config = new AppConfig();
        this.bookingService = new BookingService(new BookingRepository(
                config.getDbUrl(),
                config.getDbUsername(),
                config.getDbPassword()

        ));
        this.validator = new DTOValidator();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            BookingDTO bookingDTO = objectMapper.readValue(req.getInputStream(), BookingDTO.class);
            validator.validate(bookingDTO);

            UserDTO loggedUser = getUserFromSession(req);
            if (isLogIn(loggedUser)) {
                Optional<BookingDTO> createdBookingOpt = bookingService.createBooking(loggedUser, bookingDTO);
                if (createdBookingOpt.isPresent()) {
                    BookingDTO createdBooking = createdBookingOpt.get();
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    String jsonResponse = objectMapper.writeValueAsString(createdBooking);
                    resp.getWriter().write(jsonResponse);
                } else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "This time is not available for bookings. Try another time."));
                    resp.getWriter().write(errorResponse);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "Requires logging in first"));
                resp.getWriter().write(errorResponse);
            }
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        switch (path) {
            case "/available-hours" -> handleGetAvailableHours(req, resp);
            case "/filter" -> handleFilterBookings(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            UpdateBookingRequest sendedRequest = objectMapper.readValue(req.getInputStream(), UpdateBookingRequest.class);

            UserDTO loggedUser = getUserFromSession(req);
            if (isLogIn(loggedUser)) {
                String originalRoomName = sendedRequest.originalRoomName();
                LocalDateTime originalStartTime = sendedRequest.originalStartTime();
                String newRoomName = sendedRequest.newRoomName();
                LocalDateTime newStarTime = sendedRequest.newStarTime();
                LocalDateTime newEndTime = sendedRequest.newEndTime();

                boolean isBookingUpdated = bookingService.updateBooking(loggedUser, originalRoomName, originalStartTime,
                        newRoomName, newStarTime, newEndTime);
                if (isBookingUpdated) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Booking updated");
                } else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The operation was not completed. Please, check logs"));
                    resp.getWriter().write(errorResponse);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "Requires logging in first"));
                resp.getWriter().write(errorResponse);
            }
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            BookingDTO bookingDTO = objectMapper.readValue(req.getInputStream(), BookingDTO.class);
            validator.validate(bookingDTO);

            UserDTO loggedUser = getUserFromSession(req);
            if (isLogIn(loggedUser)) {
                String roomName = bookingDTO.roomName();
                LocalDateTime startTime = bookingDTO.startTime();

                boolean isBookingDeleted = bookingService.deleteBooking(loggedUser, roomName, startTime);
                if (isBookingDeleted) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Booking deleted");
                } else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The operation was not completed. Please, check logs"));
                    resp.getWriter().write(errorResponse);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "Requires logging in first"));
                resp.getWriter().write(errorResponse);
            }
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        }
    }

    private void handleGetAvailableHours(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            GetAvailableHoursRequest sendedRequest = objectMapper.readValue(req.getInputStream(), GetAvailableHoursRequest.class);
            LocalDate date = sendedRequest.date();
            String roomName = sendedRequest.roomName();

            Optional<List<LocalTime>> optionalAvailableHours = bookingService.getAvailableHours(date, roomName);

            if (optionalAvailableHours.isPresent()) {
                List<LocalTime> availableHours = optionalAvailableHours.get();
                resp.setStatus(HttpServletResponse.SC_OK);
                String jsonResponse = objectMapper.writeValueAsString(availableHours);
                resp.getWriter().write(jsonResponse);
            } else {
                String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "No available hours found for the given date and room."));
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(errorResponse);
            }
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        }
    }

    private void handleFilterBookings(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            FilterBookingsRequest sendedRequest = objectMapper.readValue(req.getInputStream(), FilterBookingsRequest.class);
            LocalDate date = sendedRequest.date();
            String username = sendedRequest.username();
            String roomName = sendedRequest.roomName();

            Optional<List<BookingDTO>> optionalFilterBookings = bookingService.filterBookings(date, username, roomName);
            if (optionalFilterBookings.isPresent()) {
                List<BookingDTO> filterResult = optionalFilterBookings.get();
                resp.setStatus(HttpServletResponse.SC_OK);
                String jsonResponse = objectMapper.writeValueAsString(filterResult);
                resp.getWriter().write(jsonResponse);
            } else {
                String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "No results were found for your selected filter parameters."));
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(errorResponse);
            }
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        }


    }

    private UserDTO getUserFromSession(HttpServletRequest req) {
        return (UserDTO) req.getSession().getAttribute("loggedIn");
    }

    private boolean isLogIn(UserDTO userDTO) {
        return userDTO != null;
    }


}
