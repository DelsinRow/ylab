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
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpSession httpSession;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    private UserDTO adminUserDTO;
    private UserDTO normalUserDTO;
    private RoomDTO roomDTO;

    @BeforeEach
    void setUp() {
        adminUserDTO = new UserDTO("admin", "adminpass", true);
        normalUserDTO = new UserDTO("user1", "password", false);
        roomDTO = new RoomDTO("Room1", RoomType.MEETING_ROOM.name());

        lenient().when(httpRequest.getSession()).thenReturn(httpSession);
    }

    @Test
    @DisplayName("Should create room by admin user")
    void testCreateRoomAsAdmin(SoftAssertions softly) {
        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        when(roomRepository.exists(roomDTO.name())).thenReturn(false);
        Room room = new Room("Room1", RoomType.MEETING_ROOM);
        when(roomMapper.toEntity(roomDTO)).thenReturn(room);

        roomService.createRoom(httpRequest, roomDTO);

        verify(roomRepository, times(1)).save(room);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not create room by normal user")
    void testCreateRoomAsNormalUser(SoftAssertions softly) {
        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);

        softly.assertThatThrownBy(() -> roomService.createRoom(httpRequest, roomDTO))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You do not have admin user access");
        verify(roomRepository, never()).save(any(Room.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not create room if it already exists")
    void testCreateRoomThatAlreadyExists(SoftAssertions softly) {
        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        when(roomRepository.exists(roomDTO.name())).thenReturn(true);

        softly.assertThatThrownBy(() -> roomService.createRoom(httpRequest, roomDTO))
                .isInstanceOf(ObjectAlreadyExistsException.class)
                .hasMessageContaining("Room with name 'Room1' already exists");
        verify(roomRepository, never()).save(any(Room.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should retrieve all rooms")
    void testGetRooms(SoftAssertions softly) {
        Room room = new Room("Room1", RoomType.MEETING_ROOM);
        RoomDTO roomDTO = new RoomDTO("Room1", RoomType.MEETING_ROOM.name());

        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
        when(roomMapper.toDTO(room)).thenReturn(roomDTO);

        List<RoomDTO> retrievedRooms = roomService.getRooms();

        softly.assertThat(retrievedRooms).hasSize(1);
        softly.assertThat(retrievedRooms.get(0).name()).isEqualTo("Room1");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should update room by admin user")
    void testUpdateRoomAsAdmin(SoftAssertions softly) {
        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        Room room = new Room("Room1", RoomType.MEETING_ROOM);
        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room));
        UpdateRoomRequest updateRoomRequest = new UpdateRoomRequest("Room1", "Room1Updated", RoomType.MEETING_ROOM.name());

        roomService.updateRoom(httpRequest, updateRoomRequest);

        verify(roomRepository).update(any(Room.class), any(Room.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not update room by normal user")
    void testUpdateRoomAsNormalUser(SoftAssertions softly) {
        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);
        UpdateRoomRequest updateRoomRequest = new UpdateRoomRequest("Room1", "Room1Updated", RoomType.MEETING_ROOM.name());

        softly.assertThatThrownBy(() -> roomService.updateRoom(httpRequest, updateRoomRequest))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You do not have admin user access");
        verify(roomRepository, never()).update(any(Room.class), any(Room.class));
        softly.assertAll();
    }

    @Test
    @DisplayName("Should delete room by admin user")
    void testDeleteRoomAsAdmin(SoftAssertions softly) {
        when(httpSession.getAttribute("loggedIn")).thenReturn(adminUserDTO);
        Room room = new Room("Room1", RoomType.MEETING_ROOM);
        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room));

        roomService.deleteRoom(httpRequest, "Room1");

        verify(roomRepository, times(1)).delete(room);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not delete room by normal user")
    void testDeleteRoomAsNormalUser(SoftAssertions softly) {
        when(httpSession.getAttribute("loggedIn")).thenReturn(normalUserDTO);

        softly.assertThatThrownBy(() -> roomService.deleteRoom(httpRequest, "Room1"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("You do not have admin user access");
        verify(roomRepository, never()).delete(any(Room.class));
        softly.assertAll();
    }
}