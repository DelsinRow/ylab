package com.sinaev.services;

import com.sinaev.annotations.Loggable;
import com.sinaev.mappers.RoomMapper;
import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.Room;
import com.sinaev.models.enums.RoomType;
import com.sinaev.repositories.RoomRepository;

import java.util.List;
import java.util.Optional;

/**
 * Room service class that manages room creation, retrieval, updating, and deletion.
 */
@Loggable
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
     * @param userDTO the DTO of user attempting to create the room
     * @param roomDTO the DTO of room to be created
     */
    public Optional<RoomDTO> createRoom(UserDTO userDTO, RoomDTO roomDTO) {

        if (!userIsAdmin(userDTO)) {
            System.out.println("You do not have admin user access");
            return Optional.empty();
        }
        if (roomRepository.exists(roomDTO.name())) {
            System.out.println("Room with name '" + roomDTO.name() + "' already exists");
            return Optional.empty();

        } else {
            Room room = RoomMapper.INSTANCE.toEntity(roomDTO);
            roomRepository.save(room);
            return Optional.of(roomDTO);
        }


    }

    /**
     * Returns the list of rooms.
     *
     * @return the list of rooms
     */
    public Optional<List<RoomDTO>> getRooms() {
        List<RoomDTO> rooms = roomRepository.findAll().stream().map(RoomMapper.INSTANCE::toDTO).toList();
        return rooms.isEmpty() ? Optional.empty() : Optional.of(rooms);
    }

    /**
     * Updates an existing room if the user is an admin and the room exists.
     *
     * @param userDTO          the DTO of user attempting to update the room
     * @param originalRoomName the name of the room to be updated
     * @param newRoomName      the name of new room
     * @param newRoomType      the name of new room's type
     */
    public boolean updateRoom(UserDTO userDTO, String originalRoomName, String newRoomName, String newRoomType) {
        if (!userIsAdmin(userDTO)) {
            System.out.println("You do not have admin user access");
            return false;
        }
        Optional<Room> optionalRoom = roomRepository.findByName(originalRoomName);
        if (optionalRoom.isEmpty()) {
            System.out.println("Room not founded");
            return false;
        } else {
            RoomType roomType = RoomType.valueOf(newRoomType);
            Room oldRoom = optionalRoom.get();
            Room newRoom = new Room(newRoomName, roomType);
            roomRepository.update(oldRoom, newRoom);
            return true;
        }
    }

    /**
     * Deletes a room if the user is an admin and the room exists.
     *
     * @param userDTO  the DTO of  user attempting to delete the room
     * @param roomName the name of the room to be deleted
     */
    public boolean deleteRoom(UserDTO userDTO, String roomName) {
        if (!userIsAdmin(userDTO)) {
            System.out.println("You do not have admin user access");
            return false;
        }

        Optional<Room> optionalRoom = roomRepository.findByName(roomName);
        if (optionalRoom.isEmpty()) {
            System.out.println("Room " + roomName + " not found");
            return false;
        }

        Room room = optionalRoom.get();
        roomRepository.delete(room);
        return true;
    }

    private boolean userIsAdmin(UserDTO userDTO) {
        return userDTO.admin();
    }

}