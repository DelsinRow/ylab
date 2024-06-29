package com.sinaev.repositories;

import com.sinaev.models.Room;
import com.sinaev.models.RoomType;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

/**
 * Test class for RoomRepository.
 * This class uses Testcontainers to start a PostgreSQL container for testing.
 */
@Testcontainers
public class RoomRepositoryTest {

    /**
     * PostgreSQL container for testing.
     */
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    private RoomRepository roomRepository;

    /**
     * Start the PostgreSQL container before all tests.
     */
    @BeforeAll
    public static void setUpContainer() {
        postgresContainer.start();
    }

    /**
     * Initialize the RoomRepository and create necessary database structures before each test.
     */
    @BeforeEach
    public void init() {
        roomRepository = Mockito.spy(new RoomRepository(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword()));
        doNothing().when(roomRepository).changeSearchPath(any(Connection.class));
        createEnumType();
        createTable();
    }

    /**
     * Drop database structures after each test.
     */
    @AfterEach
    public void tearDown() {
        dropTable();
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
    private void createTable() {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE rooms (id SERIAL PRIMARY KEY, room_name VARCHAR(255), room_type roomtype)");
            System.out.println("Table 'rooms' created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table", e);
        }
    }

    /**
     * Drop tables from the database.
     */
    private void dropTable() {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE rooms");
            System.out.println("Table 'rooms' dropped successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop table", e);
        }
    }

    /**
     * Test the method findAll in RoomRepository.
     * Steps:
     * 1. Create two rooms with different types.
     * 2. Save both rooms to the database using RoomRepository.
     * 3. Retrieve all rooms using RoomRepository.
     * 4. Verify that the retrieved rooms match the saved rooms.
     */
    @Test
    public void testFindAll() {
        Room room1 = new Room("Room1", RoomType.WORKSPACE);
        Room room2 = new Room("Room2", RoomType.MEETING_ROOM);
        roomRepository.save(room1);
        roomRepository.save(room2);

        List<Room> rooms = roomRepository.findAll();

        assertThat(rooms).hasSize(2);
        assertThat(rooms).extracting(Room::getName).containsExactlyInAnyOrder("Room1", "Room2");
    }

    /**
     * Test the method findByName in RoomRepository.
     * Steps:
     * 1. Create a room with a specific name and type.
     * 2. Save the room to the database using RoomRepository.
     * 3. Retrieve the room by name using RoomRepository.
     * 4. Verify that the retrieved room matches the saved room.
     */
    @Test
    public void testFindByName() {
        Room room = new Room("Room1", RoomType.WORKSPACE);
        roomRepository.save(room);

        Optional<Room> foundRoom = roomRepository.findByName("Room1");

        assertThat(foundRoom).isPresent();
        assertThat(foundRoom.get().getName()).isEqualTo("Room1");
        assertThat(foundRoom.get().getType()).isEqualTo(RoomType.WORKSPACE);
    }

    /**
     * Test the method save in RoomRepository.
     * Steps:
     * 1. Create a room with a specific name and type.
     * 2. Save the room to the database using RoomRepository.
     * 3. Retrieve the room by name using RoomRepository.
     * 4. Verify that the retrieved room matches the saved room.
     */
    @Test
    public void testSave() {
        Room room = new Room("Room1", RoomType.WORKSPACE);
        roomRepository.save(room);

        Optional<Room> foundRoom = roomRepository.findByName("Room1");

        assertThat(foundRoom).isPresent();
        assertThat(foundRoom.get().getName()).isEqualTo("Room1");
        assertThat(foundRoom.get().getType()).isEqualTo(RoomType.WORKSPACE);
    }

    /**
     * Test the method update in RoomRepository.
     * Steps:
     * 1. Create an old room with a specific name and type.
     * 2. Create a new room with an updated name and type.
     * 3. Save the old room to the database using RoomRepository.
     * 4. Update the old room to the new room using RoomRepository.
     * 5. Retrieve the room by the new name using RoomRepository.
     * 6. Verify that the retrieved room matches the updated room.
     */
    @Test
    public void testUpdate() {
        Room oldRoom = new Room("Room1", RoomType.WORKSPACE);
        Room newRoom = new Room("Room1Updated", RoomType.MEETING_ROOM);
        roomRepository.save(oldRoom);
        roomRepository.update(oldRoom, newRoom);

        Optional<Room> foundRoom = roomRepository.findByName("Room1Updated");

        assertThat(foundRoom).isPresent();
        assertThat(foundRoom.get().getName()).isEqualTo("Room1Updated");
        assertThat(foundRoom.get().getType()).isEqualTo(RoomType.MEETING_ROOM);
    }

    /**
     * Test the method delete in RoomRepository.
     * Steps:
     * 1. Create a room with a specific name and type.
     * 2. Save the room to the database using RoomRepository.
     * 3. Delete the room from the database using RoomRepository.
     * 4. Retrieve the room by name using RoomRepository.
     * 5. Verify that the retrieved room is not present.
     */
    @Test
    public void testDelete() {
        Room room = new Room("Room1", RoomType.WORKSPACE);
        roomRepository.save(room);
        roomRepository.delete(room);

        Optional<Room> foundRoom = roomRepository.findByName("Room1");

        assertThat(foundRoom).isNotPresent();
    }

    /**
     * Test the method exists in RoomRepository.
     * Steps:
     * 1. Create a room with a specific name and type.
     * 2. Save the room to the database using RoomRepository.
     * 3. Check if the room exists in the database using RoomRepository.
     * 4. Verify that the room exists.
     */
    @Test
    public void testExists() {
        Room room = new Room("Room1", RoomType.WORKSPACE);
        roomRepository.save(room);

        boolean exists = roomRepository.exists(room);

        assertThat(exists).isTrue();
    }

    /**
     * Test the method findByName in RoomRepository for a non-existing room.
     * Steps:
     * 1. Attempt to retrieve a room by a non-existing name using RoomRepository.
     * 2. Verify that the retrieved room is not present.
     */
    @Test
    public void testNonExistingRoom() {
        Optional<Room> foundRoom = roomRepository.findByName("NonExistingRoom");

        assertThat(foundRoom).isNotPresent();
    }
}