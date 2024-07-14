package com.sinaev.repositories;

import com.sinaev.handlers.SQLQueryHandler;
import com.sinaev.models.entities.Booking;
import com.sinaev.models.entities.Room;
import com.sinaev.models.entities.User;
import com.sinaev.models.enums.RoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing bookings.
 */
@Repository
@RequiredArgsConstructor
public class BookingRepository {
    private final DataSource dataSource;

    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String findAllSQL = "SELECT * FROM entity_schema.bookings";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(findAllSQL)) {

//            changeSearchPath(connection);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long userId = resultSet.getLong("user_id");
                User user = findUserById(userId);
                long roomId = resultSet.getLong("room_id");
                Room room = findRoomById(roomId);
                LocalDateTime startTime = resultSet.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = resultSet.getTimestamp("end_time").toLocalDateTime();
                Booking booking = new Booking(user, room, startTime, endTime);

                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
        return bookings;
    }

    /**
     * Finds a booking by room and start time.
     *
     * @param startTime the start time of the booking
     * @param roomName  the name of room of the booking
     * @return an Optional containing the found booking, or an empty Optional if no booking is found
     */
    public Optional<Booking> findByRoomAndTime(String roomName,LocalDateTime startTime) {
        String findByRoomAndTimeSQL = "SELECT * FROM bookings WHERE start_time = ? AND room_id = ?";
        Booking foundBooking = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(findByRoomAndTimeSQL)) {

            changeSearchPath(connection);

            preparedStatement.setTimestamp(1, Timestamp.valueOf(startTime));
            preparedStatement.setLong(2, getRoomId(roomName));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    foundBooking = new Booking(
                            findUserById(resultSet.getLong("user_id")),
                            findRoomByName(roomName),
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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(saveSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, getUserId(booking.getUser().getUsername()));
            preparedStatement.setLong(2, getRoomId(booking.getRoom().getName()));
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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, getUserId(newBooking.getUser().getUsername()));
            preparedStatement.setLong(2, getRoomId(newBooking.getRoom().getName()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(newBooking.getStartTime()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(newBooking.getEndTime()));
            preparedStatement.setLong(5, getRoomId(oldBooking.getRoom().getName()));
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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, getUserId(booking.getUser().getUsername()));
            preparedStatement.setLong(2, getRoomId(booking.getRoom().getName()));
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
     * @param roomName the name of room to find bookings for
     * @return a list of bookings for the specified room
     */
    public List<Booking> findByRoomName(String roomName) {
        List<Booking> bookings = new ArrayList<>();
        String findByRoomSQL = "SELECT * FROM bookings WHERE room_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(findByRoomSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, getRoomId(roomName));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = new Booking(
                            findUserById(resultSet.getLong("user_id")),
                            findRoomByName(roomName),
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
     * @param userName the username of user to find bookings for
     * @return a list of bookings for the specified user
     */
    public List<Booking> findByUserName(String userName) {
        List<Booking> bookings = new ArrayList<>();
        String findByUserSQL = "SELECT * FROM bookings WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(findByUserSQL)) {

            changeSearchPath(connection);

            preparedStatement.setLong(1, getUserId(userName));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Booking booking = new Booking(
                            findUserById(getUserId(userName)),
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
    public List<Booking> findByDate(LocalDate date) {
        List<Booking> bookings = new ArrayList<>();
        String findByDateSQL = "SELECT * FROM bookings WHERE start_time >= ? AND start_time < ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(findByDateSQL)) {

            changeSearchPath(connection);
            LocalDateTime dateTime = date.atTime(0, 0);
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(dateTime));
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

    public Room findRoomByName(String roomName) {
        try {
            return findRoomById(getRoomId(roomName));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the ID of a user from the database.
     *
     * @param username the name of user to find the ID for
     * @return the ID of the specified user
     * @throws SQLException if the user is not found or a database access error occurs
     */
    private long getUserId(String username) throws SQLException {
        String selectUserIdSQL = "SELECT id FROM users WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectUserIdSQL)) {

            changeSearchPath(connection);

            preparedStatement.setString(1, username);
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
     * @param roomName the name of room to find the ID for
     * @return the ID of the specified room
     * @throws SQLException if the room is not found or a database access error occurs
     */
    private long getRoomId(String roomName) throws SQLException {
        String selectRoomIdSQL = "SELECT id FROM rooms WHERE room_name = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectRoomIdSQL)) {

            changeSearchPath(connection);

            preparedStatement.setString(1, roomName);
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

        try (Connection connection = dataSource.getConnection();
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

        try (Connection connection = dataSource.getConnection();
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