package com.sinaev.repositories;

import com.sinaev.models.entities.AuditLog;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Testcontainers
class AuditLogRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("password");

    private static DataSource dataSource;
    private AuditLogRepository auditLogRepository;

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
        auditLogRepository = new AuditLogRepository(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("CREATE SCHEMA IF NOT EXISTS entity_schema");
            statement.execute("SET search_path TO entity_schema");
            statement.execute("CREATE TABLE IF NOT EXISTS audit_log (id SERIAL PRIMARY KEY, username VARCHAR(255), action VARCHAR(255), timestamp TIMESTAMP)");
            statement.execute("TRUNCATE TABLE audit_log");
        }
    }

    @Test
    @DisplayName("Test save audit log")
    void testSaveAuditLog() throws SQLException {
        AuditLog auditLog = new AuditLog("testUser", "testAction", LocalDateTime.now());
        auditLogRepository.save(auditLog);

        List<AuditLog> auditLogs = fetchAllAuditLogs();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(auditLogs).hasSize(1);
        softly.assertThat(auditLogs.get(0).getUsername()).isEqualTo("testUser");
        softly.assertThat(auditLogs.get(0).getAction()).isEqualTo("testAction");
        softly.assertThat(auditLogs.get(0).getTimestamp()).isEqualTo(auditLog.getTimestamp());
        softly.assertAll();
    }

    private List<AuditLog> fetchAllAuditLogs() throws SQLException {
        List<AuditLog> auditLogs = new ArrayList<>();
        String fetchSQL = "SELECT username, action, timestamp FROM entity_schema.audit_log";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(fetchSQL)) {

            while (resultSet.next()) {
                AuditLog auditLog = new AuditLog(
                        resultSet.getString("username"),
                        resultSet.getString("action"),
                        resultSet.getTimestamp("timestamp").toLocalDateTime()
                );
                auditLogs.add(auditLog);
            }
        }
        return auditLogs;
    }
}
