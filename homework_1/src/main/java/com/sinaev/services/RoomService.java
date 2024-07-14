package com.sinaev.services;

import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.requests.room.UpdateRoomRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
public interface RoomService {
    void createRoom(HttpServletRequest req, RoomDTO roomDTO);
    List<RoomDTO> getRooms();
    void updateRoom(HttpServletRequest req, UpdateRoomRequest updateRoomRequest);
    void deleteRoom(HttpServletRequest req, String roomName);



}
