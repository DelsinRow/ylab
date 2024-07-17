package com.sinaev.controllers;

import com.sinaev.exceptions.ObjectAlreadyExistsException;
import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.requests.room.UpdateRoomRequest;
import com.sinaev.services.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * RoomController handles requests related to room management.
 * It provides endpoints for creating, retrieving, updating, and deleting rooms.
 */
@RestController
@RequestMapping("api/v1/room")
public class RoomController {
    private final RoomService roomService;

    /**
     * Constructs a RoomController with the specified RoomService.
     *
     * @param roomService the service used to manage room-related operations
     */
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * This method processes the creation request. If successful, it returns
     * a message indicating the room was created.
     *
     * @param httpRequest the HTTP request containing session details
     * @param roomDTO     the room data transfer object containing room information
     * @return a response entity indicating the result of the creation operation
     */
    @PostMapping
    ResponseEntity<?> create(HttpServletRequest httpRequest,
                             @RequestBody RoomDTO roomDTO) {
        try {
            roomService.createRoom(httpRequest, roomDTO);
            return ResponseEntity.ok().body("Room created");
        } catch (SecurityException | ObjectAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * This method returns a list of all rooms.
     *
     * @return a response entity containing the list of all rooms
     */
    @GetMapping
    ResponseEntity<List<RoomDTO>> getAll() {
        List<RoomDTO> rooms = roomService.getRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * This method processes the update request. If successful, it returns
     * a message indicating the room was updated.
     *
     * @param httpRequest       the HTTP request containing session details
     * @param updateRoomRequest the request object containing updated room information
     * @return a response entity indicating the result of the update operation
     */
    @PutMapping
    ResponseEntity<?> update(HttpServletRequest httpRequest,
                             @RequestBody UpdateRoomRequest updateRoomRequest) {
        try {
            roomService.updateRoom(httpRequest, updateRoomRequest);
            return ResponseEntity.ok().body("Room updated");
        } catch (SecurityException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * This method processes the delete request. If successful, it returns
     * a message indicating the room was deleted.
     *
     * @param httpRequest the HTTP request containing session details
     * @param roomName    the name of the room to be deleted
     * @return a response entity indicating the result of the deletion operation
     */
    @DeleteMapping
    ResponseEntity<?> delete(HttpServletRequest httpRequest,
                             @RequestParam(name = "room_name") String roomName) {
        try {
            roomService.deleteRoom(httpRequest, roomName);
            return ResponseEntity.ok().body("Room deleted");
        } catch (SecurityException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
