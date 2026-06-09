package com.visualgallery.repository;

import com.visualgallery.config.DatabaseConfig;
import com.visualgallery.model.Post;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PostRepository - Data access layer for Post operations.
 *
 * Repository Pattern for Post CRUD, search, feed, and admin management.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class PostRepository {

    private static final Logger LOGGER = Logger.getLogger(PostRepository.class.getName());
    private final DatabaseConfig dbConfig;

    public PostRepository() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    /**
     * Saves a new post to the database.
     *
     * @param post the Post to save
     * @return the generated ID, or -1 on failure
     */
    public int save(Post post) {
        String sql = "INSERT INTO posts (user_id, title, caption, media_path, thumbnail_path, " +
                     "media_type, tags, total_likes, total_comments, is_deleted, upload_date, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 0, 0, FALSE, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, post.getUserId());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getCaption());
            ps.setString(4, post.getMediaPath());
            ps.setString(5, post.getThumbnailPath());
            ps.setString(6, post.getMediaType());
            ps.setString(7, post.getTags());
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving post", e);
        }
        return -1;
    }

    /**
     * Finds a post by its ID, joined with user info.
     *
     * @param id the post ID
     * @return Post or null
     */
    public Post findById(int id) {
        String sql = "SELECT p.*, u.username, u.full_name, u.profile_picture " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.id = ? AND p.is_deleted = FALSE";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToPost(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding post by id: " + id, e);
        }
        return null;
    }

    /**
     * Gets all active (non-deleted) posts for the explore feed.
     * Supports sorting: NEWEST, OLDEST, POPULAR.
     *
     * @param sortBy   sort criterion
     * @param page     page number (1-based)
     * @param pageSize items per page
     * @return list of posts
     */
    public List<Post> findFeedPosts(String sortBy, int page, int pageSize) {
        List<Post> posts = new ArrayList<>();
        String orderBy = switch (sortBy.toUpperCase()) {
            case "OLDEST"  -> "p.upload_date ASC";
            case "POPULAR" -> "p.total_likes DESC, p.total_comments DESC";
            default        -> "p.upload_date DESC";
        };
        String sql = "SELECT p.*, u.username, u.full_name, u.profile_picture " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.is_deleted = FALSE " +
                     "ORDER BY " + orderBy + " LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching feed posts", e);
        }
        return posts;
    }

    /**
     * Searches posts by title, tag, username, or category.
     *
     * @param keyword    search term
     * @param filterType "ALL", "PHOTO", or "VIDEO"
     * @return list of matching posts
     */
    public List<Post> search(String keyword, String filterType) {
        List<Post> posts = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT p.*, u.username, u.full_name, u.profile_picture " +
            "FROM posts p JOIN users u ON p.user_id = u.id " +
            "LEFT JOIN post_categories pc ON p.id = pc.post_id " +
            "LEFT JOIN categories c ON pc.category_id = c.id " +
            "WHERE p.is_deleted = FALSE " +
            "AND (p.title LIKE ? OR p.tags LIKE ? OR u.username LIKE ? OR c.category_name LIKE ?) "
        );
        if (!"ALL".equalsIgnoreCase(filterType)) {
            sql.append("AND p.media_type = ? ");
        }
        sql.append("ORDER BY p.upload_date DESC LIMIT 50");

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            if (!"ALL".equalsIgnoreCase(filterType)) {
                ps.setString(5, filterType.toUpperCase());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching posts: " + keyword, e);
        }
        return posts;
    }

    /**
     * Gets all posts by a specific user.
     *
     * @param userId the user's ID
     * @return list of the user's posts
     */
    public List<Post> findByUserId(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name, u.profile_picture " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.user_id = ? AND p.is_deleted = FALSE " +
                     "ORDER BY p.upload_date DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding posts by user: " + userId, e);
        }
        return posts;
    }

    /**
     * Gets posts liked by a specific user.
     *
     * @param userId the user's ID
     * @return list of liked posts
     */
    public List<Post> findLikedByUserId(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name, u.profile_picture " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "JOIN likes l ON p.id = l.post_id " +
                     "WHERE l.user_id = ? AND p.is_deleted = FALSE " +
                     "ORDER BY l.created_at DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding liked posts for user: " + userId, e);
        }
        return posts;
    }

    /**
     * Gets posts saved by a specific user.
     *
     * @param userId the user's ID
     * @return list of saved posts
     */
    public List<Post> findSavedByUserId(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name, u.profile_picture " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "JOIN saved_posts sp ON p.id = sp.post_id " +
                     "WHERE sp.user_id = ? AND p.is_deleted = FALSE " +
                     "ORDER BY sp.created_at DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) posts.add(mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding saved posts for user: " + userId, e);
        }
        return posts;
    }

    /**
     * Updates a post's title, caption, and tags.
     *
     * @param post the post with updated data
     * @return true if update succeeded
     */
    public boolean update(Post post) {
        String sql = "UPDATE posts SET title = ?, caption = ?, tags = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getCaption());
            ps.setString(3, post.getTags());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(5, post.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating post: " + post.getId(), e);
            return false;
        }
    }

    /**
     * Soft-deletes a post (sets is_deleted = TRUE).
     *
     * @param postId the post to delete
     * @return true if operation succeeded
     */
    public boolean softDelete(int postId) {
        String sql = "UPDATE posts SET is_deleted = TRUE, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, postId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error soft-deleting post: " + postId, e);
            return false;
        }
    }

    /**
     * Returns total number of active posts.
     */
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM posts WHERE is_deleted = FALSE";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting posts", e);
        }
        return 0;
    }

    /**
     * Returns count by media type.
     *
     * @param mediaType "PHOTO" or "VIDEO"
     */
    public int countByType(String mediaType) {
        String sql = "SELECT COUNT(*) FROM posts WHERE media_type = ? AND is_deleted = FALSE";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mediaType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting posts by type: " + mediaType, e);
        }
        return 0;
    }

    /**
     * Returns monthly upload statistics for the given year.
     * Returns Map<month (1-12), count>
     *
     * @param year the year to get statistics for
     */
    public Map<Integer, Integer> getMonthlyUploadStats(int year) {
        Map<Integer, Integer> stats = new HashMap<>();
        String sql = "SELECT MONTH(upload_date) AS month, COUNT(*) AS count " +
                     "FROM posts WHERE YEAR(upload_date) = ? AND is_deleted = FALSE " +
                     "GROUP BY MONTH(upload_date) ORDER BY month";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getInt("month"), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching monthly upload stats", e);
        }
        return stats;
    }

    /**
     * Returns all posts for admin management (including soft-deleted).
     */
    public List<Post> findAllForAdmin() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.full_name, u.profile_picture " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "ORDER BY p.upload_date DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) posts.add(mapResultSetToPost(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all posts for admin", e);
        }
        return posts;
    }

    // ==================== MAPPING ====================

    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setUserId(rs.getInt("user_id"));
        post.setTitle(rs.getString("title"));
        post.setCaption(rs.getString("caption"));
        post.setMediaPath(rs.getString("media_path"));
        post.setThumbnailPath(rs.getString("thumbnail_path"));
        post.setMediaType(rs.getString("media_type"));
        post.setTags(rs.getString("tags"));
        post.setTotalLikes(rs.getInt("total_likes"));
        post.setTotalComments(rs.getInt("total_comments"));
        post.setDeleted(rs.getBoolean("is_deleted"));
        Timestamp uploadDate = rs.getTimestamp("upload_date");
        if (uploadDate != null) post.setUploadDate(uploadDate.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) post.setUpdatedAt(updatedAt.toLocalDateTime());

        // Joined fields
        try {
            post.setUploaderUsername(rs.getString("username"));
            post.setUploaderFullName(rs.getString("full_name"));
            post.setUploaderProfilePicture(rs.getString("profile_picture"));
        } catch (SQLException ignored) { /* joined fields may not be present */ }

        return post;
    }
}
