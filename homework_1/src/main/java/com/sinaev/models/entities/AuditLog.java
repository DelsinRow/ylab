package com.sinaev.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity class for AuditLog.
 * <p>
 * This class represents an audit log entry, capturing the username, action performed, and the timestamp of the action.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {
    private Long id;
    private String username;
    private String action;
    private LocalDateTime timestamp;

    /**
     * Constructs an AuditLog with the specified username, action, and timestamp.
     *
     * @param username  the username of the user who performed the action
     * @param action    the action performed
     * @param timestamp the time the action was performed
     */
    public AuditLog(String username, String action, LocalDateTime timestamp) {
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
    }

}
