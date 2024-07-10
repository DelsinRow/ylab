package com.sinaev.models.requests.room;

public record UpdateRoomRequest(
        String originalRoomName,
        String newRoomName,
        String newRoomType) {
}
