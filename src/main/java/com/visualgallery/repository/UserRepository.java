package com.visualgallery.repository;

import com.visualgallery.config.DatabaseConfig;
import com.visualgallery.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UserRepository - Data access layer for User operations.
 *
 * Repository Pattern implementation for User CRUD operations.
 * Uses Prepared Statements for security.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class UserRepository {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public UserRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the user ID
     * @return User object or null
     */
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by id: " + id, e);
        }
        return null;
    }

    /**
     * Returns all users (for admin management).
     *
     * @return list of all users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all users", e);
        }
        return users;
    }

    /**
     * Returns paginated users list.
     *
     * @param page     page number (1-based)
     * @param pageSize number of items per page
     * @return list of users for the given page
     */
    public List<User> findAllPaginated(int page, int pageSize) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'USER' ORDER BY created_at DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching paginated users", e);
        }
        return users;
    }

    /**
     * Searches for users by keyword (username or full_name).
     *
     * @param keyword the search keyword
     * @return list of matching users
     */
    public List<User> search(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE (username LIKE ? OR full_name LIKE ?) AND is_active = TRUE";
        String pattern = "%" + keyword + "%";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching users: " + keyword, e);
        }
        return users;
    }

    /**
     * Updates a user's profile information.
     *
     * @param user the User object with updated data
     * @return true if update succeeded
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, full_name = ?, bio = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getBio());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user: " + user.getId(), e);
            return false;
        }
    }

    /**
     * Updates a user's password.
     *
     * @param userId         the user's ID
     * @param hashedPassword the new BCrypt-hashed password
     * @return true if update succeeded
     */
    public boolean updatePassword(int userId, String hashedPassword) {
        String sql = "UPDATE users SET password = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating password for user: " + userId, e);
            return false;
        }
    }

    /**
     * Updates a user's profile picture path.
     *
     * @param userId     the user's ID
     * @param picturePath the file path of the new profile picture
     * @return true if update succeeded
     */
    public boolean updateProfilePicture(int userId, String picturePath) {
        String sql = "UPDATE users SET profile_picture = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, picturePath);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating profile picture for user: " + userId, e);
            return false;
        }
    }

    /**
     * Soft-deletes (deactivates) a user account.
     *
     * @param userId the user to deactivate
     * @return true if operation succeeded
     */
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET is_active = FALSE, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deactivating user: " + userId, e);
            return false;
        }
    }

    /**
     * Hard-deletes a user account.
     *
     * @param userId the user to delete
     * @return true if deletion succeeded
     */
    public boolean delete(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user: " + userId, e);
            return false;
        }
    }

    /**
     * Saves a new user (admin-created) to the database.
     *
     * @param user the User to save
     * @return true if save succeeded
     */
    public boolean save(User user) {
        String sql = "INSERT INTO users (username, email, password, role, full_name, bio, is_active, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, TRUE, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole() != null ? user.getRole() : "USER");
            ps.setString(5, user.getFullName());
            ps.setString(6, user.getBio());
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving user: " + user.getUsername(), e);
            return false;
        }
    }

    /**
     * Returns total count of regular users.
     */
    public int countUsers() {
        return countByRole("USER");
    }

    /**
     * Returns total count of admins.
     */
    public int countAdmins() {
        return countByRole("ADMIN");
    }

    private int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting by role: " + role, e);
        }
        return 0;
    }

    /**
     * Returns user statistics (upload, likes, comments).
     *
     * @param userId the user ID
     * @return int[] {totalUploads, totalLikes, totalComments}
     */
    public int[] getUserStats(int userId) {
        int[] stats = {0, 0, 0};
        String sql = "SELECT COUNT(p.id) AS uploads, " +
                     "COALESCE(SUM(p.total_likes), 0) AS likes, " +
                     "COALESCE(SUM(p.total_comments), 0) AS comments " +
                     "FROM posts p WHERE p.user_id = ? AND p.is_deleted = FALSE";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats[0] = rs.getInt("uploads");
                    stats[1] = rs.getInt("likes");
                    stats[2] = rs.getInt("comments");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching user stats: " + userId, e);
        }
        return stats;
    }

    // ==================== MAPPING ====================

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setFullName(rs.getString("full_name"));
        user.setBio(rs.getString("bio"));
        user.setProfilePicture(rs.getString("profile_picture"));
        user.setActive(rs.getBoolean("is_active"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) user.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) user.setUpdatedAt(updatedAt.toLocalDateTime());
        return user;
    }
}
