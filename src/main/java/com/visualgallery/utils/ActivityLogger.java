package com.visualgallery.utils;

import com.visualgallery.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ActivityLogger - Logs user actions to the activity_logs table.
 *
 * OOP: Utility, Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class ActivityLogger {

    private static final Logger LOGGER = Logger.getLogger(ActivityLogger.class.getName());

    // Action constants
    public static final String ACTION_LOGIN    = "LOGIN";
    public static final String ACTION_LOGOUT   = "LOGOUT";
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String ACTION_UPLOAD   = "UPLOAD";
    public static final String ACTION_LIKE     = "LIKE";
    public static final String ACTION_UNLIKE   = "UNLIKE";
    public static final String ACTION_COMMENT  = "COMMENT";
    public static final String ACTION_DELETE   = "DELETE";
    public static final String ACTION_EDIT     = "EDIT";
    public static final String ACTION_EXPORT   = "EXPORT";

    private ActivityLogger() { }

    /**
     * Logs an activity to the database asynchronously.
     *
     * @param userId      the performing user's ID (null for anonymous)
     * @param action      the action performed
     * @param description a description of the action
     */
    public static void log(Integer userId, String action, String description) {
        // Run in background thread to not block UI
        Thread logThread = new Thread(() -> {
            String sql = "INSERT INTO activity_logs (user_id, action, description, created_at) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConfig.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                if (userId != null) ps.setInt(1, userId);
                else ps.setNull(1, Types.INTEGER);
                ps.setString(2, action);
                ps.setString(3, description);
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                ps.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to log activity: " + action, e);
            }
        });
        logThread.setDaemon(true);
        logThread.start();
    }
}
