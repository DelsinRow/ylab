package com.sinaev.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.requests.room.UpdateRoomRequest;
import com.sinaev.services.RoomService;
import com.sinaev.validators.DTOValidator;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoomServletTest {

    private RoomServlet roomServlet;
    private RoomService mockRoomService;
    private DTOValidator mockValidator;
    private ObjectMapper objectMapper;
    /**
     * Set up the test environment before each test.
     */
    @BeforeEach
    public void setUp() {
        roomServlet = new RoomServlet();
        mockRoomService = mock(RoomService.class);
        mockValidator = mock(DTOValidator.class);
        objectMapper = new ObjectMapper();

        roomServlet.setRoomService(mockRoomService);
        roomServlet.setValidator(mockValidator);
        roomServlet.setObjectMapper(objectMapper);
    }

    @Test
    @DisplayName("Test doPost method - create room successfully")
    public void testDoPostCreateRoomSuccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        RoomDTO roomDTO = new RoomDTO("room1", "type1");
        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(roomDTO);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getSession()).thenReturn(session);
        when(request.getInputStream()).thenReturn(inputStream);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(writer);
        when(mockRoomService.createRoom(any(UserDTO.class), any(RoomDTO.class))).thenReturn(Optional.of(roomDTO));

        roomServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedResponse = objectMapper.writeValueAsString(roomDTO);
        assertEquals(expectedResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test doPost method - room creation conflict")
    public void testDoPostCreateRoomConflict() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        RoomDTO roomDTO = new RoomDTO("room1", "type1");
        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(roomDTO);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getSession()).thenReturn(session);
        when(request.getInputStream()).thenReturn(inputStream);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(writer);
        when(mockRoomService.createRoom(any(UserDTO.class), any(RoomDTO.class))).thenReturn(Optional.empty());

        roomServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedErrorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The room hasn't been created. Please, check logs"));
        assertEquals(expectedErrorResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test doGet method - rooms found")
    public void testDoGetRoomsFound() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        RoomDTO roomDTO = new RoomDTO("room1", "type1");
        List<RoomDTO> rooms = Collections.singletonList(roomDTO);
        String jsonResponse = objectMapper.writeValueAsString(rooms);

        when(request.getSession()).thenReturn(mock(HttpSession.class));
        when(response.getWriter()).thenReturn(writer);
        when(mockRoomService.getRooms()).thenReturn(Optional.of(rooms));

        roomServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        assertEquals(jsonResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test doGet method - rooms not found")
    public void testDoGetRoomsNotFound() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(request.getSession()).thenReturn(mock(HttpSession.class));
        when(response.getWriter()).thenReturn(writer);
        when(mockRoomService.getRooms()).thenReturn(Optional.empty());

        roomServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedErrorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "Rooms not found"));
        assertEquals(expectedErrorResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test doPut method - room updated successfully")
    public void testDoPutRoomUpdateSuccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UpdateRoomRequest updateRoomRequest = new UpdateRoomRequest("room1", "newRoomName", "newRoomType");
        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(updateRoomRequest);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getSession()).thenReturn(session);
        when(request.getInputStream()).thenReturn(inputStream);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(writer);
        when(mockRoomService.updateRoom(any(UserDTO.class), eq("room1"), eq("newRoomName"), eq("newRoomType"))).thenReturn(true);

        roomServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("Room updated");
    }

    @Test
    @DisplayName("Test doPut method - room update conflict")
    public void testDoPutRoomUpdateConflict() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UpdateRoomRequest updateRoomRequest = new UpdateRoomRequest("room1", "newRoomName", "newRoomType");
        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(updateRoomRequest);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getSession()).thenReturn(session);
        when(request.getInputStream()).thenReturn(inputStream);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(writer);
        when(mockRoomService.updateRoom(any(UserDTO.class), eq("room1"), eq("newRoomName"), eq("newRoomType"))).thenReturn(false);

        roomServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedErrorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "Incorrect input data. Please, check logs"));
        assertEquals(expectedErrorResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test doDelete method - room deleted successfully")
    public void testDoDeleteRoomSuccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UserDTO userDTO = new UserDTO("username", "password", false);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(request.getParameter("roomName")).thenReturn("room1");
        when(response.getWriter()).thenReturn(writer);
        when(mockRoomService.deleteRoom(any(UserDTO.class), eq("room1"))).thenReturn(true);
        roomServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("Room deleted");
    }

    @Test
    @DisplayName("Test doDelete method - room deletion conflict")
    public void testDoDeleteRoomConflict() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UserDTO userDTO = new UserDTO("username", "password", false);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("loggedIn")).thenReturn(userDTO);
        when(request.getParameter("roomName")).thenReturn("room1");
        when(response.getWriter()).thenReturn(writer);
        when(mockRoomService.deleteRoom(any(UserDTO.class), eq("room1"))).thenReturn(false);

        roomServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedErrorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The operation was not completed. Please, check logs"));
        assertEquals(expectedErrorResponse, argumentCaptor.getValue());
    }

    // MockServletInputStream class
    private static class MockServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public MockServletInputStream(ByteArrayInputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}