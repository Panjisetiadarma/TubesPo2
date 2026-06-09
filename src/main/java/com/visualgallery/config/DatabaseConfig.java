package com.visualgallery.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DatabaseConfig - Singleton Database Configuration
 *
 * Manages JDBC connection to MySQL database.
 * Implements Singleton pattern for connection management.
 *
 * OOP Principles: Encapsulation, Singleton Pattern
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());

    // ==================== SINGLETON ====================
    private static DatabaseConfig instance;

    // ==================== DB CONFIGURATION ====================
    private String url;
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private String driver;

    // ==================== CONSTRUCTOR (Private for Singleton) ====================
    private DatabaseConfig() {
        loadProperties();
    }

    /**
     * Returns the singleton instance of DatabaseConfig.
     *
     * @return DatabaseConfig instance
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Loads database configuration from db.properties file.
     * Falls back to default values if file not found.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config/db.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                this.driver   = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                this.host     = props.getProperty("db.host", "localhost");
                this.port     = props.getProperty("db.port", "3306");
                this.database = props.getProperty("db.name", "visual_gallery_db");
                this.username = props.getProperty("db.username", "root");
                this.password = props.getProperty("db.password", "");
            } else {
                LOGGER.warning("db.properties not found. Using default configuration.");
                setDefaults();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading database properties", e);
            setDefaults();
        }
        buildUrl();
    }

    /**
     * Sets default database configuration values.
     */
    private void setDefaults() {
        this.driver   = "com.mysql.cj.jdbc.Driver";
        this.host     = "localhost";
        this.port     = "3306";
        this.database = "visual_gallery_db";
        this.username = "root";
        this.password = "";
    }

    /**
     * Builds the JDBC connection URL from config parts.
     */
    private void buildUrl() {
        this.url = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8",
            host, port, database
        );
    }

    /**
     * Gets a new database connection.
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Tests the database connection.
     *
     * @return true if connection successful
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection test failed", e);
            return false;
        }
    }

    /**
     * Safely closes a connection.
     *
     * @param connection the connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }

    // ==================== GETTERS / SETTERS ====================

    public String getUrl()      { return url; }
    public String getHost()     { return host; }
    public String getPort()     { return port; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
    public String getDriver()   { return driver; }

    public void setHost(String host)         { this.host = host; buildUrl(); }
    public void setPort(String port)         { this.port = port; buildUrl(); }
    public void setDatabase(String database) { this.database = database; buildUrl(); }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "DatabaseConfig{host='" + host + "', port='" + port +
               "', database='" + database + "', username='" + username + "'}";
    }
}
