package com.sinaev.services;

import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.requests.room.UpdateRoomRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Service interface for managing rooms.
 * <p>
 * This interface defines methods for creating, retrieving, updating, and deleting rooms.
 * </p>
 */
public interface RoomService {
    /**
     * Creates a new room.
     *
     * @param req     the HTTP request containing user session information
     * @param roomDTO the room data transfer object containing room details
     */
    void createRoom(HttpServletRequest req, RoomDTO roomDTO);

    /**
     * Retrieves a list of all rooms.
     *
     * @return a list of room data transfer objects
     */
    List<RoomDTO> getRooms();

    /**
     * Updates an existing room.
     *
     * @param req               the HTTP request containing user session information
     * @param updateRoomRequest the request containing the original and new room details
     */
    void updateRoom(HttpServletRequest req, UpdateRoomRequest updateRoomRequest);

    /**
     * Deletes an existing room.
     *
     * @param req      the HTTP request containing user session information
     * @param roomName the name of the room to be deleted
     */
    void deleteRoom(HttpServletRequest req, String roomName);


}
