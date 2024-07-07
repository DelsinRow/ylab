package com.sinaev.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinaev.annotations.Loggable;
import com.sinaev.configs.AppConfig;
import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.requests.room.UpdateRoomRequest;
import com.sinaev.repositories.RoomRepository;
import com.sinaev.services.RoomService;
import com.sinaev.validators.DTOValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Setter
@Loggable
@WebServlet("/room")
public class RoomServlet extends HttpServlet {
    private AppConfig config;
    private RoomService roomService;
    private DTOValidator validator;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.config = new AppConfig();
        this.roomService = new RoomService(new RoomRepository(
                config.getDbUrl(),
                config.getDbUsername(),
                config.getDbPassword()

        ));
        this.validator = new DTOValidator();
        this.objectMapper = new ObjectMapper();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            RoomDTO roomDTO = objectMapper.readValue(req.getInputStream(), RoomDTO.class);
            validator.validate(roomDTO);

            UserDTO loggedUser = getUserFromSession(req);
            if (isLogIn(loggedUser)) {
                Optional<RoomDTO> createdRoomOpt = roomService.createRoom(loggedUser, roomDTO);
                if (createdRoomOpt.isPresent()) {
                    RoomDTO createdRoom = createdRoomOpt.get();
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    String jsonResponse = objectMapper.writeValueAsString(createdRoom);
                    resp.getWriter().write(jsonResponse);
                } else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The room hasn't been created. Please, check logs"));
                    resp.getWriter().write(errorResponse);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "Requires logging in first"));
                resp.getWriter().write(errorResponse);
            }
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            Optional<List<RoomDTO>> roomsListOpt = roomService.getRooms();
            if (roomsListOpt.isPresent()) {
                List<RoomDTO> rooms = roomsListOpt.get();
                resp.setStatus(HttpServletResponse.SC_OK);
                String jsonResponse = objectMapper.writeValueAsString(rooms);
                resp.getWriter().write(jsonResponse);
            } else {
                String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "Rooms not found"));
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(errorResponse);
            }
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            UpdateRoomRequest sendedRequest = objectMapper.readValue(req.getInputStream(), UpdateRoomRequest.class);

            UserDTO loggedUser = getUserFromSession(req);
            if (isLogIn(loggedUser)) {
                String originalRoomName = sendedRequest.originalRoomName();
                String newRoomName = sendedRequest.newRoomName();
                String newRoomType = sendedRequest.newRoomType();


                boolean isRoomUpdated = roomService.updateRoom(loggedUser, originalRoomName, newRoomName, newRoomType);
                if (isRoomUpdated) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Room updated");
                } else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "Incorrect input data. Please, check logs"));
                    resp.getWriter().write(errorResponse);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "Requires logging in first"));
                resp.getWriter().write(errorResponse);
            }
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            UserDTO loggedUser = getUserFromSession(req);
            if (isLogIn(loggedUser)) {
                String roomName = req.getParameter("roomName");

                boolean isRoomDeleted = roomService.deleteRoom(loggedUser, roomName);
                if (isRoomDeleted) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Room deleted");
                } else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "The operation was not completed. Please, check logs"));
                    resp.getWriter().write(errorResponse);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", "Requires logging in first"));
                resp.getWriter().write(errorResponse);
            }
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorResponse = objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage()));
            resp.getWriter().write(errorResponse);
        }
    }

    private UserDTO getUserFromSession(HttpServletRequest req) {
        return (UserDTO) req.getSession().getAttribute("loggedIn");
    }

    private boolean isLogIn(UserDTO userDTO) {
        return userDTO != null;
    }
}
