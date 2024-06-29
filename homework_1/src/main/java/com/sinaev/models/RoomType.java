package com.sinaev.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the type of a room.
 */
@Getter
@AllArgsConstructor
public enum RoomType {
    /**
     * A workspace type room.
     */
    WORKSPACE("WORKSPACE"),

    /**
     * A meeting room type room.
     */
    MEETING_ROOM("MEETING_ROOM");

    /**
     * The type of the room.
     */
    private final String type;

}
