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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Room service class that manages room creation, retrieval, updating, and deletion.
 */
@Service
@Loggable
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }


    public void createRoom(HttpServletRequest req, RoomDTO roomDTO) {
        UserDTO userDTO = getUserDTOFromSession(req);
        if (!userIsAdmin(userDTO)) {
            throw new SecurityException("You do not have admin user access");
        }
        if (roomRepository.exists(roomDTO.name())) {
            throw new ObjectAlreadyExistsException("Room with name '" + roomDTO.name() + "' already exists");

        } else {
            Room room = RoomMapper.INSTANCE.toEntity(roomDTO);
            roomRepository.save(room);
        }


    }

    /**
     * Returns the list of rooms.
     *
     * @return the list of rooms
     */
    public List<RoomDTO> getRooms() {
        List<RoomDTO> rooms = roomRepository.findAll().stream().map(RoomMapper.INSTANCE::toDTO).toList();
        return rooms;
    }


    public void updateRoom(HttpServletRequest req, UpdateRoomRequest updateRoomRequest) {
        UserDTO userDTO = getUserDTOFromSession(req);
        if (!userIsAdmin(userDTO)) {
            throw new SecurityException("You do not have admin user access");
        }

        String originalRoomName = updateRoomRequest.originalRoomName();
        Optional<Room> optionalRoom = roomRepository.findByName(originalRoomName);
        if (optionalRoom.isEmpty()) {
            throw new NoSuchElementException("Room not found");
        } else {
            String newRoomName = updateRoomRequest.newRoomName();
            RoomType roomType = RoomType.valueOf(updateRoomRequest.newRoomType());
            Room oldRoom = optionalRoom.get();
            Room newRoom = new Room(newRoomName, roomType);
            roomRepository.update(oldRoom, newRoom);
        }
    }


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

    private boolean userIsAdmin(UserDTO userDTO) {
        return userDTO.admin();
    }

    private UserDTO getUserDTOFromSession(HttpServletRequest httpRequest) {
        UserDTO userDTO = (UserDTO) httpRequest.getSession().getAttribute("loggedIn");
        if (userDTO == null) {
            throw new NoSuchElementException("Log in first");
        } else {
            return userDTO;
        }
    }

}