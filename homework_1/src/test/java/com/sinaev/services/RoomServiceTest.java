package com.sinaev.services;

import com.sinaev.models.Room;
import com.sinaev.models.RoomType;
import com.sinaev.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomServiceTest {

    private RoomService roomService;
    private List<Room> rooms;
    private User adminUser;
    private User normalUser;
    private Room room1;

    @BeforeEach
    public void setUp() {
        rooms = new ArrayList<>();
        room1 = new Room("Room1", RoomType.MEETING_ROOM);
        rooms.add(room1);
        roomService = new RoomService(rooms);
        adminUser = new User("admin", "adminpass", true);
        normalUser = new User("user1", "password", false);
    }

    @Test
    public void testCreateRoomAsAdmin() {
        Room room2 = new Room("Room2", RoomType.MEETING_ROOM);
        roomService.createRoom(adminUser, room2);

        assert rooms.size() == 2;
        assert rooms.contains(room2);
    }

    @Test
    public void testCreateRoomAsNormalUser() {
        Room room2 = new Room("Room2", RoomType.MEETING_ROOM);
        roomService.createRoom(normalUser, room2);

        assert rooms.size() == 1;
        assert !rooms.contains(room2);
    }

    @Test
    public void testCreateRoomThatAlreadyExists() {
        roomService.createRoom(adminUser, room1);

        assert rooms.size() == 1;
    }

    @Test
    public void testGetRooms() {
        List<Room> retrievedRooms = roomService.getRooms();

        assert retrievedRooms.size() == 1;
        assert retrievedRooms.get(0).equals(room1);
    }

    @Test
    public void testUpdateRoomAsAdmin() {
        Room updatedRoom = new Room("UpdatedRoom1", RoomType.WORKSPACE);
        roomService.updateRoom(adminUser, "Room1", updatedRoom);

        assert rooms.size() == 1;
        assert rooms.get(0).getName().equals(updatedRoom.getName());
        assert rooms.get(0).getType().equals(updatedRoom.getType());
    }

    @Test
    public void testUpdateRoomAsNormalUser() {
        Room updatedRoom = new Room("UpdatedRoom1", RoomType.WORKSPACE);
        roomService.updateRoom(normalUser, "Room1", updatedRoom);

        assert rooms.size() == 1;
        assert rooms.get(0).getName().equals(room1.getName());
    }

    @Test
    public void testDeleteRoomAsAdmin() {
        roomService.deleteRoom(adminUser, "Room1");

        assert rooms.size() == 0;
    }

    @Test
    public void testDeleteRoomAsNormalUser() {
        roomService.deleteRoom(normalUser, "Room1");

        assert rooms.size() == 1;
    }

    @Test
    public void testGetRoomByName() {
        Optional<Room> foundRoom = roomService.getRoomByName("Room1");

        assert foundRoom.isPresent();
        assert foundRoom.get().equals(room1);
    }

    @Test
    public void testGetNonExistingRoomByName() {
        Optional<Room> foundRoom = roomService.getRoomByName("NonExistingRoom");

        assert !foundRoom.isPresent();
    }
}