package com.sinaev.repositories;

import com.sinaev.models.entities.Booking;
import com.sinaev.models.entities.Room;
import com.sinaev.models.entities.User;
import com.sinaev.models.enums.RoomType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("Should find booking by room and time")
    public void testFindByRoomAndTime() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);

        Optional<Booking> foundBooking = bookingRepository.findByRoomAndTime(startTime, room.getName());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundBooking).isPresent();
        softly.assertThat(foundBooking.get().getUser().getUsername()).isEqualTo("testUser");
        softly.assertThat(foundBooking.get().getRoom().getName()).isEqualTo("Room1");
        softly.assertThat(foundBooking.get().getStartTime()).isEqualTo(startTime);
        softly.assertThat(foundBooking.get().getEndTime()).isEqualTo(endTime);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should save booking")
    public void testSave() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);

        Optional<Booking> foundBooking = bookingRepository.findByRoomAndTime(startTime, room.getName());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundBooking).isPresent();
        softly.assertThat(foundBooking.get().getUser().getUsername()).isEqualTo("testUser");
        softly.assertThat(foundBooking.get().getRoom().getName()).isEqualTo("Room1");
        softly.assertThat(foundBooking.get().getStartTime()).isEqualTo(startTime);
        softly.assertThat(foundBooking.get().getEndTime()).isEqualTo(endTime);
        softly.assertAll();
    }

    @Test
    @DisplayName("Should update booking")
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

        Optional<Booking> foundBooking = bookingRepository.findByRoomAndTime(newBooking.getStartTime(), room.getName());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundBooking).isPresent();
        softly.assertThat(foundBooking.get().getUser().getUsername()).isEqualTo("testUser");
        softly.assertThat(foundBooking.get().getRoom().getName()).isEqualTo("Room1");
        softly.assertThat(foundBooking.get().getStartTime()).isEqualTo(newBooking.getStartTime());
        softly.assertThat(foundBooking.get().getEndTime()).isEqualTo(newBooking.getEndTime());
        softly.assertAll();
    }

    @Test
    @DisplayName("Should delete booking")
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
        Optional<Booking> foundBooking = bookingRepository.findByRoomAndTime(startTime, room.getName());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundBooking).isNotPresent();
        softly.assertAll();
    }

    @Test
    @DisplayName("Should find bookings by room")
    public void testFindByRoom() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findByRoomName(room.getName());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(bookings).hasSize(1);
        softly.assertThat(bookings.get(0).getUser().getUsername()).isEqualTo("testUser");
        softly.assertThat(bookings.get(0).getRoom().getName()).isEqualTo("Room1");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should find bookings by user")
    public void testFindByUser() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findByUserName(user.getUsername());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(bookings).hasSize(1);
        softly.assertThat(bookings.get(0).getRoom().getName()).isEqualTo("Room1");
        softly.assertThat(bookings.get(0).getUser().getUsername()).isEqualTo("testUser");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should find bookings by date")
    public void testFindByDate() {
        User user = new User("testUser", "testPass");
        Room room = new Room("Room1", RoomType.WORKSPACE);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = new Booking(user, room, startTime, endTime);
        saveUser(user);
        saveRoom(room);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByDate(startTime.toLocalDate());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(bookings).hasSize(1);
        softly.assertThat(bookings.get(0).getRoom().getName()).isEqualTo("Room1");
        softly.assertThat(bookings.get(0).getUser().getUsername()).isEqualTo("testUser");
        softly.assertThat(bookings.get(0).getStartTime()).isEqualTo(startTime);
        softly.assertThat(bookings.get(0).getEndTime()).isEqualTo(endTime);
        softly.assertAll();
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
