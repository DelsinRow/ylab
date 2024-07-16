package com.sinaev.services.impl;

import com.sinaev.annotations.Loggable;
import com.sinaev.exceptions.UsernameAlreadyTakenException;
import com.sinaev.mappers.UserMapper;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.User;
import com.sinaev.repositories.UserRepository;
import com.sinaev.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Manages user registration and authentication.
 */
@Service
@Loggable
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Logs in the specified user if they are registered.
     *
     * @param httpReq the HTTP request containing user session information
     * @param userDTO the DTO of user attempting to log in
     * @throws NoSuchElementException if the user is not found
     */
    public void login(HttpServletRequest httpReq, UserDTO userDTO) {
        if (!isRegistered(userDTO)) {
            throw new NoSuchElementException("User: '" + userDTO.username() + "' not found");
        }
        User user = userMapper.toEntity(userDTO);
        if (isAdmin(user)) {
            user.setAdmin(true);
        }
        setUserDTOInSession(httpReq, userDTO);
    }

    /**
     * Registers the specified user if their username is not already taken.
     *
     * @param userDTO the DTO of user to register
     * @throws UsernameAlreadyTakenException if the username is already taken
     */
    public void register(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        user.setAdmin(false);
        if (usernameIsTaken(user)) {
            throw new UsernameAlreadyTakenException("User with login " + userDTO.username() + " already exist");
        }
        userRepository.save(user);
    }

    /**
     * Checks if a user with the specified login and password is registered.
     *
     * @param userDTO the DTO of the user including login and password
     * @return true if the user with the specified login and password is registered, false otherwise
     */
    private boolean isRegistered(UserDTO userDTO) {
        return userRepository.findByUsername(userDTO.username())
                .map(user -> user.getPassword().equals(userDTO.password()))
                .orElse(false);
    }

    /**
     * Checks if the specified user is an admin.
     *
     * @param user the user to check
     * @return true if the user is an admin, false otherwise
     */
    private boolean isAdmin(User user) {
        return userRepository.findByUsername(user.getUsername())
                .map(User::isAdmin)
                .orElse(false);
    }

    /**
     * Checks if the specified user's username is already taken.
     *
     * @param user the user to check
     * @return true if the username is already taken, false otherwise
     */
    private boolean usernameIsTaken(User user) {
        return userRepository.existsByUsername(user.getUsername());
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return an Optional containing the user if found, or an empty Optional if no user with the specified username exists
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Sets the user DTO in the session.
     *
     * @param req     the HTTP request containing the session
     * @param userDTO the user data transfer object to be set in the session
     * @throws NoSuchElementException if the user is not found
     */
    public void setUserDTOInSession(HttpServletRequest req, UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findByUsername(userDTO.username());
        if (userOpt.isEmpty()) {
            throw new NoSuchElementException("User not found");
        }
        UserDTO userWithAdminStatus = userMapper.toDTO(userOpt.get());
        HttpSession session = req.getSession();
        session.setAttribute("loggedIn", userWithAdminStatus);
    }
}