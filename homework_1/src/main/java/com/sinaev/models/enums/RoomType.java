package com.sinaev.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the type of a rooms.
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
