package com.sinaev.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.services.UserService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServletTest {

    private UserServlet userServlet;
    private UserService mockUserService;
    private DTOValidator mockValidator;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        userServlet = new UserServlet();
        mockUserService = mock(UserService.class);
        mockValidator = mock(DTOValidator.class);
        objectMapper = new ObjectMapper();

        userServlet.setUserService(mockUserService);
        userServlet.setValidator(mockValidator);
        userServlet.setObjectMapper(objectMapper);
    }

    @Test
    @DisplayName("Test handleRegister method - success")
    public void testHandleRegisterSuccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(userDTO);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getPathInfo()).thenReturn("/register");
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getSession()).thenReturn(session);  // Ensure session is returned
        when(response.getWriter()).thenReturn(writer);
        when(mockUserService.register(any(UserDTO.class))).thenReturn(true);

        userServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("User 'username' successfully registered");
    }

    @Test
    @DisplayName("Test handleRegister method - conflict")
    public void testHandleRegisterConflict() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(userDTO);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getPathInfo()).thenReturn("/register");
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getSession()).thenReturn(session);  // Ensure session is returned
        when(response.getWriter()).thenReturn(writer);
        when(mockUserService.register(any(UserDTO.class))).thenReturn(false);

        userServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(writer).write(argumentCaptor.capture());

        String expectedErrorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The operation was not completed. Please, check logs"));
        assertEquals(expectedErrorResponse, argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Test handleLogin method - success")
    public void testHandleLoginSuccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(userDTO);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
        when(mockUserService.login(any(UserDTO.class))).thenReturn(Optional.of(userDTO));

        userServlet.doPost(request, response);

        verify(session).setAttribute("loggedIn", userDTO);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).write("User 'username' successfully logged in");
    }

    @Test
    @DisplayName("Test handleLogin method - conflict")
    public void testHandleLoginConflict() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PrintWriter writer = mock(PrintWriter.class);

        UserDTO userDTO = new UserDTO("username", "password", false);
        String json = objectMapper.writeValueAsString(userDTO);
        ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream(json.getBytes()));

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
        when(mockUserService.login(any(UserDTO.class))).thenReturn(Optional.empty());

        userServlet.doPost(request, response);

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