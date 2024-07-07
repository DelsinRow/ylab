package com.sinaev.repositories;

import com.sinaev.models.entities.User;
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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

/**
 * Test class for UserRepository.
 * This class uses Testcontainers to start a PostgreSQL container for testing.
 */
@Testcontainers
public class UserRepositoryTest {

    /**
     * PostgreSQL container for testing.
     */
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    private UserRepository userRepository;

    /**
     * Start the PostgreSQL container before all tests.
     */
    @BeforeAll
    public static void setUp() {
        postgresContainer.start();
    }

    /**
     * Initialize the UserRepository and create necessary database structures before each test.
     */
    @BeforeEach
    public void init() {
        userRepository = Mockito.spy(new UserRepository(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword()));
        doNothing().when(userRepository).changeSearchPath(any(Connection.class));
        createTable();
    }

    /**
     * Drop database structures after each test.
     */
    @AfterEach
    public void tearDown() {
        dropTable();
    }

    /**
     * Create the 'users' table in the database.
     */
    private void createTable() {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE users (id SERIAL PRIMARY KEY, username VARCHAR(255), password VARCHAR(255), is_admin BOOLEAN)");
            System.out.println("Table 'users' created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table", e);
        }
    }

    /**
     * Drop the 'users' table from the database.
     */
    private void dropTable() {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE users");
            System.out.println("Table 'users' dropped successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop table", e);
        }
    }


    @Test
    @DisplayName("Should find user by username")
    public void testFindByUsername() {
        User user = new User("testUser", "testPass", true);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testUser");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundUser).isPresent();
        softly.assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
        softly.assertThat(foundUser.get().getPassword()).isEqualTo("testPass");
        softly.assertThat(foundUser.get().isAdmin()).isTrue();
        softly.assertAll();
    }

    @Test
    @DisplayName("Should save user")
    public void testSave() {
        User user = new User("testUser", "testPass", true);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testUser");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundUser).isPresent();
        softly.assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
        softly.assertThat(foundUser.get().getPassword()).isEqualTo("testPass");
        softly.assertThat(foundUser.get().isAdmin()).isTrue();
        softly.assertAll();
    }

    @Test
    @DisplayName("Should check if username exists")
    public void testExistsByUsername() {
        User user = new User("testUser", "testPass", true);
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("testUser");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(exists).isTrue();
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not find non-existing user by username")
    public void testNonExistingUser() {
        Optional<User> foundUser = userRepository.findByUsername("nonExistingUser");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundUser).isNotPresent();
        softly.assertAll();
    }
}