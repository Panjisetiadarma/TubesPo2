package com.visualgallery.repository;

import com.visualgallery.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LikeRepository - Data access for Like/Unlike operations.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class LikeRepository {

    private static final Logger LOGGER = Logger.getLogger(LikeRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public LikeRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Adds a like for a user on a post.
     *
     * @param postId the post ID
     * @param userId the user ID
     * @return true if like was added
     */
    public boolean like(int postId, int userId) {
        String sql = "INSERT IGNORE INTO likes (post_id, user_id, created_at) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding like: postId=" + postId + ", userId=" + userId, e);
            return false;
        }
    }

    /**
     * Removes a like for a user on a post.
     *
     * @param postId the post ID
     * @param userId the user ID
     * @return true if like was removed
     */
    public boolean unlike(int postId, int userId) {
        String sql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing like: postId=" + postId + ", userId=" + userId, e);
            return false;
        }
    }

    /**
     * Checks if a user has liked a post.
     *
     * @param postId the post ID
     * @param userId the user ID
     * @return true if user has liked this post
     */
    public boolean isLikedBy(int postId, int userId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE post_id = ? AND user_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking like status", e);
        }
        return false;
    }

    /**
     * Returns the total number of likes for a post.
     *
     * @param postId the post ID
     */
    public int getLikeCount(int postId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE post_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting like count for post: " + postId, e);
        }
        return 0;
    }

    /**
     * Returns total likes across all posts.
     */
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM likes";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting total likes", e);
        }
        return 0;
    }
}
