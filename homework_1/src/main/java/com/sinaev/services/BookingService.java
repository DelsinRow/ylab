package com.sinaev.services;

import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.entities.Booking;
import com.sinaev.models.requests.booking.FilterBookingsRequest;
import com.sinaev.models.requests.booking.GetAvailableHoursRequest;
import com.sinaev.models.requests.booking.RemoveBookingRequest;
import com.sinaev.models.requests.booking.UpdateBookingRequest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

/**
 * Service interface for managing bookings.
 * <p>
 * This interface defines methods for creating, updating, deleting, and filtering bookings,
 * as well as retrieving available booking hours.
 * </p>
 */
public interface BookingService {
    /**
     * Creates a new booking.
     *
     * @param httpRequest the HTTP request containing user session information
     * @param bookingDTO  the booking data transfer object containing booking details
     */
    void createBooking(HttpServletRequest httpRequest, BookingDTO bookingDTO);

    /**
     * Retrieves available hours for booking a room on a specific date.
     *
     * @param request the request containing the date and room name
     * @return a list of available hours
     */
    List<LocalTime> getAvailableHours(GetAvailableHoursRequest request);

    /**
     * Updates an existing booking.
     *
     * @param httpRequest the HTTP request containing user session information
     * @param request     the request containing the original and new booking details
     */
    void updateBooking(HttpServletRequest httpRequest, UpdateBookingRequest request);

    /**
     * Deletes an existing booking.
     *
     * @param httpRequest the HTTP request containing user session information
     * @param request     the request containing the booking details to be deleted
     */
    void deleteBooking(HttpServletRequest httpRequest, RemoveBookingRequest request);

    /**
     * Filters bookings based on the specified criteria.
     *
     * @param request the request containing the filtering criteria
     * @return a list of filtered bookings
     */
    List<BookingDTO> filterBookings(FilterBookingsRequest request);

}
