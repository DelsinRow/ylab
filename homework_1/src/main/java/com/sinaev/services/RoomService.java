package com.sinaev.services;

import com.sinaev.models.Room;
import com.sinaev.models.User;
import com.sinaev.repositories.RoomRepository;

import java.util.List;
import java.util.Optional;

/**
 * Room service class that manages room creation, retrieval, updating, and deletion.
 */
public class RoomService {
    private final RoomRepository roomRepository;

    /**
     * Constructs a RoomService with the specified repository.
     *
     * @param roomRepository the repository for managing rooms
     */
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Creates a new room if the user is an admin and the room does not already exist.
     *
     * @param user the user attempting to create the room
     * @param room the room to be created
     */
    public void createRoom(User user, Room room) {
        if (user.isAdmin()) {
            if (!roomRepository.exists(room)) {
                roomRepository.save(room);
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
        return roomRepository.findAll();
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
            Optional<Room> optionalRoom = roomRepository.findByName(roomName);
            if (optionalRoom.isPresent()) {
                Room room = optionalRoom.get();
                roomRepository.delete(room);
                roomRepository.save(updatedRoom);
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
            Optional<Room> optionalRoom = roomRepository.findByName(roomName);
            if (optionalRoom.isPresent()) {
                Room room = optionalRoom.get();
                roomRepository.delete(room);
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
        return roomRepository.findByName(resourceName);
    }
}