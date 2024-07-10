package com.sinaev.repositories;

import com.sinaev.models.entities.AuditLog;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

/**
 * Test class for AuditLogRepository.
 * This class uses Testcontainers to start a PostgreSQL container for testing.
 */
@Testcontainers
public class AuditLogRepositoryTest {

    /**
     * PostgreSQL container for testing.
     */
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    private AuditLogRepository auditLogRepository;

    /**
     * Start the PostgreSQL container before all tests.
     */
    @BeforeAll
    public static void setUpContainer() {
        postgresContainer.start();
    }

    /**
     * Initialize the AuditLogRepository and create necessary database structures before each test.
     */
    @BeforeEach
    public void init() {
        auditLogRepository = Mockito.spy(new AuditLogRepository(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword()));
        doNothing().when(auditLogRepository).changeSearchPath(any(Connection.class));
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
     * Create necessary tables in the database.
     */
    private void createTable() {
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE audit_log (id SERIAL PRIMARY KEY, username VARCHAR(255), action VARCHAR(255), timestamp TIMESTAMP)");
            System.out.println("Table 'audit_log' created successfully.");
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
            statement.execute("DROP TABLE audit_log");
            System.out.println("Table 'audit_log' dropped successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop table", e);
        }
    }

    @Test
    @DisplayName("Should save audit log")
    public void testSave() {
        AuditLog auditLog = new AuditLog("testUser", "testAction", LocalDateTime.now());
        auditLogRepository.save(auditLog);

        SoftAssertions softly = new SoftAssertions();
        try (Connection connection = DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(), postgresContainer.getPassword());
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM audit_log WHERE username = ?")) {
            preparedStatement.setString(1, auditLog.getUsername());
            try (var resultSet = preparedStatement.executeQuery()) {
                softly.assertThat(resultSet.next()).isTrue();
                softly.assertThat(resultSet.getString("username")).isEqualTo("testUser");
                softly.assertThat(resultSet.getString("action")).isEqualTo("testAction");
                softly.assertThat(resultSet.getTimestamp("timestamp").toLocalDateTime()).isEqualTo(auditLog.getTimestamp());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to verify audit log", e);
        }
        softly.assertAll();
    }
}
