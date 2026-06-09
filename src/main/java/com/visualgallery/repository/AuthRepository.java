package com.visualgallery.repository;

import com.visualgallery.config.DatabaseConfig;
import com.visualgallery.model.User;
import com.visualgallery.model.Admin;
import com.visualgallery.model.Account;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AuthRepository - Data access for authentication operations.
 *
 * Implements Repository Pattern for authentication-related DB queries.
 * Uses Prepared Statements for SQL Injection prevention.
 *
 * OOP: Encapsulation, Repository Pattern
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class AuthRepository {

    private static final Logger LOGGER = Logger.getLogger(AuthRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public AuthRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Finds an account by email address.
     * Returns User or Admin based on role field.
     *
     * @param email the email to search for
     * @return Account object (User or Admin), or null if not found
     */
    public Account findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND is_active = TRUE";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding account by email: " + email, e);
        }
        return null;
    }

    /**
     * Finds an account by username.
     *
     * @param username the username to search for
     * @return Account object or null
     */
    public Account findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = TRUE";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding account by username: " + username, e);
        }
        return null;
    }

    /**
     * Registers a new user into the database.
     *
     * @param username       the desired username
     * @param email          the email address
     * @param hashedPassword the BCrypt-hashed password
     * @param fullName       the user's full name
     * @return true if registration succeeded
     */
    public boolean registerUser(String username, String email,
                                 String hashedPassword, String fullName) {
        String sql = "INSERT INTO users (username, email, password, role, full_name, is_active, created_at, updated_at) " +
                     "VALUES (?, ?, ?, 'USER', ?, TRUE, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hashedPassword);
            ps.setString(4, fullName);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user: " + username, e);
            return false;
        }
    }

    /**
     * Checks if a username already exists.
     *
     * @param username the username to check
     * @return true if username is taken
     */
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking username existence: " + username, e);
        }
        return false;
    }

    /**
     * Checks if an email already exists.
     *
     * @param email the email to check
     * @return true if email is taken
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email existence: " + email, e);
        }
        return false;
    }

    /**
     * Maps a ResultSet row to the appropriate Account subclass.
     *
     * @param rs the ResultSet positioned at a valid row
     * @return User or Admin instance
     * @throws SQLException on DB error
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        Account account;

        if ("ADMIN".equalsIgnoreCase(role)) {
            Admin admin = new Admin();
            admin.setFullName(rs.getString("full_name"));
            account = admin;
        } else {
            User user = new User();
            user.setFullName(rs.getString("full_name"));
            account = user;
        }

        account.setId(rs.getInt("id"));
        account.setUsername(rs.getString("username"));
        account.setEmail(rs.getString("email"));
        account.setPassword(rs.getString("password"));
        account.setRole(role);
        account.setBio(rs.getString("bio"));
        account.setProfilePicture(rs.getString("profile_picture"));
        account.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) account.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) account.setUpdatedAt(updatedAt.toLocalDateTime());

        return account;
    }
}
