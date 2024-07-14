package com.sinaev.controllers;

import com.sinaev.exceptions.ObjectAlreadyExistsException;
import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.requests.room.UpdateRoomRequest;
import com.sinaev.services.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/room")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

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

    @GetMapping
    ResponseEntity<List<RoomDTO>> getAll() {
        List<RoomDTO> rooms = roomService.getRooms();
        return ResponseEntity.ok(rooms);
    }

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
