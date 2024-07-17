package com.sinaev.repositories;

import com.sinaev.models.entities.User;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class UserRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("password");

    private static DataSource dataSource;
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
        userRepository = new UserRepository(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("CREATE SCHEMA IF NOT EXISTS entity_schema");
            statement.execute("SET search_path TO entity_schema");
            statement.execute("CREATE TABLE IF NOT EXISTS users (username VARCHAR(255) PRIMARY KEY, password VARCHAR(255), is_admin BOOLEAN)");
            statement.execute("TRUNCATE TABLE users");
        }
    }

    @Test
    @DisplayName("Test save user")
    void testSaveUser() {
        User user = new User("testUser", "testPassword", false);
        userRepository.save(user);

        assertTrue(userRepository.existsByUsername("testUser"));
    }

    @Test
    @DisplayName("Test find user by username")
    void testFindByUsername() {
        User user = new User("testUser", "testPassword", false);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testUser");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundUser).isPresent();
        softly.assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
        softly.assertThat(foundUser.get().getPassword()).isEqualTo("testPassword");
        softly.assertThat(foundUser.get().isAdmin()).isFalse();
        softly.assertAll();
    }

    @Test
    @DisplayName("Test user exists by username")
    void testExistsByUsername() {
        User user = new User("testUser", "testPassword", false);
        userRepository.save(user);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(userRepository.existsByUsername("testUser")).isTrue();
        softly.assertThat(userRepository.existsByUsername("nonExistingUser")).isFalse();
        softly.assertAll();
    }
}