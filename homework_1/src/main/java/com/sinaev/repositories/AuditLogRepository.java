package com.sinaev.repositories;

import com.sinaev.handlers.SQLQueryHandler;
import com.sinaev.models.entities.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class AuditLogRepository {
    private final DataSource dataSource;

    public void save(AuditLog auditLog) {
        String saveSQL = "INSERT INTO audit_log (username, action, timestamp) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(saveSQL)) {

            changeSearchPath(connection);

            preparedStatement.setString(1, auditLog.getUsername());
            preparedStatement.setString(2, auditLog.getAction());
            preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(auditLog.getTimestamp()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Got SQL Exception " + e.getMessage());
        }
    }

    protected void changeSearchPath(Connection connection) {
        SQLQueryHandler handler = new SQLQueryHandler();
        handler.addSearchPathPrivate(connection);
    }
}
