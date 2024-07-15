package com.sinaev.repositories;

import com.sinaev.models.entities.Booking;
import com.sinaev.models.entities.Room;
import com.sinaev.models.entities.User;
import com.sinaev.models.enums.RoomType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class BookingRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("password");

    private static DataSource dataSource;
    private BookingRepository bookingRepository;
    private RoomRepository roomRepository;
    private UserRepository userRepository;

    @BeforeAll
    static void setUpDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(postgreSQLContainer.getJdbcUrl());
        ds.setUser(postgreSQLContainer.getUsername());
        ds.setPassword(postgreSQLContainer.getPassword());
        dataSource = ds;
    }

    @BeforeEach
    void setUp() throws SQLException {
        bookingRepository = new BookingRepository(dataSource);
        roomRepository = new RoomRepository(dataSource);
        userRepository = new UserRepository(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("CREATE SCHEMA IF NOT EXISTS entity_schema");
            statement.execute("SET search_path TO entity_schema");
            statement.execute("""
                    DO $$
                    BEGIN
                        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'roomtype') THEN
                            CREATE TYPE roomtype AS ENUM ('WORKSPACE', 'MEETING_ROOM');
                        END IF;
                    END $$;
                    """);
            statement.execute("CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, username VARCHAR(255), password VARCHAR(255), is_admin BOOLEAN)");
            statement.execute("CREATE TABLE IF NOT EXISTS rooms (id SERIAL PRIMARY KEY, room_name VARCHAR(255), room_type VARCHAR(50))");
            statement.execute("CREATE TABLE IF NOT EXISTS bookings (user_id BIGINT, room_id BIGINT, start_time TIMESTAMP, end_time TIMESTAMP, PRIMARY KEY (user_id, room_id, start_time))");
            statement.execute("TRUNCATE TABLE bookings, users, rooms");
        }
    }

    @Test
    @DisplayName("Test save booking")
    void testSaveBooking() throws SQLException {
        User user = new User("testUser", "testPassword", false);
        Room room = new Room("Meeting Room", RoomType.MEETING_ROOM);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);

        userRepository.save(user);
        roomRepository.save(room);

        Booking booking = new Booking(user, room, startTime, endTime);
        bookingRepository.save(booking);

        Optional<Booking> foundBooking = bookingRepository.findByRoomAndTime("Meeting Room", startTime);
        assertTrue(foundBooking.isPresent());
        assertEquals("testUser", foundBooking.get().getUser().getUsername());
        assertEquals("Meeting Room", foundBooking.get().getRoom().getName());
    }

    @Test
    @DisplayName("Test find all bookings")
    void testFindAllBookings() throws SQLException {
        User user = new User("testUser", "testPassword", false);
        Room room = new Room("Meeting Room", RoomType.MEETING_ROOM);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);

        userRepository.save(user);
        roomRepository.save(room);

        Booking booking1 = new Booking(user, room, startTime, endTime);
        Booking booking2 = new Booking(user, room, startTime.plusDays(1), endTime.plusDays(1));
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findAll();
        assertEquals(2, bookings.size());
    }

    @Test
    @DisplayName("Test update booking")
    void testUpdateBooking() throws SQLException {
        User user = new User("testUser", "testPassword", false);
        Room room = new Room("Meeting Room", RoomType.MEETING_ROOM);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);

        userRepository.save(user);
        roomRepository.save(room);

        Booking oldBooking = new Booking(user, room, startTime, endTime);
        bookingRepository.save(oldBooking);

        Room newRoom = new Room("Updated Room", RoomType.WORKSPACE);
        roomRepository.save(newRoom);

        Booking newBooking = new Booking(user, newRoom, startTime, endTime.plusHours(1));
        bookingRepository.update(oldBooking, newBooking);

        Optional<Booking> updatedBooking = bookingRepository.findByRoomAndTime("Updated Room", startTime);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(updatedBooking).isPresent();
        softly.assertThat(updatedBooking.get().getRoom().getName()).isEqualTo("Updated Room");
        softly.assertThat(updatedBooking.get().getEndTime()).isEqualTo(endTime.plusHours(1));
        softly.assertAll();
    }

    @Test
    @DisplayName("Test delete booking")
    void testDeleteBooking() throws SQLException {
        User user = new User("testUser", "testPassword", false);
        Room room = new Room("Meeting Room", RoomType.MEETING_ROOM);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);

        userRepository.save(user);
        roomRepository.save(room);

        Booking booking = new Booking(user, room, startTime, endTime);
        bookingRepository.save(booking);
        bookingRepository.delete(booking);

        Optional<Booking> foundBooking = bookingRepository.findByRoomAndTime("Meeting Room", startTime);
        assertFalse(foundBooking.isPresent());
    }

    @Test
    @DisplayName("Test find bookings by date")
    void testFindBookingsByDate() throws SQLException {
        User user = new User("testUser", "testPassword", false);
        Room room = new Room("Meeting Room", RoomType.MEETING_ROOM);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);

        userRepository.save(user);
        roomRepository.save(room);

        Booking booking = new Booking(user, room, startTime, endTime);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByDate(LocalDate.now());
        assertEquals(1, bookings.size());
    }
}