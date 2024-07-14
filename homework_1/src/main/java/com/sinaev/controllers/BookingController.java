package com.sinaev.controllers;

import com.sinaev.exceptions.BookingIsNotAvailableException;
import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.entities.Booking;
import com.sinaev.models.requests.booking.FilterBookingsRequest;
import com.sinaev.models.requests.booking.GetAvailableHoursRequest;
import com.sinaev.models.requests.booking.RemoveBookingRequest;
import com.sinaev.models.requests.booking.UpdateBookingRequest;
import com.sinaev.services.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

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

    @GetMapping("/available-hours")
    ResponseEntity<?> getAvailableHours(@RequestBody GetAvailableHoursRequest request) {
        try {
            List<LocalTime> hours = bookingService.getAvailableHours(request);
            return ResponseEntity.ok(hours);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    ResponseEntity<?> update (HttpServletRequest httpRequest,
                              @RequestBody UpdateBookingRequest request) {
        try {
            bookingService.updateBooking(httpRequest,request);
            return ResponseEntity.ok().body("Booking updated");
        } catch (NoSuchElementException | SecurityException | BookingIsNotAvailableException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    ResponseEntity<?> delete (HttpServletRequest httpRequest,
                              @RequestBody RemoveBookingRequest request){
        try {
            bookingService.deleteBooking(httpRequest, request);
            return ResponseEntity.ok().body("Booking deleted");
        } catch (BookingIsNotAvailableException | SecurityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter")
    ResponseEntity<?> filter (@RequestBody FilterBookingsRequest request) {
        List<BookingDTO> bookings = bookingService.filterBookings(request);
        return ResponseEntity.ok(bookings);
    }@GetMapping("/all")
    ResponseEntity<?> filter () {
        List<Booking> bookings = bookingService.findAll();
        return ResponseEntity.ok(bookings);
    }


}

