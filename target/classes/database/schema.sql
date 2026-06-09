-- ============================================================
-- Visual Gallery Database Schema
-- Version: 1.0.0
-- Created: 2024
-- ============================================================

CREATE DATABASE IF NOT EXISTS visual_gallery_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE visual_gallery_db;

-- ============================================================
-- TABLE: users
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    full_name   VARCHAR(100),
    bio         TEXT,
    profile_picture VARCHAR(500),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_username (username),
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: categories
-- ============================================================
CREATE TABLE IF NOT EXISTS categories (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description   TEXT,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_categories_name (category_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: posts
-- ============================================================
CREATE TABLE IF NOT EXISTS posts (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    title           VARCHAR(255) NOT NULL,
    caption         TEXT,
    media_path      VARCHAR(500) NOT NULL,
    thumbnail_path  VARCHAR(500),
    media_type      ENUM('PHOTO', 'VIDEO') NOT NULL,
    tags            VARCHAR(500),
    total_likes     INT NOT NULL DEFAULT 0,
    total_comments  INT NOT NULL DEFAULT 0,
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE,
    upload_date     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_posts_user_id (user_id),
    INDEX idx_posts_media_type (media_type),
    INDEX idx_posts_upload_date (upload_date),
    INDEX idx_posts_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: post_categories
-- ============================================================
CREATE TABLE IF NOT EXISTS post_categories (
    post_id     INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (post_id, category_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: comments
-- ============================================================
CREATE TABLE IF NOT EXISTS comments (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    post_id      INT NOT NULL,
    user_id      INT NOT NULL,
    comment_text TEXT NOT NULL,
    is_deleted   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_comments_post_id (post_id),
    INDEX idx_comments_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: likes
-- ============================================================
CREATE TABLE IF NOT EXISTS likes (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    post_id    INT NOT NULL,
    user_id    INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_likes_post_user (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_likes_post_id (post_id),
    INDEX idx_likes_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: notifications
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL,
    sender_id  INT,
    post_id    INT,
    message    TEXT NOT NULL,
    type       ENUM('LIKE', 'COMMENT', 'SYSTEM') NOT NULL DEFAULT 'SYSTEM',
    status     ENUM('READ', 'UNREAD') NOT NULL DEFAULT 'UNREAD',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: activity_logs
-- ============================================================
CREATE TABLE IF NOT EXISTS activity_logs (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT,
    action      VARCHAR(100) NOT NULL,
    description TEXT,
    ip_address  VARCHAR(45),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_activity_user_id (user_id),
    INDEX idx_activity_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: saved_posts
-- ============================================================
CREATE TABLE IF NOT EXISTS saved_posts (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL,
    post_id    INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_saved_post_user (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- STORED PROCEDURES
-- ============================================================

DELIMITER //

-- Increment like count
CREATE PROCEDURE IF NOT EXISTS IncrementLike(IN p_post_id INT)
BEGIN
    UPDATE posts SET total_likes = total_likes + 1 WHERE id = p_post_id;
END //

-- Decrement like count
CREATE PROCEDURE IF NOT EXISTS DecrementLike(IN p_post_id INT)
BEGIN
    UPDATE posts SET total_likes = GREATEST(total_likes - 1, 0) WHERE id = p_post_id;
END //

-- Increment comment count
CREATE PROCEDURE IF NOT EXISTS IncrementComment(IN p_post_id INT)
BEGIN
    UPDATE posts SET total_comments = total_comments + 1 WHERE id = p_post_id;
END //

-- Decrement comment count
CREATE PROCEDURE IF NOT EXISTS DecrementComment(IN p_post_id INT)
BEGIN
    UPDATE posts SET total_comments = GREATEST(total_comments - 1, 0) WHERE id = p_post_id;
END //

DELIMITER ;

-- ============================================================
-- TRIGGERS
-- ============================================================

-- Trigger: after like insert → update count
CREATE TRIGGER IF NOT EXISTS after_like_insert
    AFTER INSERT ON likes
    FOR EACH ROW
    UPDATE posts SET total_likes = total_likes + 1 WHERE id = NEW.post_id;

-- Trigger: after like delete → update count
CREATE TRIGGER IF NOT EXISTS after_like_delete
    AFTER DELETE ON likes
    FOR EACH ROW
    UPDATE posts SET total_likes = GREATEST(total_likes - 1, 0) WHERE id = OLD.post_id;

-- Trigger: after comment insert → update count
CREATE TRIGGER IF NOT EXISTS after_comment_insert
    AFTER INSERT ON comments
    FOR EACH ROW
    UPDATE posts SET total_comments = total_comments + 1 WHERE id = NEW.post_id;

-- Trigger: after comment delete → update count
CREATE TRIGGER IF NOT EXISTS after_comment_delete
    AFTER DELETE ON comments
    FOR EACH ROW
    UPDATE posts SET total_comments = GREATEST(total_comments - 1, 0) WHERE id = OLD.post_id;

-- ============================================================
-- DUMMY DATA
-- ============================================================

-- Insert Categories
INSERT INTO categories (category_name, description) VALUES
('Photography', 'General photography and visual art'),
('Portrait', 'Portrait and people photography'),
('Landscape', 'Nature and landscape photography'),
('Street', 'Street and urban photography'),
('Architecture', 'Buildings and architectural photography'),
('Wildlife', 'Animals and wildlife photography'),
('Food', 'Food and culinary photography'),
('Travel', 'Travel and destination photography'),
('Fashion', 'Fashion and style photography'),
('Abstract', 'Abstract and creative photography'),
('Videography', 'Video content and films'),
('Cinematic', 'Cinematic style video content')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Insert Admin User (password: admin123)
INSERT INTO users (username, email, password, role, full_name, bio, is_active) VALUES
('admin', 'admin@visualgallery.com',
 'admin123',
 'ADMIN', 'Visual Gallery Admin',
 'Platform administrator for Visual Gallery', TRUE)
ON DUPLICATE KEY UPDATE role = 'ADMIN';

-- Insert Sample Users (password: User@12345)
INSERT INTO users (username, email, password, role, full_name, bio, is_active) VALUES
('panji_setia', 'panji@example.com',
 'User@12345',
 'USER', 'Panji Setiadarma', 'Photography enthusiast | Nature lover', TRUE),
('luna_art', 'luna@example.com',
 'User@12345',
 'USER', 'Luna Artika', 'Capturing moments, one frame at a time', TRUE),
('rafi_foto', 'rafi@example.com',
 'User@12345',
 'USER', 'Rafi Fotografer', 'Street photographer based in Jakarta', TRUE),
('sari_visual', 'sari@example.com',
 'User@12345',
 'USER', 'Sari Visual', 'Fashion & lifestyle content creator', TRUE)
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);
