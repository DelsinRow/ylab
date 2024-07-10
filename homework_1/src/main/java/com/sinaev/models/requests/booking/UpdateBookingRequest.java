package com.sinaev.models.requests.booking;

import java.time.LocalDateTime;

public record UpdateBookingRequest(
        String originalRoomName,
        LocalDateTime originalStartTime,
        String newRoomName,
        LocalDateTime newStarTime,
        LocalDateTime newEndTime) {
}
