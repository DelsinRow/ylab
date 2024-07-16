package com.sinaev.services.impl;

import com.sinaev.annotations.Loggable;
import com.sinaev.exceptions.ObjectAlreadyExistsException;
import com.sinaev.mappers.RoomMapper;
import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.Room;
import com.sinaev.models.enums.RoomType;
import com.sinaev.models.requests.room.UpdateRoomRequest;
import com.sinaev.repositories.RoomRepository;
import com.sinaev.services.RoomService;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Room service class that manages room creation, retrieval, updating, and deletion.
 */
@Service
@Loggable
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    /**
     * Creates a new room.
     *
     * @param req     the HTTP request containing user session information
     * @param roomDTO the room data transfer object containing room details
     */
    public void createRoom(HttpServletRequest req, RoomDTO roomDTO) {
        UserDTO userDTO = getUserDTOFromSession(req);
        if (!userIsAdmin(userDTO)) {
            throw new SecurityException("You do not have admin user access");
        }
        if (roomRepository.exists(roomDTO.name())) {
            throw new ObjectAlreadyExistsException("Room with name '" + roomDTO.name() + "' already exists");
        }
        Room room = roomMapper.toEntity(roomDTO);
        roomRepository.save(room);
    }

    /**
     * Returns the list of rooms.
     *
     * @return the list of rooms
     */
    public List<RoomDTO> getRooms() {
        List<RoomDTO> rooms = roomRepository.findAll().stream().map(roomMapper::toDTO).toList();
        return rooms;
    }

    /**
     * Updates an existing room.
     *
     * @param req               the HTTP request containing user session information
     * @param updateRoomRequest the request containing the original and new room details
     */
    public void updateRoom(HttpServletRequest req, UpdateRoomRequest updateRoomRequest) {
        UserDTO userDTO = getUserDTOFromSession(req);
        if (!userIsAdmin(userDTO)) {
            throw new SecurityException("You do not have admin user access");
        }

        String originalRoomName = updateRoomRequest.originalRoomName();
        Optional<Room> optionalRoom = roomRepository.findByName(originalRoomName);
        if (optionalRoom.isEmpty()) {
            throw new NoSuchElementException("Room not found");
        }
        String newRoomName = updateRoomRequest.newRoomName();
        RoomType roomType = RoomType.valueOf(updateRoomRequest.newRoomType());
        Room oldRoom = optionalRoom.get();
        Room newRoom = new Room(newRoomName, roomType);
        roomRepository.update(oldRoom, newRoom);
    }

    /**
     * Deletes an existing room.
     *
     * @param req      the HTTP request containing user session information
     * @param roomName the name of the room to be deleted
     */
    public void deleteRoom(HttpServletRequest req, String roomName) {
        UserDTO userDTO = getUserDTOFromSession(req);
        if (!userIsAdmin(userDTO)) {
            throw new SecurityException("You do not have admin user access");
        }
        Optional<Room> optionalRoom = roomRepository.findByName(roomName);
        if (optionalRoom.isEmpty()) {
            throw new NoSuchElementException("Room not found");
        }
        Room room = optionalRoom.get();
        roomRepository.delete(room);
    }

    /**
     * Checks if the user has admin privileges.
     *
     * @param userDTO the user data transfer object
     * @return true if the user is an admin, false otherwise
     */
    private boolean userIsAdmin(UserDTO userDTO) {
        return userDTO.admin();
    }

    /**
     * Retrieves the user DTO from the session.
     *
     * @param httpRequest the HTTP request containing the session
     * @return the user DTO
     * @throws NoSuchElementException if the user is not found in the session
     */
    private UserDTO getUserDTOFromSession(HttpServletRequest httpRequest) {
        UserDTO userDTO = (UserDTO) httpRequest.getSession().getAttribute("loggedIn");
        if (userDTO == null) {
            throw new NoSuchElementException("Log in first");
        } else {
            return userDTO;
        }
    }

}