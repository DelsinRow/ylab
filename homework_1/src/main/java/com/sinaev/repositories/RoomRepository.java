package com.sinaev.repositories;

import com.sinaev.handlers.SQLQueryHandler;
import com.sinaev.models.entities.Room;
import com.sinaev.models.enums.RoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing rooms.
 */
@Repository
@RequiredArgsConstructor
public class RoomRepository {
    private final DataSource dataSource;

    /**
     * Finds all rooms in the database.
     *
     * @return a list of all rooms
     */
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String findAllSQL = "SELECT * FROM rooms";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(findAllSQL)) {

            changeSearchPath(connection);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("room_name");
                RoomType roomType = RoomType.valueOf(resultSet.getString("room_type"));
                Room room = new Room(name, roomType);
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
        return rooms;
    }

    /**
     * Finds a room by its name.
     *
     * @param roomName the name of the room to find
     * @return an Optional containing the found room, or an empty Optional if no room is found
     */
    public Optional<Room> findByName(String roomName) {
        String selectSQL = "SELECT * FROM rooms WHERE room_name = ?";
        Room foundRoom = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            changeSearchPath(connection);

            preparedStatement.setString(1, roomName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    foundRoom = new Room(
                            resultSet.getString("room_name"),
                            RoomType.valueOf(resultSet.getString("room_type")));
                }
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
        return Optional.ofNullable(foundRoom);
    }

    /**
     * Updates an existing room in the database.
     *
     * @param oldRoom the old room to update
     * @param newRoom the new room data
     */
    public void update(Room oldRoom, Room newRoom) {
        String updateSQL = "UPDATE rooms SET room_name = ?, room_type = ?::roomtype WHERE room_name = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            changeSearchPath(connection);

            preparedStatement.setString(1, newRoom.getName());
            preparedStatement.setString(2, newRoom.getType().name());
            preparedStatement.setString(3, oldRoom.getName());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
    }

    /**
     * Saves a new room to the database.
     *
     * @param room the room to save
     */
    public void save(Room room) {
        String saveSQL = "INSERT INTO rooms(room_name, room_type) VALUES (?, ?::roomtype)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(saveSQL)) {

            changeSearchPath(connection);

            System.out.println("room name: " + room.getName() + ", type: " + room.getType().getType() + " saved");
            preparedStatement.setString(1, room.getName());
            preparedStatement.setString(2, room.getType().getType());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
    }

    /**
     * Deletes a room from the database.
     *
     * @param room the room to delete
     */
    public void delete(Room room) {
        String roomName = room.getName();
        String deleteSQL = "DELETE FROM rooms WHERE room_name = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            changeSearchPath(connection);

            preparedStatement.setString(1, roomName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
    }

    /**
     * Checks if a room exists in the database.
     *
     * @param roomName the name of room to check
     * @return true if the room exists, false otherwise
     */
    public boolean exists(String roomName) {
        String selectSQL = "SELECT * FROM rooms WHERE room_name = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            changeSearchPath(connection);

            preparedStatement.setString(1, roomName);

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
    protected void changeSearchPath(Connection connection) {
        SQLQueryHandler handler = new SQLQueryHandler();
        handler.addSearchPathPrivate(connection);
    }
}