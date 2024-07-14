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

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth")
    ResponseEntity<?> login(HttpServletRequest httpRequest,
                            @RequestBody UserDTO userDTO) {
        try {
            userService.login(httpRequest, userDTO);
            userService.setUserDTOInSession(httpRequest, userDTO);
//            HttpSession session = httpRequest.getSession();
//            session.setAttribute("loggedIn", userDTO);
            return ResponseEntity.ok().body("User logged successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    ResponseEntity<?> register(HttpServletRequest httpRequest,
                               @RequestBody UserDTO userDTO) {
        try {
            userService.register(userDTO);
            userService.setUserDTOInSession(httpRequest, userDTO);
            return ResponseEntity.ok().body("User registered successfully");
        } catch (UsernameAlreadyTakenException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
