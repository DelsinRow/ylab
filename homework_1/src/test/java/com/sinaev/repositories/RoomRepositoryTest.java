//package com.sinaev.repositories;
//
//import com.sinaev.models.entities.Room;
//import com.sinaev.models.enums.RoomType;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//
///**
// * Test class for RoomRepository.
// * This class uses Testcontainers to start a PostgreSQL container for testing.
// */
//@Testcontainers
//public class RoomRepositoryTest {
//
//    /**
//     * PostgreSQL container for testing.
//     */
//    @Container
//    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
//            .withDatabaseName("test")
//            .withUsername("test")
//            .withPassword("test");
//
//    private RoomRepository roomRepository;
//
//    /**
//     * Start the PostgreSQL container before all tests.
//     */
//    @BeforeAll
//    public static void setUpContainer() {
//        postgresContainer.start();
//    }
//
//    /**
//     * Initialize the RoomRepository and create necessary database structures before each test.
//     */
//    @BeforeEach
//    public void init() {
//        roomRepository = Mockito.spy(new RoomRepository(postgresContainer.getJdbcUrl(),
//                postgresContainer.getUsername(), postgresContainer.getPassword()));
//        doNothing().when(roomRepository).changeSearchPath(any(Connection.class));
//        createEnumType();
//        createTable();
//    }
//
//    /**
//     * Drop database structures after each test.
//     */
//    @AfterEach
//    public void tearDown() {
//        dropTable();
//        dropEnumType();
//    }
//
//    /**
//     * Create enum type 'roomtype' in the database.
//     */
//    private void createEnumType() {
//        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
//                postgresContainer.getUsername(), postgresContainer.getPassword());
//             Statement statement = connection.createStatement()) {
//            statement.execute("CREATE TYPE roomtype AS ENUM ('WORKSPACE', 'MEETING_ROOM')");
//            System.out.println("Enum type 'roomtype' created successfully.");
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to create enum type", e);
//        }
//    }
//
//    /**
//     * Drop enum type 'roomtype' from the database.
//     */
//    private void dropEnumType() {
//        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
//                postgresContainer.getUsername(), postgresContainer.getPassword());
//             Statement statement = connection.createStatement()) {
//            statement.execute("DROP TYPE roomtype");
//            System.out.println("Enum type 'roomtype' dropped successfully.");
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to drop enum type", e);
//        }
//    }
//
//    /**
//     * Create necessary tables in the database.
//     */
//    private void createTable() {
//        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
//                postgresContainer.getUsername(), postgresContainer.getPassword());
//             Statement statement = connection.createStatement()) {
//            statement.execute("CREATE TABLE rooms (id SERIAL PRIMARY KEY, room_name VARCHAR(255), room_type roomtype)");
//            System.out.println("Table 'rooms' created successfully.");
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to create table", e);
//        }
//    }
//
//    /**
//     * Drop tables from the database.
//     */
//    private void dropTable() {
//        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
//                postgresContainer.getUsername(), postgresContainer.getPassword());
//             Statement statement = connection.createStatement()) {
//            statement.execute("DROP TABLE rooms");
//            System.out.println("Table 'rooms' dropped successfully.");
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to drop table", e);
//        }
//    }
//
//    @Test
//    @DisplayName("Should find all rooms")
//    public void testFindAll() {
//        Room room1 = new Room("Room1", RoomType.WORKSPACE);
//        Room room2 = new Room("Room2", RoomType.MEETING_ROOM);
//        roomRepository.save(room1);
//        roomRepository.save(room2);
//
//        List<Room> rooms = roomRepository.findAll();
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(rooms).hasSize(2);
//        softly.assertThat(rooms).extracting(Room::getName).containsExactlyInAnyOrder("Room1", "Room2");
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should find room by name")
//    public void testFindByName() {
//        Room room = new Room("Room1", RoomType.WORKSPACE);
//        roomRepository.save(room);
//
//        Optional<Room> foundRoom = roomRepository.findByName("Room1");
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(foundRoom).isPresent();
//        softly.assertThat(foundRoom.get().getName()).isEqualTo("Room1");
//        softly.assertThat(foundRoom.get().getType()).isEqualTo(RoomType.WORKSPACE);
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should save room")
//    public void testSave() {
//        Room room = new Room("Room1", RoomType.WORKSPACE);
//        roomRepository.save(room);
//
//        Optional<Room> foundRoom = roomRepository.findByName("Room1");
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(foundRoom).isPresent();
//        softly.assertThat(foundRoom.get().getName()).isEqualTo("Room1");
//        softly.assertThat(foundRoom.get().getType()).isEqualTo(RoomType.WORKSPACE);
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should update room")
//    public void testUpdate() {
//        Room oldRoom = new Room("Room1", RoomType.WORKSPACE);
//        Room newRoom = new Room("Room1Updated", RoomType.MEETING_ROOM);
//        roomRepository.save(oldRoom);
//        roomRepository.update(oldRoom, newRoom);
//
//        Optional<Room> foundRoom = roomRepository.findByName("Room1Updated");
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(foundRoom).isPresent();
//        softly.assertThat(foundRoom.get().getName()).isEqualTo("Room1Updated");
//        softly.assertThat(foundRoom.get().getType()).isEqualTo(RoomType.MEETING_ROOM);
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should delete room")
//    public void testDelete() {
//        Room room = new Room("Room1", RoomType.WORKSPACE);
//        roomRepository.save(room);
//        roomRepository.delete(room);
//
//        Optional<Room> foundRoom = roomRepository.findByName("Room1");
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(foundRoom).isNotPresent();
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should check if room exists")
//    public void testExists() {
//        Room room = new Room("Room1", RoomType.WORKSPACE);
//        roomRepository.save(room);
//        boolean exists = roomRepository.exists("Room1");
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(exists).isTrue();
//        softly.assertAll();
//    }
//
//    @Test
//    @DisplayName("Should not find non-existing room by name")
//    public void testNonExistingRoom() {
//        Optional<Room> foundRoom = roomRepository.findByName("NonExistingRoom");
//
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(foundRoom).isNotPresent();
//        softly.assertAll();
//    }
//}