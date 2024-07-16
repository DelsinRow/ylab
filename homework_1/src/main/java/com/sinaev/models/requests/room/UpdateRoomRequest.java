package com.sinaev.models.requests.room;

/**
 * Request object for updating a room.
 * <p>
 * This record encapsulates the parameters required to update a room, including the original room name
 * and the new room details such as the new name and type.
 * </p>
 *
 * @param originalRoomName  the original name of the room
 * @param newRoomName       the new name of the room
 * @param newRoomType       the new type of the room
 */
public record UpdateRoomRequest(
        String originalRoomName,
        String newRoomName,
        String newRoomType) {
}
