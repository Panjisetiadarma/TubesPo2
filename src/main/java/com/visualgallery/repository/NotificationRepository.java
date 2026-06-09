package com.visualgallery.repository;

import com.visualgallery.config.DatabaseConfig;
import com.visualgallery.model.Notification;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NotificationRepository - Data access for Notification operations.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class NotificationRepository {

    private static final Logger LOGGER = Logger.getLogger(NotificationRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public NotificationRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public int save(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, sender_id, post_id, message, type, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, 'UNREAD', ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, notification.getUserId());
            if (notification.getSenderId() != null) ps.setInt(2, notification.getSenderId());
            else ps.setNull(2, Types.INTEGER);
            if (notification.getPostId() != null) ps.setInt(3, notification.getPostId());
            else ps.setNull(3, Types.INTEGER);
            ps.setString(4, notification.getMessage());
            ps.setString(5, notification.getType().name());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving notification", e);
        }
        return -1;
    }

    public List<Notification> findByUserId(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.*, u.username AS sender_username, u.profile_picture AS sender_pic, " +
                     "p.title AS post_title " +
                     "FROM notifications n " +
                     "LEFT JOIN users u ON n.sender_id = u.id " +
                     "LEFT JOIN posts p ON n.post_id = p.id " +
                     "WHERE n.user_id = ? ORDER BY n.created_at DESC LIMIT 50";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToNotification(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding notifications for user: " + userId, e);
        }
        return list;
    }

    public int countUnread(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND status = 'UNREAD'";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting unread notifications", e);
        }
        return 0;
    }

    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET status = 'READ' WHERE user_id = ? AND status = 'UNREAD'";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking notifications as read for user: " + userId, e);
            return false;
        }
    }

    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET status = 'READ' WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking notification as read: " + notificationId, e);
            return false;
        }
    }

    public List<Notification> findAllForAdmin() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.*, u.username AS sender_username, u.profile_picture AS sender_pic, " +
                     "p.title AS post_title FROM notifications n " +
                     "LEFT JOIN users u ON n.sender_id = u.id " +
                     "LEFT JOIN posts p ON n.post_id = p.id " +
                     "ORDER BY n.created_at DESC LIMIT 100";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultSetToNotification(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching notifications for admin", e);
        }
        return list;
    }

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        int senderId = rs.getInt("sender_id");
        if (!rs.wasNull()) n.setSenderId(senderId);
        int postId = rs.getInt("post_id");
        if (!rs.wasNull()) n.setPostId(postId);
        n.setMessage(rs.getString("message"));
        String typeStr = rs.getString("type");
        try { n.setType(Notification.Type.valueOf(typeStr)); } catch (Exception e) { n.setType(Notification.Type.SYSTEM); }
        String statusStr = rs.getString("status");
        try { n.setStatus(Notification.Status.valueOf(statusStr)); } catch (Exception e) { n.setStatus(Notification.Status.UNREAD); }
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) n.setCreatedAt(created.toLocalDateTime());
        try { n.setSenderUsername(rs.getString("sender_username")); } catch (SQLException ignored) {}
        try { n.setSenderProfilePicture(rs.getString("sender_pic")); } catch (SQLException ignored) {}
        try { n.setPostTitle(rs.getString("post_title")); } catch (SQLException ignored) {}
        return n;
    }
}
