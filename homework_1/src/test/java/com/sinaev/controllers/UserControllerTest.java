package com.sinaev.controllers;

import com.sinaev.exceptions.UsernameAlreadyTakenException;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private UserController userController;

    public UserControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test successful login")
    public void testLoginSuccess() {
        UserDTO userDTO = new UserDTO("username", "password", false);
        doNothing().when(userService).login(httpRequest, userDTO);

        ResponseEntity<?> response = userController.login(httpRequest, userDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User logged successfully", response.getBody());
        verify(userService, times(1)).login(httpRequest, userDTO);
    }

    @Test
    @DisplayName("Test login with NoSuchElementException")
    public void testLoginNoSuchElementException() {
        UserDTO userDTO = new UserDTO("username", "password", false);
        doThrow(new NoSuchElementException("User not found")).when(userService).login(httpRequest, userDTO);

        ResponseEntity<?> response = userController.login(httpRequest, userDTO);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody());
        verify(userService, times(1)).login(httpRequest, userDTO);
    }

    @Test
    @DisplayName("Test successful registration")
    public void testRegisterSuccess() {
        UserDTO userDTO = new UserDTO("username", "password", false);
        doNothing().when(userService).register(userDTO);

        ResponseEntity<?> response = userController.register(httpRequest, userDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
        verify(userService, times(1)).register(userDTO);
    }

    @Test
    @DisplayName("Test registration with UsernameAlreadyTakenException")
    public void testRegisterUsernameAlreadyTakenException() {
        UserDTO userDTO = new UserDTO("username", "password", false);
        doThrow(new UsernameAlreadyTakenException("Username is already taken")).when(userService).register(userDTO);

        ResponseEntity<?> response = userController.register(httpRequest, userDTO);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Username is already taken", response.getBody());
        verify(userService, times(1)).register(userDTO);
    }
}