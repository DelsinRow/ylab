package com.sinaev.models;

public enum RoomType {
    WORKSPACE("workspace"),
    MEETING_ROOM("meeting room");

    private final String type;

    RoomType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
