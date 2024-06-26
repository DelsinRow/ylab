package com.sinaev.services;

import com.sinaev.models.Room;
import com.sinaev.models.RoomType;
import com.sinaev.models.User;
import com.sinaev.repositories.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RoomServiceTest {

    private RoomService roomService;
    private RoomRepository roomRepository;
    private User adminUser;
    private User normalUser;
    private Room room1;

    @BeforeEach
    public void setUp() {
        roomRepository = mock(RoomRepository.class);
        roomService = new RoomService(roomRepository);
        adminUser = new User("admin", "adminpass", true);
        normalUser = new User("user1", "password", false);
        room1 = new Room("Room1", RoomType.MEETING_ROOM);
    }

    /**
     * Tests the creation of a room by an admin user.
     * Steps:
     * 1. Mock the repository to return false for room existence.
     * 2. Call the createRoom method.
     * 3. Verify that the room is saved in the repository.
     * Expected result: The room is created and saved in the repository.
     */
    @Test
    public void testCreateRoomAsAdmin() {
        when(roomRepository.exists(room1)).thenReturn(false);

        roomService.createRoom(adminUser, room1);

        verify(roomRepository, times(1)).save(room1);
        verify(roomRepository, times(1)).exists(room1);
    }

    /**
     * Tests the creation of a room by a normal user.
     * Steps:
     * 1. Call the createRoom method.
     * 2. Verify that the room is not saved in the repository.
     * Expected result: The room is not created and not saved in the repository.
     */
    @Test
    public void testCreateRoomAsNormalUser() {
        roomService.createRoom(normalUser, room1);

        verify(roomRepository, never()).save(room1);
    }

    /**
     * Tests the creation of a room that already exists.
     * Steps:
     * 1. Mock the repository to return true for room existence.
     * 2. Call the createRoom method.
     * 3. Verify that the room is not saved in the repository.
     * Expected result: The room is not created and not saved in the repository.
     */
    @Test
    public void testCreateRoomThatAlreadyExists() {
        when(roomRepository.exists(room1)).thenReturn(true);

        roomService.createRoom(adminUser, room1);

        verify(roomRepository, never()).save(room1);
    }

    /**
     * Tests the retrieval of all rooms.
     * Steps:
     * 1. Mock the repository to return a list of rooms.
     * 2. Call the getRooms method.
     * 3. Verify the returned list of rooms.
     * Expected result: The list of rooms is retrieved from the repository.
     */
    @Test
    public void testGetRooms() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room1));

        List<Room> retrievedRooms = roomService.getRooms();

        assertThat(retrievedRooms).hasSize(1);
        assertThat(retrievedRooms.get(0)).isEqualTo(room1);
    }

    /**
     * Tests the update of a room by an admin user.
     * Steps:
     * 1. Mock the repository to return the room.
     * 2. Call the updateRoom method.
     * 3. Verify that the room is deleted and the updated room is saved in the repository.
     * Expected result: The room is updated and saved in the repository.
     */
    @Test
    public void testUpdateRoomAsAdmin() {
        Room updatedRoom = new Room("UpdatedRoom1", RoomType.WORKSPACE);
        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room1));

        roomService.updateRoom(adminUser, "Room1", updatedRoom);

        verify(roomRepository, times(1)).delete(room1);
        verify(roomRepository, times(1)).save(updatedRoom);
    }

    /**
     * Tests the update of a room by a normal user.
     * Steps:
     * 1. Call the updateRoom method.
     * 2. Verify that the room is not deleted and the updated room is not saved in the repository.
     * Expected result: The room is not updated and not saved in the repository.
     */
    @Test
    public void testUpdateRoomAsNormalUser() {
        Room updatedRoom = new Room("UpdatedRoom1", RoomType.WORKSPACE);

        roomService.updateRoom(normalUser, "Room1", updatedRoom);

        verify(roomRepository, never()).delete(room1);
        verify(roomRepository, never()).save(updatedRoom);
    }

    /**
     * Tests the deletion of a room by an admin user.
     * Steps:
     * 1. Mock the repository to return the room.
     * 2. Call the deleteRoom method.
     * 3. Verify that the room is deleted from the repository.
     * Expected result: The room is deleted from the repository.
     */
    @Test
    public void testDeleteRoomAsAdmin() {
        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room1));

        roomService.deleteRoom(adminUser, "Room1");

        verify(roomRepository, times(1)).delete(room1);
    }

    /**
     * Tests the deletion of a room by a normal user.
     * Steps:
     * 1. Call the deleteRoom method.
     * 2. Verify that the room is not deleted from the repository.
     * Expected result: The room is not deleted from the repository.
     */
    @Test
    public void testDeleteRoomAsNormalUser() {
        roomService.deleteRoom(normalUser, "Room1");

        verify(roomRepository, never()).delete(room1);
    }

    /**
     * Tests the retrieval of a room by its name.
     * Steps:
     * 1. Mock the repository to return the room.
     * 2. Call the getRoomByName method.
     * 3. Verify the returned room.
     * Expected result: The room is retrieved from the repository.
     */
    @Test
    public void testGetRoomByName() {
        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room1));

        Optional<Room> foundRoom = roomService.getRoomByName("Room1");

        assertThat(foundRoom).isPresent();
        assertThat(foundRoom.get()).isEqualTo(room1);
    }

    /**
     * Tests the retrieval of a non-existing room by its name.
     * Steps:
     * 1. Mock the repository to return an empty Optional.
     * 2. Call the getRoomByName method.
     * 3. Verify the room is not found.
     * Expected result: The room is not found in the repository.
     */
    @Test
    public void testGetNonExistingRoomByName() {
        when(roomRepository.findByName("NonExistingRoom")).thenReturn(Optional.empty());

        Optional<Room> foundRoom = roomService.getRoomByName("NonExistingRoom");

        assertThat(foundRoom).isNotPresent();
    }
}