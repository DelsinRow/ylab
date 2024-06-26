package com.sinaev.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a room for booking in the system.
 */
@Getter
@Setter
@AllArgsConstructor
public class Room {
    /**
     * The name of the room.
     */
    private String name;

    /**
     * The type of the room.
     */
    private RoomType type;
}