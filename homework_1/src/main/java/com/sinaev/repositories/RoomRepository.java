package com.sinaev.repositories;

import com.sinaev.models.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for managing rooms.
 */
public class RoomRepository {
    private final List<Room> rooms = new ArrayList<>();

    /**
     * Finds all rooms in the repository.
     *
     * @return a list of all rooms
     */
    public List<Room> findAll() {
        return new ArrayList<>(rooms);
    }

    /**
     * Finds a room by its name.
     *
     * @param roomName the name of the room to search for
     * @return an Optional containing the found room, or an empty Optional if no room was found
     */
    public Optional<Room> findByName(String roomName) {
        return rooms.stream()
                .filter(room -> room.getName().equals(roomName))
                .findFirst();
    }
    /**
     * Saves a new room to the repository.
     *
     * @param room the room to save
     */
    public void save(Room room) {
        rooms.add(room);
    }

    /**
     * Deletes a room from the repository.
     *
     * @param room the room to delete
     */
    public void delete(Room room) {
        rooms.remove(room);
    }

    /**
     * Checks if a room already exists in the repository.
     *
     * @param room the room to check
     * @return true if the room exists, false otherwise
     */
    public boolean exists(Room room) {
        return rooms.stream()
                .anyMatch(existingRoom -> existingRoom.getName().equals(room.getName()));
    }
}