package com.sinaev.controllers;

import com.sinaev.exceptions.UsernameAlreadyTakenException;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

/**
 * UserController handles the user authentication and registration requests.
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    /**
     * Constructs a UserController with the specified UserService.
     *
     * @param userService the service used to manage user-related operations
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Authenticates a user and sets their details in the session.
     *
     * @param httpRequest the HTTP request containing session details
     * @param userDTO     the user data transfer object containing login information
     * @return a response entity indicating the result of the login operation
     */
    @PostMapping("/auth")
    ResponseEntity<?> login(HttpServletRequest httpRequest,
                            @RequestBody UserDTO userDTO) {
        try {
            userService.login(httpRequest, userDTO);
            userService.setUserDTOInSession(httpRequest, userDTO);
            return ResponseEntity.ok().body("User logged successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Registers a new user and sets their details in the session.
     *
     * @param httpRequest the HTTP request containing session details
     * @param userDTO     the user data transfer object containing registration information
     * @return a response entity indicating the result of the registration operation
     */
    @PostMapping("/register")
    ResponseEntity<?> register(HttpServletRequest httpRequest,
                               @RequestBody UserDTO userDTO) {
        try {
            userService.register(userDTO);
            return ResponseEntity.ok().body("User registered successfully");
        } catch (UsernameAlreadyTakenException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
