package com.visualgallery;

import com.visualgallery.config.DatabaseConfig;
import com.visualgallery.controller.AuthController;
import com.visualgallery.utils.ThemeManager;

import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main - Entry point for the Visual Gallery application.
 *
 * Initializes the database connection pool, applies the UI theme,
 * and launches the Authentication Controller.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting Visual Gallery Application...");

        // 1. Initialize Database Connection Pool
        try {
            // Getting the instance forces the initialization of HikariCP (if configured)
            // or verifies the basic JDBC connection.
            DatabaseConfig.getInstance();
            LOGGER.info("Database initialized successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database. Exiting.", e);
            System.exit(1);
        }

        // 2. Apply Theme & Launch GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply FlatLaf theme based on saved preferences
                ThemeManager.getInstance().applyTheme();
                
                // Initialize AuthController to manage the login flow
                AuthController authController = new AuthController();
                authController.openLoginFrame();
                
                LOGGER.info("GUI launched successfully.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to launch GUI", e);
            }
        });
    }
}
