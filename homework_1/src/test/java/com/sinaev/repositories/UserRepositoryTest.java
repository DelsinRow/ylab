package com.sinaev.repositories;

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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    /**
     * Test the method findByUsername in UserRepository.
     * Steps:
     * 1. Create a user.
     * 2. Save the user to the database using UserRepository.
     * 3. Retrieve the user by username using UserRepository.
     * 4. Verify that the retrieved user matches the saved user.
     */
    @Test
    public void testFindByUsername() {
        User user = new User("testUser", "testPass", true);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testUser");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
        assertThat(foundUser.get().getPassword()).isEqualTo("testPass");
        assertThat(foundUser.get().isAdmin()).isTrue();
    }

    /**
     * Test the method save in UserRepository.
     * Steps:
     * 1. Create a user.
     * 2. Save the user to the database using UserRepository.
     * 3. Retrieve the user by username using UserRepository.
     * 4. Verify that the retrieved user matches the saved user.
     */
    @Test
    public void testSave() {
        User user = new User("testUser", "testPass", true);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testUser");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
        assertThat(foundUser.get().getPassword()).isEqualTo("testPass");
        assertThat(foundUser.get().isAdmin()).isTrue();
    }

    /**
     * Test the method existsByUsername in UserRepository.
     * Steps:
     * 1. Create a user.
     * 2. Save the user to the database using UserRepository.
     * 3. Check if the user exists by username using UserRepository.
     * 4. Verify that the user exists.
     */
    @Test
    public void testExistsByUsername() {
        User user = new User("testUser", "testPass", true);
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("testUser");

        assertThat(exists).isTrue();
    }

    /**
     * Test the method findByUsername in UserRepository for a non-existing user.
     * Steps:
     * 1. Attempt to retrieve a user by a non-existing username using UserRepository.
     * 2. Verify that the retrieved user is not present.
     */
    @Test
    public void testNonExistingUser() {
        Optional<User> foundUser = userRepository.findByUsername("nonExistingUser");

        assertThat(foundUser).isNotPresent();
    }
}