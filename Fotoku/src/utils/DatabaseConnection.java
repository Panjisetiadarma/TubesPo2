package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/tubes_po2";
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; 
    private static volatile boolean initialized = false;

    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            if (!initialized) {
                initializeDatabase();
                initialized = true;
            }

            return DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver MySQL tidak ditemukan! Pastikan Anda sudah menambahkan mysql-connector-j.jar ke classpath.", "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            throw new SQLException("Driver MySQL tidak ditemukan", e);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal terhubung ke MySQL! Pastikan XAMPP/MySQL Anda sudah berjalan.\n\nError: " + e.getMessage(), "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }

    private static void initializeDatabase() {
        // Buat database otomatis jika belum ada
        String baseUrl = "jdbc:mysql://localhost:3306/";
        try (Connection setupConn = DriverManager.getConnection(baseUrl, USER, PASSWORD);
             Statement stmt = setupConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS tubes_po2");
        } catch (SQLException e) {
            // Abaikan error pembuatan database
        }

        // Auto-create tables if they don't exist
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = connection.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) NOT NULL UNIQUE," +
                "email VARCHAR(100) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL," +
                "full_name VARCHAR(100)," +
                "bio TEXT," +
                "profile_picture_url VARCHAR(255)," +
                "location VARCHAR(100)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
                    
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN full_name VARCHAR(100)");
            } catch (SQLException ignore) { }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN bio TEXT");
            } catch (SQLException ignore) { }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN profile_picture_url VARCHAR(255)");
            } catch (SQLException ignore) { }
            
            stmt.execute("CREATE TABLE IF NOT EXISTS posts (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "image_path VARCHAR(255) NOT NULL," +
                "caption TEXT," +
                "likes_count INT DEFAULT 0," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS post_saves (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "post_id INT NOT NULL," +
                "user_id INT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "UNIQUE KEY unique_save (post_id, user_id)," +
                "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS post_likes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "post_id INT NOT NULL," +
                "user_id INT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "UNIQUE KEY unique_like (post_id, user_id)," +
                "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");
                    
            stmt.execute("CREATE TABLE IF NOT EXISTS comments (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "post_id INT NOT NULL," +
                "user_id INT NOT NULL," +
                "text TEXT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");
            
            // Masukkan data admin dummy secara default jika belum ada
            stmt.execute("INSERT IGNORE INTO users (username, email, password, full_name, location) " +
                         "VALUES ('admin', 'admin@fotoku.com', 'admin123', 'Admin Fotoku', 'Jakarta, Indonesia')");
            stmt.execute("INSERT IGNORE INTO users (username, email, password, full_name, location) " +
                         "VALUES ('panji_photo', 'panji@fotoku.com', '123', 'Panji', 'Danau Toba, Sumut')");
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel otomatis: " + e.getMessage());
        }
    }
}