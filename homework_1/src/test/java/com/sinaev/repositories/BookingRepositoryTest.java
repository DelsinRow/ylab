package com.sinaev.repositories;

import com.sinaev.models.Booking;
import com.sinaev.models.Room;
import com.sinaev.models.RoomType;
import com.sinaev.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

/**
 * Test class for BookingRepository.
 * This class uses Testcontainers to start a PostgreSQL container for testing.
 */
@Testcontainers
public class BookingRepositoryTest {

    /**
     * PostgreSQL container for testing.
     */
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    private BookingRepository bookingRepository;

    /**
     * Start the PostgreSQL container before all tests.
     */
    @BeforeAll
    public static void setUpContainer() {
        postgresContainer.start();
    }

    /**
     * Initialize the BookingRepository and create necessary database structures before each test.
     */
    @BeforeEach
    public void init() {
        bookingRepository = Mockito.spy(new BookingRepository(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword()));
        doNothing().when(bookingRepository).changeSearchPath(any(Connection.class));
        createEnumType();
        createTables();
    }

    /**
     * Drop database structures after each test.
     */
    @AfterEach
    public void tearDown() {
        dropTables();
        dropEnumType();
    }

    /**
     * Create enum type 'roomtype' in the database.
     */
    private void createEnumType() {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TYPE roomtype AS ENUM ('WORKSPACE', 'MEETING_ROOM')");
            System.out.println("Enum type 'roomtype' created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create enum type", e);
        }
    }

    /**
     * Drop enum type 'roomtype' from the database.
     */
    private void dropEnumType() {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TYPE roomtype");
            System.out.println("Enum type 'roomtype' dropped successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop enum type", e);
        }
    }

    /**
     * Create necessary tables in the database.
     */
    private void createTables() {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE users (id SERIAL PRIMARY KEY, username VARCHAR(255), password VARCHAR(255))");
            statement.execute("CREATE TABLE rooms (id SERIAL PRIMARY KEY, room_name VARCHAR(255), room_type roomtype)");
            statement.execute("CREATE TABLE bookings (id SERIAL PRIMARY KEY, user_id INTEGER, room_id INTEGER, start_time TIMESTAMP, end_time TIMESTAMP)");
            System.out.println("Tables 'users', 'rooms', and 'bookings' created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tables", e);
        }
    }

    /**
     * Drop tables from the database.
     */
    private void dropTables() {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE bookings");
            statement.execute("DROP TABLE rooms");
            statement.execute("DROP TABLE users");
            System.out.println("Tables 'bookings', 'rooms', and 'users' dropped successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop tables", e);
        }
    }

    /**
     * Test the method findByRoomAndTime in BookingRepository.
     * Steps:
     * 1. Create a user and a room.
     * 2. Create a booking with the user and room.
     * 3. Save the user and room to the database.
     * 4. Save the booking to the database using BookingRepository.
     * 5. Retrieve the booking by room and start time.
     * 6. Verify the retrieved booking matches the saved booking.
     */
    @Test
    public void testFindByRoomAndTime() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);

        Optional<Booking> foundBooking = bookingRepository.findByRoomAndTime(startTime, room);

        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get().getUser().getUsername()).isEqualTo("testUser");
        assertThat(foundBooking.get().getRoom().getName()).isEqualTo("Room1");
        assertThat(foundBooking.get().getStartTime()).isEqualTo(startTime);
        assertThat(foundBooking.get().getEndTime()).isEqualTo(endTime);
    }

    /**
     * Test the method save in BookingRepository.
     * Steps:
     * 1. Create a user and a room.
     * 2. Create a booking with the user and room.
     * 3. Save the user and room to the database.
     * 4. Save the booking to the database using BookingRepository.
     * 5. Retrieve the booking by room and start time.
     * 6. Verify the retrieved booking matches the saved booking.
     */
    @Test
    public void testSave() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);

        Optional<Booking> foundBooking = bookingRepository.findByRoomAndTime(startTime, room);

        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get().getUser().getUsername()).isEqualTo("testUser");
        assertThat(foundBooking.get().getRoom().getName()).isEqualTo("Room1");
        assertThat(foundBooking.get().getStartTime()).isEqualTo(startTime);
        assertThat(foundBooking.get().getEndTime()).isEqualTo(endTime);
    }

    /**
     * Test the method update in BookingRepository.
     * Steps:
     * 1. Create a user and a room.
     * 2. Create an old booking with the user and room.
     * 3. Create a new booking with the same user and room but different times.
     * 4. Save the user and room to the database.
     * 5. Save the old booking to the database using BookingRepository.
     * 6. Update the old booking to the new booking using BookingRepository.
     * 7. Retrieve the booking by the new room and start time.
     * 8. Verify the retrieved booking matches the updated booking.
     */
    @Test
    public void testUpdate() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking oldBooking = new Booking(user, room, startTime, endTime);
        Booking newBooking = new Booking(user, room, startTime.plusDays(1), endTime.plusDays(1));
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(oldBooking);
        bookingRepository.update(oldBooking, newBooking);

        Optional<Booking> foundBooking = bookingRepository.findByRoomAndTime(newBooking.getStartTime(), room);

        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get().getUser().getUsername()).isEqualTo("testUser");
        assertThat(foundBooking.get().getRoom().getName()).isEqualTo("Room1");
        assertThat(foundBooking.get().getStartTime()).isEqualTo(newBooking.getStartTime());
        assertThat(foundBooking.get().getEndTime()).isEqualTo(newBooking.getEndTime());
    }

    /**
     * Test the method delete in BookingRepository.
     * Steps:
     * 1. Create a user and a room.
     * 2. Create a booking with the user and room
     * 3. Save the user and room to the database.
     * 4. Save the booking to the database using BookingRepository.
     * 5. Delete the booking from the database using BookingRepository.
     * 6. Retrieve the booking by room and start time.
     * 7. Verify the retrieved booking is not present.
     */
    @Test
    public void testDelete() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);
        bookingRepository.delete(booking);
        Optional<Booking> foundBooking = bookingRepository.findByRoomAndTime(startTime, room);

        assertThat(foundBooking).isNotPresent();
    }

    /**
     * Test the method findByRoom in BookingRepository.
     * Steps:
     * 1. Create a user and a room.
     * 2. Create a booking with the user and room.
     * 3. Save the user and room to the database.
     * 4. Save the booking to the database using BookingRepository.
     * 5. Retrieve the bookings by room.
     * 6. Verify the retrieved bookings match the saved booking.
     */
    @Test
    public void testFindByRoom() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findByRoom(room);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getUser().getUsername()).isEqualTo("testUser");
        assertThat(bookings.get(0).getRoom().getName()).isEqualTo("Room1");
    }

    /**
     * Test the method findByUser in BookingRepository.
     * Steps:
     * 1. Create a user and a room.
     * 2. Create a booking with the user and room.
     * 3. Save the user and room to the database.
     * 4. Save the booking to the database using BookingRepository.
     * 5. Retrieve the bookings by user.
     * 6. Verify the retrieved bookings match the saved booking.
     */
    @Test
    public void testFindByUser() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findByUser(user);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getRoom().getName()).isEqualTo("Room1");
        assertThat(bookings.get(0).getUser().getUsername()).isEqualTo("testUser");
    }

    /**
     * Test the method findByDate in BookingRepository.
     * Steps:
     * 1. Create a user and a room.
     * 2. Create a booking with the user and room.
     * 3. Save the user and room to the database.
     * 4. Save the booking to the database using BookingRepository.
     * 5. Retrieve the bookings by date.
     * 6. Verify the retrieved bookings match the saved booking.
     */
    @Test
    public void testFindByDate() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByDate(startTime);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getRoom().getName()).isEqualTo("Room1");
        assertThat(bookings.get(0).getUser().getUsername()).isEqualTo("testUser");
        assertThat(bookings.get(0).getStartTime()).isEqualTo(startTime);
        assertThat(bookings.get(0).getEndTime()).isEqualTo(endTime);
    }

    /**
     * Save a user to the database.
     *
     * @param user The user to be saved.
     */
    private void saveUser(User user) {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    /**
     * Save a room to the database.
     *
     * @param room The room to be saved.
     */
    private void saveRoom(Room room) {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO rooms (room_name, room_type) VALUES (?, ?::roomtype)")) {
            preparedStatement.setString(1, room.getName());
            preparedStatement.setString(2, room.getType().name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save room", e);
        }
    }
}