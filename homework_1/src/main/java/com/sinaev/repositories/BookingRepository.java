package com.sinaev.repositories;

import com.sinaev.handlers.SQLQueryHandler;
import com.sinaev.models.Booking;
import com.sinaev.models.Room;
import com.sinaev.models.RoomType;
import com.sinaev.models.User;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing bookings.
 */
@RequiredArgsConstructor
public class BookingRepository {
    private final String urlDB;
    private final String userDB;
    private final String passwordDB;

    /**
     * Finds a booking by room and start time.
     *
     * @param startTime the start time of the booking
     * @param room      the room of the booking
     * @return an Optional containing the found booking, or an empty Optional if no booking is found
     */
    public Optional<Booking> findByRoomAndTime(LocalDateTime startTime, Room room) {
        String findByRoomAndTimeSQL = "SELECT * FROM bookings WHERE start_time = ? AND room_id = ?";
        Booking foundBooking = null;

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(findByRoomAndTimeSQL)) {

            changeSearchPath(connection);

            preparedStatement.setTimestamp(1, Timestamp.valueOf(startTime));
            preparedStatement.setLong(2, getRoomId(room));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    foundBooking = new Booking(
                            findUserById(resultSet.getLong("user_id")),
                            room,
                            resultSet.getTimestamp("start_time").toLocalDateTime(),
                            resultSet.getTimestamp("end_time").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }

        return Optional.ofNullable(foundBooking);
    }

    /**
     * Saves a booking to the database.
     *
     * @param booking the booking to save
     */
    public void save(Booking booking) {
        String saveSQL = "INSERT INTO bookings (user_id, room_id, start_time, end_time) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(saveSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, getUserId(booking.getUser()));
            preparedStatement.setLong(2, getRoomId(booking.getRoom()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Booking saved successfully.");
            } else {
                System.out.println("Failed to save the booking.");
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
    }

    /**
     * Updates an existing booking in the database.
     *
     * @param oldBooking the old booking to update
     * @param newBooking the new booking data
     */
    public void update(Booking oldBooking, Booking newBooking) {
        String updateSQL = "UPDATE bookings SET user_id = ?, room_id = ?, start_time = ?, end_time = ? WHERE room_id = ? AND start_time = ?";

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, getUserId(newBooking.getUser()));
            preparedStatement.setLong(2, getRoomId(newBooking.getRoom()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(newBooking.getStartTime()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(newBooking.getEndTime()));
            preparedStatement.setLong(5, getRoomId(oldBooking.getRoom()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(oldBooking.getStartTime()));

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Booking updated successfully.");
            } else {
                System.out.println("No booking found with the specified name.");
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
    }

    /**
     * Deletes a booking from the database.
     *
     * @param booking the booking to delete
     */
    public void delete(Booking booking) {
        String deleteSQL = "DELETE FROM bookings WHERE user_id = ? AND room_id = ? AND start_time = ? AND end_time = ?";

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, getUserId(booking.getUser()));
            preparedStatement.setLong(2, getRoomId(booking.getRoom()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Booking deleted successfully.");
            } else {
                System.out.println("Failed to delete the booking.");
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
    }

    /**
     * Finds all bookings for a specific room.
     *
     * @param room the room to find bookings for
     * @return a list of bookings for the specified room
     */
    public List<Booking> findByRoom(Room room) {
        List<Booking> bookings = new ArrayList<>();
        String findByRoomSQL = "SELECT * FROM bookings WHERE room_id = ?";

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(findByRoomSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, getRoomId(room));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = new Booking(
                            findUserById(resultSet.getLong("user_id")),
                            room,
                            resultSet.getTimestamp("start_time").toLocalDateTime(),
                            resultSet.getTimestamp("end_time").toLocalDateTime()
                    );
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }

        return bookings;
    }

    /**
     * Finds all bookings for a specific user.
     *
     * @param user the user to find bookings for
     * @return a list of bookings for the specified user
     */
    public List<Booking> findByUser(User user) {
        List<Booking> bookings = new ArrayList<>();
        String findByUserSQL = "SELECT * FROM bookings WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(findByUserSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, getUserId(user));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = new Booking(
                            user,
                            findRoomById(resultSet.getLong("room_id")),
                            resultSet.getTimestamp("start_time").toLocalDateTime(),
                            resultSet.getTimestamp("end_time").toLocalDateTime()
                    );
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }

        return bookings;
    }

    /**
     * Finds all bookings for a specific date.
     *
     * @param date the date to find bookings for
     * @return a list of bookings for the specified date
     */
    public List<Booking> findByDate(LocalDateTime date) {
        List<Booking> bookings = new ArrayList<>();
        String findByDateSQL = "SELECT * FROM bookings WHERE start_time >= ? AND start_time < ?";

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(findByDateSQL)) {

            changeSearchPath(connection);

            LocalDateTime endOfDay = date.toLocalDate().atTime(LocalTime.MAX);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(date));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endOfDay));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = new Booking(
                            findUserById(resultSet.getLong("user_id")),
                            findRoomById(resultSet.getLong("room_id")),
                            resultSet.getTimestamp("start_time").toLocalDateTime(),
                            resultSet.getTimestamp("end_time").toLocalDateTime()
                    );
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
        return bookings;
    }

    /**
     * Retrieves the ID of a user from the database.
     *
     * @param user the user to find the ID for
     * @return the ID of the specified user
     * @throws SQLException if the user is not found or a database access error occurs
     */
    private long getUserId(User user) throws SQLException {
        String selectUserIdSQL = "SELECT id FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(selectUserIdSQL)) {

            changeSearchPath(connection);

            preparedStatement.setString(1, user.getUsername());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                } else {
                    throw new SQLException("User not found.");
                }
            }
        }
    }

    /**
     * Retrieves the ID of a room from the database.
     *
     * @param room the room to find the ID for
     * @return the ID of the specified room
     * @throws SQLException if the room is not found or a database access error occurs
     */
    private long getRoomId(Room room) throws SQLException {
        String selectRoomIdSQL = "SELECT id FROM rooms WHERE room_name = ?";
        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(selectRoomIdSQL)) {

            changeSearchPath(connection);

            preparedStatement.setString(1, room.getName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                } else {
                    throw new SQLException("Room not found.");
                }
            }
        }
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId the ID of the user to find
     * @return the user with the specified ID
     * @throws SQLException if the user is not found or a database access error occurs
     */
    private User findUserById(long userId) throws SQLException {
        String selectUserSQL = "SELECT * FROM users WHERE id = ?";
        User user = null;

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(resultSet.getString("username"), resultSet.getString("password"));
                }
            }
        }

        if (user == null) {
            throw new SQLException("User not found.");
        }

        return user;
    }

    /**
     * Finds a room by its ID.
     *
     * @param roomId the ID of the room to find
     * @return the room with the specified ID
     * @throws SQLException if the room is not found or a database access error occurs
     */
    private Room findRoomById(long roomId) throws SQLException {
        String selectRoomSQL = "SELECT * FROM rooms WHERE id = ?";
        Room room = null;

        try (Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
             PreparedStatement preparedStatement = connection.prepareStatement(selectRoomSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, roomId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    room = new Room(resultSet.getString("room_name"), RoomType.valueOf(resultSet.getString("room_type")));
                }
            }
        }

        if (room == null) {
            throw new SQLException("Room not found.");
        }

        return room;
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