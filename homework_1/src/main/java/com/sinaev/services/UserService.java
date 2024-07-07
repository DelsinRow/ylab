package com.sinaev.services;

import com.sinaev.annotations.Loggable;
import com.sinaev.mappers.UserMapper;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.User;
import com.sinaev.repositories.UserRepository;

import java.util.Optional;

/**
 * Manages user registration and authentication.
 */
@Loggable
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    /**
     * Constructs a UserService with the specified repository.
     *
     * @param userRepository the repository for managing users
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Logs in the specified user if they are registered and sets their admin status.
     *
     * @param userDTO the DTO of user attempting to log in
     * @return the logged-in User object if successful; otherwise, returns a User object with login status set to false
     */
    public Optional<UserDTO> login(UserDTO userDTO) {
        if (isRegistered(userDTO)) {
            User user = userMapper.toEntity(userDTO);
            if (isAdmin(user)) {
                user.setAdmin(true);
            }
            System.out.println("User: '" + user.getUsername() + "' successfully logged in");
            return Optional.of(UserMapper.INSTANCE.toDTO(user));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Registers the specified user if their username is not already taken.
     *
     * @param userDTO the DTO of user to register
     */
    public boolean register(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        user.setAdmin(false);
        if (!loginIsTaken(user)) {
            userRepository.save(user);
            System.out.println("User with login " + userDTO.username() + " registered");
            return true;
        } else {
            System.out.println("User with login " + userDTO.username() + " already exist");
            return false;
        }
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
    private boolean loginIsTaken(User user) {
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
}