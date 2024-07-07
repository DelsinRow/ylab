package com.sinaev.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinaev.annotations.Loggable;
import com.sinaev.configs.AppConfig;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.repositories.UserRepository;
import com.sinaev.services.UserService;
import com.sinaev.validators.DTOValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
@Setter
@Loggable
@WebServlet("/user/*")
public class UserServlet extends HttpServlet {
    private AppConfig config;
    private UserService userService;
    private DTOValidator validator;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.config = new AppConfig();
        this.userService = new UserService(new UserRepository(
                config.getDbUrl(),
                config.getDbUsername(),
                config.getDbPassword()

        ));
        this.validator = new DTOValidator();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        switch (path) {
            case "/login" -> handleLogin(req, resp);
            case "/register" -> handleRegister(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO userDTO = objectMapper.readValue(req.getInputStream(), UserDTO.class);

        validator.validate(userDTO);

        boolean isRegistered = userService.register(userDTO);
        if (isRegistered) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("User '" + userDTO.username() + "' successfully registered");
        } else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The operation was not completed. Please, check logs"));
            resp.getWriter().write(errorResponse);
        }

    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO userDTO = objectMapper.readValue(req.getInputStream(), UserDTO.class);

        validator.validate(userDTO);

        Optional<UserDTO> userDTOOptional = userService.login(userDTO);
        if (userDTOOptional.isPresent()) {

            UserDTO user = userDTOOptional.get();
            HttpSession session = req.getSession();
            session.setAttribute("loggedIn", user);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("User '" + userDTO.username() + "' successfully logged in");
        } else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The operation was not completed. Please, check logs"));
            resp.getWriter().write(errorResponse);
        }
    }
}
