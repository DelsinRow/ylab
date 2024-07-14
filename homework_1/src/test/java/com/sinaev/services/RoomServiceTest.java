//package com.sinaev.services;
//
//import com.sinaev.mappers.RoomMapper;
//import com.sinaev.models.dto.RoomDTO;
//import com.sinaev.models.dto.UserDTO;
//import com.sinaev.models.entities.Room;
//import com.sinaev.repositories.RoomRepository;
//import com.sinaev.services.impl.RoomServiceImpl;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//class RoomServiceTest {
//
//    private RoomServiceImpl roomService;
//    private RoomRepository roomRepository;
//    private UserDTO adminUserDTO;
//    private UserDTO normalUserDTO;
//    private RoomDTO roomDTO;
//    private Room room;
//
//    @BeforeEach
//    void setUp() {
//        roomRepository = mock(RoomRepository.class);
//        roomService = new RoomServiceImpl(roomRepository);
//        adminUserDTO = new UserDTO("admin", "adminpass", true);
//        normalUserDTO = new UserDTO("user1", "password", false);
//        roomDTO = new RoomDTO("Room1", "MEETING_ROOM");
//        room = RoomMapper.INSTANCE.toEntity(roomDTO);
//    }
//
//    @Test
//    @DisplayName("Should create room by admin user")
//    void testCreateRoomAsAdmin() {
//        when(roomRepository.exists(roomDTO.name())).thenReturn(false);
//
//        Optional<RoomDTO> createdRoom = roomService.createRoom(adminUserDTO, roomDTO);
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(createdRoom).isPresent();
//        verify(roomRepository, times(1)).save(any(Room.class));
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should not create room by normal user")
//    void testCreateRoomAsNormalUser() {
//        Optional<RoomDTO> createdRoom = roomService.createRoom(normalUserDTO, roomDTO);
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(createdRoom).isNotPresent();
//        verify(roomRepository, never()).save(any(Room.class));
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should not create room if it already exists")
//    void testCreateRoomThatAlreadyExists() {
//        when(roomRepository.exists(roomDTO.name())).thenReturn(true);
//
//        Optional<RoomDTO> createdRoom = roomService.createRoom(adminUserDTO, roomDTO);
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(createdRoom).isNotPresent();
//        verify(roomRepository, never()).save(any(Room.class));
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should retrieve all rooms")
//    void testGetRooms() {
//        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
//
//        Optional<List<RoomDTO>> retrievedRooms = roomService.getRooms();
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(retrievedRooms).isPresent();
//        softly.assertThat(retrievedRooms.get()).hasSize(1);
//        softly.assertThat(retrievedRooms.get().get(0).name()).isEqualTo("Room1");
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should update room by admin user")
//    void testUpdateRoomAsAdmin() {
//        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room));
//
//        boolean result = roomService.updateRoom(adminUserDTO, "Room1", "Room1Updated", "MEETING_ROOM");
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(result).isTrue();
//        verify(roomRepository).update(any(Room.class), any(Room.class));
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should not update room by normal user")
//    void testUpdateRoomAsNormalUser() {
//        boolean result = roomService.updateRoom(normalUserDTO, "Room1", "Room1Updated", "MEETING_ROOM");
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(result).isFalse();
//        verify(roomRepository, never()).update(any(Room.class), any(Room.class));
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should delete room by admin user")
//    void testDeleteRoomAsAdmin() {
//        when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room));
//
//        boolean result = roomService.deleteRoom(adminUserDTO, "Room1");
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(result).isTrue();
//        verify(roomRepository, times(1)).delete(any(Room.class));
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should not delete room by normal user")
//    void testDeleteRoomAsNormalUser() {
//        boolean result = roomService.deleteRoom(normalUserDTO, "Room1");
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(result).isFalse();
//        verify(roomRepository, never()).delete(any(Room.class));
//        softly.assertAll();
//    }
//}