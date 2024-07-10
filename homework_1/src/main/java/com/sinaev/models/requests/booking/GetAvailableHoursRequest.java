package com.sinaev.models.requests.booking;

import java.time.LocalDate;

public record GetAvailableHoursRequest(
        LocalDate date,
        String roomName) {
}
