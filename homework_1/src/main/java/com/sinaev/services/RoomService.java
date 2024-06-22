package com.sinaev.services;

import com.sinaev.models.Room;
import com.sinaev.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Room service class that manages room creation, retrieval, updating, and deletion.
 */
public class RoomService {
    private final List<Room> rooms;

    /**
     * Constructs a RoomService with the specified list of rooms.
     *
     * @param rooms the list of rooms to manage
     */
    public RoomService(List<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * Creates a new room if the user is an admin and the room does not already exist.
     *
     * @param user the user attempting to create the room
     * @param room the room to be created
     */
    public void createRoom(User user, Room room) {
        if (user.isAdmin()) {
            if (!roomIsExist(room)) {
                rooms.add(room);
                System.out.println("Room: '" + room.getName() + "' successfully created");
            } else {
                System.out.println("Room with name '" + room.getName() + "' already exists");
            }
        } else {
            System.out.println("You do not have admin user access");
        }
    }

    /**
     * Returns the list of rooms.
     *
     * @return the list of rooms
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * Updates an existing room if the user is an admin and the room exists.
     *
     * @param user        the user attempting to update the room
     * @param roomName    the name of the room to be updated
     * @param updatedRoom the updated room details
     */
    public void updateRoom(User user, String roomName, Room updatedRoom) {
        if (user.isAdmin()) {
            Optional<Room> optionalRoom = getRoomByName(roomName);
            if (optionalRoom.isPresent()) {
                Room room = optionalRoom.get();
                rooms.set(rooms.indexOf(room), updatedRoom);
                System.out.println("Room '" + roomName + "' successfully updated");
            } else {
                System.out.println("Room not found");
            }
        } else {
            System.out.println("You do not have admin user access");
        }
    }

    /**
     * Deletes a room if the user is an admin and the room exists.
     *
     * @param user     the user attempting to delete the room
     * @param roomName the name of the room to be deleted
     */
    public void deleteRoom(User user, String roomName) {
        if (user.isAdmin()) {
            Optional<Room> optionalRoom = getRoomByName(roomName);
            if (optionalRoom.isPresent()) {
                Room room = optionalRoom.get();
                rooms.remove(room);
                System.out.println("Room '" + roomName + "' successfully removed");
            } else {
                System.out.println("Room not found");
            }
        } else {
            System.out.println("You do not have admin user access");
        }
    }

    /**
     * Finds a room by its name.
     *
     * @param resourceName the name of the room to find
     * @return an Optional containing the found room, or an empty Optional if no room was found
     */
    public Optional<Room> getRoomByName(String resourceName) {
        return rooms.stream()
                .filter(room -> room.getName().equals(resourceName))
                .findFirst();
    }

    /**
     * Checks if a room already exists in the list.
     *
     * @param room the room to check for existence
     * @return true if the room exists, false otherwise
     */
    private boolean roomIsExist(Room room) {
        return rooms.stream()
                .anyMatch(u -> u.getName().equals(room.getName()));
    }
}
