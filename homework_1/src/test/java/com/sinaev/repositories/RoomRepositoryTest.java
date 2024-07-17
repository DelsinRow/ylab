package com.sinaev.repositories;

import com.sinaev.models.entities.Room;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class RoomRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("password");

    private static DataSource dataSource;
    private RoomRepository roomRepository;

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
        roomRepository = new RoomRepository(dataSource);
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
            statement.execute("CREATE TABLE IF NOT EXISTS rooms (room_name VARCHAR(255) PRIMARY KEY, room_type VARCHAR(50))");
            statement.execute("TRUNCATE TABLE rooms");
        }
    }

    @Test
    @DisplayName("Test save room")
    void testSaveRoom() {
        Room room = new Room("Meeting Room", RoomType.MEETING_ROOM);
        roomRepository.save(room);

        assertTrue(roomRepository.exists("Meeting Room"));
    }

    @Test
    @DisplayName("Test find room by name")
    void testFindByName() {
        Room room = new Room("Meeting Room", RoomType.MEETING_ROOM);
        roomRepository.save(room);

        Optional<Room> foundRoom = roomRepository.findByName("Meeting Room");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundRoom).isPresent();
        softly.assertThat(foundRoom.get().getName()).isEqualTo("Meeting Room");
        softly.assertThat(foundRoom.get().getType()).isEqualTo(RoomType.MEETING_ROOM);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test room exists by name")
    void testExistsByName() {
        Room room = new Room("Meeting Room", RoomType.MEETING_ROOM);
        roomRepository.save(room);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(roomRepository.exists("Meeting Room")).isTrue();
        softly.assertThat(roomRepository.exists("NonExistingRoom")).isFalse();
        softly.assertAll();
    }

    @Test
    @DisplayName("Test find all rooms")
    void testFindAll() {
        Room room1 = new Room("Meeting Room", RoomType.MEETING_ROOM);
        Room room2 = new Room("Workspace Room", RoomType.WORKSPACE);
        roomRepository.save(room1);
        roomRepository.save(room2);

        List<Room> rooms = roomRepository.findAll();
        assertEquals(2, rooms.size());
    }

    @Test
    @DisplayName("Test update room")
    void testUpdateRoom() {
        Room oldRoom = new Room("Meeting Room", RoomType.MEETING_ROOM);
        Room newRoom = new Room("Updated Room", RoomType.WORKSPACE);
        roomRepository.save(oldRoom);
        roomRepository.update(oldRoom, newRoom);

        Optional<Room> updatedRoom = roomRepository.findByName("Updated Room");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(updatedRoom).isPresent();
        softly.assertThat(updatedRoom.get().getName()).isEqualTo("Updated Room");
        softly.assertThat(updatedRoom.get().getType()).isEqualTo(RoomType.WORKSPACE);
        softly.assertAll();
    }

    @Test
    @DisplayName("Test delete room")
    void testDeleteRoom() {
        Room room = new Room("Meeting Room", RoomType.MEETING_ROOM);
        roomRepository.save(room);
        roomRepository.delete(room);

        assertFalse(roomRepository.exists("Meeting Room"));
    }
}
