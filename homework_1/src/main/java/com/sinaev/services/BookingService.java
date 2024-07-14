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
public interface BookingService {
    void createBooking(HttpServletRequest httpRequest, BookingDTO bookingDTO);

    List<LocalTime> getAvailableHours(GetAvailableHoursRequest request);

    void updateBooking(HttpServletRequest httpRequest, UpdateBookingRequest request);

    void deleteBooking(HttpServletRequest httpRequest, RemoveBookingRequest request);

    List<BookingDTO> filterBookings(FilterBookingsRequest request);
    List<Booking> findAll();

}
