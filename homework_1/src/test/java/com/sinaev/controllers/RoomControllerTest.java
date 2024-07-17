package com.sinaev.controllers;

import com.sinaev.exceptions.ObjectAlreadyExistsException;
import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.requests.room.UpdateRoomRequest;
import com.sinaev.services.RoomService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

public class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private RoomController roomController;

    private SoftAssertions softly;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        softly = new SoftAssertions();
    }

    @Test
    @DisplayName("Test successful room creation")
    public void testCreateRoomSuccess() {
        RoomDTO roomDTO = new RoomDTO("newRoom", "WORKSPACE");
        doNothing().when(roomService).createRoom(httpRequest, roomDTO);

        ResponseEntity<?> response = roomController.create(httpRequest, roomDTO);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        softly.assertThat(response.getBody()).isEqualTo("Room created");
        verify(roomService, times(1)).createRoom(httpRequest, roomDTO);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test room creation with exception")
    public void testCreateRoomException() {
        RoomDTO roomDTO = new RoomDTO("newRoom", "WORKSPACE");
        doThrow(new ObjectAlreadyExistsException("Room already exists")).when(roomService).createRoom(httpRequest, roomDTO);

        ResponseEntity<?> response = roomController.create(httpRequest, roomDTO);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(400);
        softly.assertThat(response.getBody()).isEqualTo("Room already exists");
        verify(roomService, times(1)).createRoom(httpRequest, roomDTO);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test get all rooms")
    public void testGetAllRooms() {
        RoomDTO room1 = new RoomDTO("newRoom1", "WORKSPACE");
        RoomDTO room2 = new RoomDTO("newRoom2", "WORKSPACE");
        List<RoomDTO> rooms = Arrays.asList(room1, room2);
        when(roomService.getRooms()).thenReturn(rooms);

        ResponseEntity<List<RoomDTO>> response = roomController.getAll();

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        softly.assertThat(response.getBody()).isEqualTo(rooms);
        verify(roomService, times(1)).getRooms();
        softly.assertAll();
    }

    @Test
    @DisplayName("Test successful room update")
    public void testUpdateRoomSuccess() {
        UpdateRoomRequest updateRoomRequest = new UpdateRoomRequest("room", "newRoom", "WORKSPACE");
        doNothing().when(roomService).updateRoom(httpRequest, updateRoomRequest);

        ResponseEntity<?> response = roomController.update(httpRequest, updateRoomRequest);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        softly.assertThat(response.getBody()).isEqualTo("Room updated");
        verify(roomService, times(1)).updateRoom(httpRequest, updateRoomRequest);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test room update with exception")
    public void testUpdateRoomException() {
        UpdateRoomRequest updateRoomRequest = new UpdateRoomRequest("room", "newRoom", "WORKSPACE");
        doThrow(new NoSuchElementException("Room not found")).when(roomService).updateRoom(httpRequest, updateRoomRequest);

        ResponseEntity<?> response = roomController.update(httpRequest, updateRoomRequest);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(400);
        softly.assertThat(response.getBody()).isEqualTo("Room not found");
        verify(roomService, times(1)).updateRoom(httpRequest, updateRoomRequest);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test successful room deletion")
    public void testDeleteRoomSuccess() {
        String roomName = "TestRoom";
        doNothing().when(roomService).deleteRoom(httpRequest, roomName);

        ResponseEntity<?> response = roomController.delete(httpRequest, roomName);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(200);
        softly.assertThat(response.getBody()).isEqualTo("Room deleted");
        verify(roomService, times(1)).deleteRoom(httpRequest, roomName);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test room deletion with exception")
    public void testDeleteRoomException() {
        String roomName = "TestRoom";
        doThrow(new NoSuchElementException("Room not found")).when(roomService).deleteRoom(httpRequest, roomName);

        ResponseEntity<?> response = roomController.delete(httpRequest, roomName);

        softly.assertThat(response.getStatusCodeValue()).isEqualTo(400);
        softly.assertThat(response.getBody()).isEqualTo("Room not found");
        verify(roomService, times(1)).deleteRoom(httpRequest, roomName);
        softly.assertAll();
    }
}