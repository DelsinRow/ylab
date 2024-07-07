package com.sinaev.models.requests.booking;

import java.time.LocalDate;

public record FilterBookingsRequest(
        LocalDate date,
        String username,
        String roomName) {
}
