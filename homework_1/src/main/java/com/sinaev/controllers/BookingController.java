package com.sinaev.controllers;

import com.sinaev.exceptions.BookingIsNotAvailableException;
import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.requests.booking.FilterBookingsRequest;
import com.sinaev.models.requests.booking.GetAvailableHoursRequest;
import com.sinaev.models.requests.booking.RemoveBookingRequest;
import com.sinaev.models.requests.booking.UpdateBookingRequest;
import com.sinaev.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * BookingController handles requests related to booking management.
 * It provides endpoints for creating, retrieving, updating, and deleting bookings.
 */
@RestController
@RequestMapping("api/v1/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    /**
     * This method processes the creation request. If successful, it returns
     * a message indicating the booking was created.
     *
     * @param httpRequest the HTTP request containing session details
     * @param bookingDTO  the booking data transfer object containing booking information
     * @return a response entity indicating the result of the creation operation
     */
    @PostMapping
    ResponseEntity<?> create(HttpServletRequest httpRequest,
                             @RequestBody BookingDTO bookingDTO) {
        try {
            bookingService.createBooking(httpRequest, bookingDTO);
            return ResponseEntity.ok().body("Booking created");
        } catch (BookingIsNotAvailableException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * This method returns a list of available booking hours based on the request.
     *
     * @param request the request object containing criteria for available hours
     * @return a response entity containing the list of available hours
     */
    @GetMapping("/available-hours")
    ResponseEntity<?> getAvailableHours(@RequestBody GetAvailableHoursRequest request) {
        try {
            List<LocalTime> hours = bookingService.getAvailableHours(request);
            return ResponseEntity.ok(hours);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * This method processes the update request. If successful, it returns
     * a message indicating the booking was updated.
     *
     * @param httpRequest the HTTP request containing session details
     * @param request     the request object containing updated booking information
     * @return a response entity indicating the result of the update operation
     */
    @PutMapping
    ResponseEntity<?> update(HttpServletRequest httpRequest,
                             @RequestBody UpdateBookingRequest request) {
        try {
            bookingService.updateBooking(httpRequest, request);
            return ResponseEntity.ok().body("Booking updated");
        } catch (NoSuchElementException | SecurityException | BookingIsNotAvailableException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * This method processes the delete request. If successful, it returns
     * a message indicating the booking was deleted.
     *
     * @param httpRequest the HTTP request containing session details
     * @param request     the request object containing booking removal information
     * @return a response entity indicating the result of the deletion operation
     */
    @DeleteMapping
    ResponseEntity<?> delete(HttpServletRequest httpRequest,
                             @RequestBody RemoveBookingRequest request) {
        try {
            bookingService.deleteBooking(httpRequest, request);
            return ResponseEntity.ok().body("Booking deleted");
        } catch (BookingIsNotAvailableException | SecurityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * This method returns a list of bookings that match the given filter criteria.
     *
     * @param request the request object containing filter criteria
     * @return a response entity containing the list of filtered bookings
     */
    @GetMapping("/filter")
    ResponseEntity<?> filter(@RequestBody FilterBookingsRequest request) {
        List<BookingDTO> bookings = bookingService.filterBookings(request);
        return ResponseEntity.ok(bookings);
    }

}

