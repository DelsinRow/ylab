package com.sinaev.services;

import com.sinaev.exceptions.ObjectAlreadyExistsException;
import com.sinaev.mappers.RoomMapper;
import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.Room;
import com.sinaev.models.enums.RoomType;
import com.sinaev.models.requests.room.UpdateRoomRequest;
import com.sinaev.repositories.RoomRepository;
import com.sinaev.services.impl.RoomServiceImpl;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;

class RoomServiceTest {

    private RoomServiceImpl roomService;
    private RoomRepository roomRepository;
    private HttpServletRequest httpRequest;
    private HttpSession httpSession;
    private UserDTO adminUserDTO;
    private UserDTO normalUserDTO;
    private RoomDTO roomDTO;
    private Room room;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        httpRequest = mock(HttpServletRequest.class);
        httpSession = mock(HttpSession.class);
        roomService = new RoomServiceImpl(roomRepository);
        adminUserDTO = new UserDTO("admin", "adminpass", true);
        normalUserDTO = new UserDTO("user1", "password", false);
        roomDTO = new RoomDTO("Room1", RoomType.MEETING_ROOM.name());
        room = RoomMapper.INSTANCE.toEntity(roomDTO);

        when(httpRequest.getSession()).thenReturn(httpSession);
    }

    @Test
    @DisplayName("Should create room by admin user")
    void testCreateRoomAsAdmin() {
        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        when(roomRepository.exists(roomDTO.name())).thenReturn(false);

        roomService.createRoom(httpRequest, roomDTO);

        SoftAssertions softly = new SoftAssertions();
        verify(roomRepository, times(1)).save(any(Room.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not create room by normal user")
    void testCreateRoomAsNormalUser() {
        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatThrownBy(() -> roomService.createRoom(httpRequest, roomDTO))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You do not have admin user access");
        verify(roomRepository, never()).save(any(Room.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not create room if it already exists")
    void testCreateRoomThatAlreadyExists() {
        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        when(roomRepository.exists(roomDTO.name())).thenReturn(true);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatThrownBy(() -> roomService.createRoom(httpRequest, roomDTO))
                .isInstanceOf(ObjectAlreadyExistsException.class)
                .hasMessageContaining("Room with name 'Room1' already exists");
        verify(roomRepository, never()).save(any(Room.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should retrieve all rooms")
    void testGetRooms() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));

        List<RoomDTO> retrievedRooms = roomService.getRooms();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(retrievedRooms).hasSize(1);
        softly.assertThat(retrievedRooms.get(0).name()).isEqualTo("Room1");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should update room by admin user")
    void testUpdateRoomAsAdmin() {
        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room));
        UpdateRoomRequest updateRoomRequest = new UpdateRoomRequest("Room1", "Room1Updated", RoomType.MEETING_ROOM.name());

        roomService.updateRoom(httpRequest, updateRoomRequest);

        SoftAssertions softly = new SoftAssertions();
        verify(roomRepository).update(any(Room.class), any(Room.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not update room by normal user")
    void testUpdateRoomAsNormalUser() {
        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);
        UpdateRoomRequest updateRoomRequest = new UpdateRoomRequest("Room1", "Room1Updated", RoomType.MEETING_ROOM.name());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatThrownBy(() -> roomService.updateRoom(httpRequest, updateRoomRequest))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You do not have admin user access");
        verify(roomRepository, never()).update(any(Room.class), any(Room.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should delete room by admin user")
    void testDeleteRoomAsAdmin() {
        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room));

        roomService.deleteRoom(httpRequest, "Room1");

        SoftAssertions softly = new SoftAssertions();
        verify(roomRepository, times(1)).delete(any(Room.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not delete room by normal user")
    void testDeleteRoomAsNormalUser() {
        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatThrownBy(() -> roomService.deleteRoom(httpRequest, "Room1"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You do not have admin user access");
        verify(roomRepository, never()).delete(any(Room.class));
        softly.assertAll();
    }
}