package com.sinaev.services;

import com.sinaev.models.dto.UserDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * Service interface for managing users.
 * <p>
 * This interface defines methods for user login, registration, and session management.
 * </p>
 */
public interface UserService {
    /**
     * Logs in a user.
     *
     * @param httpReq the HTTP request containing user session information
     * @param userDTO the user data transfer object containing user credentials
     */
    void login(HttpServletRequest httpReq, UserDTO userDTO);

    /**
     * Registers a new user.
     *
     * @param userDTO the user data transfer object containing user details
     */
    void register(UserDTO userDTO);

    /**
     * Sets the user DTO in the session.
     *
     * @param req     the HTTP request containing the session
     * @param userDTO the user data transfer object to be set in the session
     */
    void setUserDTOInSession(HttpServletRequest req, UserDTO userDTO);
}
