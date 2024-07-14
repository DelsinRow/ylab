package com.sinaev.models.requests.booking;

import java.time.LocalDateTime;

public record RemoveBookingRequest (String roomName, LocalDateTime startTime){
}
