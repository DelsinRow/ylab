package com.sinaev.models.entities;

import com.sinaev.models.enums.RoomType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a room for booking in the system.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"name", "type"})
public class Room {
    /**
     * The id of the room.
     */
    private Long id;
    /**
     * The name of the room.
     */
    private String name;

    /**
     * The type of the room.
     */
    private RoomType type;

    public Room(String name, RoomType type) {
        this.name = name;
        this.type = type;
    }
}