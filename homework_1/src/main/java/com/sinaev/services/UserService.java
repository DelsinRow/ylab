package com.sinaev.services;

import com.sinaev.models.User;
import com.sinaev.repositories.UserRepository;

import java.util.Optional;

/**
 * Manages user registration and authentication.
 */
public class UserService {
    private final UserRepository userRepository;

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
     * @param username the username of the user attempting to log in
     * @param password the password of the user attempting to log in
     * @return the logged-in User object if successful; otherwise, returns a User object with login status set to false
     */
    public User login(String username, String password) {
        if (isRegistered(username, password)) {
            User user = new User(username, password);
            user.setLoggedIn(true);
            if (isAdmin(username)) {
                user.setAdmin(true);
            }
            System.out.println("User: '" + user.getUsername() + "' successfully logged in");
            return user;
        } else {
            return new User(false);
        }
    }

    /**
     * Registers the specified user if their username is not already taken.
     *
     * @param user the user to register
     */
    public void register(User user) {
        if (!loginIsTaken(user)) {
            userRepository.save(user);
            System.out.println("User with login: '" + user.getUsername() + "' and password: '" + user.getPassword() + "' successfully registered");
        } else {
            System.out.println("Login: '" + user.getUsername() + "' is already taken. Try another one");
        }
    }

    /**
     * Checks if a user with the specified login and password is registered.
     *
     * @param username the login of the user
     * @param password the password of the user
     * @return true if the user with the specified login and password is registered, false otherwise
     */
    private boolean isRegistered(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    /**
     * Checks if the specified user is an admin.
     *
     * @param username the username of the user to check
     * @return true if the user is an admin, false otherwise
     */
    private boolean isAdmin(String username) {
        return userRepository.findByUsername(username)
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