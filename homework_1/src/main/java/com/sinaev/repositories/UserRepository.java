package com.sinaev.repositories;

import com.sinaev.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing users.
 */
public class UserRepository {
    private final List<User> users = new ArrayList<>();

    /**
     * Finds all users in the repository.
     *
     * @return a list of all users
     */
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the found user, or an empty Optional if no user was found
     */
    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    /**
     * Saves a new user to the repository.
     *
     * @param user the user to save
     */
    public void save(User user) {
        users.add(user);
    }

    /**
     * Checks if a user with the specified username exists in the repository.
     *
     * @param username the username to check
     * @return true if a user with the specified username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return users.stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }
}