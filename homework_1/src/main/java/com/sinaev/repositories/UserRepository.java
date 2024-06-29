package com.sinaev.repositories;

import com.sinaev.handlers.SQLQueryHandler;
import com.sinaev.models.User;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Repository for managing users.
 */
@RequiredArgsConstructor
public class UserRepository {
    private final String urlDB;
    private final String userDB;
    private final String passwordDB;

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to find
     * @return an Optional containing the found user, or an empty Optional if no user is found
     */
    public Optional<User> findByUsername(String username) {
        String selectSQL = "SELECT * FROM users WHERE username = ?";
        User foundUser = null;
        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            changeSearchPath(connection);
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    foundUser = new User(
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getBoolean("is_admin"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }

        return Optional.ofNullable(foundUser);
    }

    /**
     * Saves a new user to the database.
     *
     * @param saveUser the user to save
     */
    public void save(User saveUser) {
        String insertSQL = "INSERT INTO users(username, password, is_admin) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            changeSearchPath(connection);

            preparedStatement.setString(1, saveUser.getUsername());
            preparedStatement.setString(2, saveUser.getPassword());
            preparedStatement.setBoolean(3, saveUser.isAdmin());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
    }

    /**
     * Checks if a user exists by their username.
     *
     * @param username the username to check for existence
     * @return true if a user with the specified username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        String SQL_SELECT = "SELECT 1 FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT)) {
            changeSearchPath(connection);
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }

        return false;
    }

    /**
     * Changes the search path to the specified schema.
     *
     * @param connection the database connection to use
     */
    void changeSearchPath(Connection connection) {
        SQLQueryHandler handler = new SQLQueryHandler();
        handler.addSearchPathPrivate(connection);
    }
}