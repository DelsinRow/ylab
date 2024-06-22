package com.sinaev.models;

import java.time.LocalDateTime;

public class Booking {
    private User user;
    private Room room;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Booking(User user, Room room, LocalDateTime startTime, LocalDateTime endTime) {
        this.user = user;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Booking: [" +
                "user:" + user.getUsername() +
                ", room:" + room.getName() +
                ", startTime:" + startTime +
                ", endTime:" + endTime +
                ']';
    }
}
