package com.visualgallery.repository;

import com.visualgallery.config.DatabaseConfig;
import com.visualgallery.model.Comment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CommentRepository - Data access for Comment operations.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class CommentRepository {

    private static final Logger LOGGER = Logger.getLogger(CommentRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public CommentRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public int save(Comment comment) {
        String sql = "INSERT INTO comments (post_id, user_id, comment_text, is_deleted, created_at, updated_at) " +
                     "VALUES (?, ?, ?, FALSE, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, comment.getPostId());
            ps.setInt(2, comment.getUserId());
            ps.setString(3, comment.getCommentText());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving comment", e);
        }
        return -1;
    }

    public List<Comment> findByPostId(int postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username, u.full_name, u.profile_picture " +
                     "FROM comments c JOIN users u ON c.user_id = u.id " +
                     "WHERE c.post_id = ? AND c.is_deleted = FALSE " +
                     "ORDER BY c.created_at DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) comments.add(mapResultSetToComment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding comments by post: " + postId, e);
        }
        return comments;
    }

    public boolean update(int commentId, String newText) {
        String sql = "UPDATE comments SET comment_text = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newText);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, commentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating comment: " + commentId, e);
            return false;
        }
    }

    public boolean delete(int commentId) {
        String sql = "UPDATE comments SET is_deleted = TRUE, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, commentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting comment: " + commentId, e);
            return false;
        }
    }

    public List<Comment> findAllForAdmin() {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username, u.full_name, u.profile_picture " +
                     "FROM comments c JOIN users u ON c.user_id = u.id " +
                     "WHERE c.is_deleted = FALSE ORDER BY c.created_at DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) comments.add(mapResultSetToComment(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all comments for admin", e);
        }
        return comments;
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM comments WHERE is_deleted = FALSE";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting comments", e);
        }
        return 0;
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment c = new Comment();
        c.setId(rs.getInt("id"));
        c.setPostId(rs.getInt("post_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setCommentText(rs.getString("comment_text"));
        c.setDeleted(rs.getBoolean("is_deleted"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) c.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) c.setUpdatedAt(updatedAt.toLocalDateTime());
        try {
            c.setCommenterUsername(rs.getString("username"));
            c.setCommenterFullName(rs.getString("full_name"));
            c.setCommenterProfilePicture(rs.getString("profile_picture"));
        } catch (SQLException ignored) {}
        return c;
    }
}
