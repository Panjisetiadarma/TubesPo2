package com.visualgallery.controller;

import com.visualgallery.model.Account;
import com.visualgallery.service.AuthService;
import com.visualgallery.utils.ActivityLogger;
import com.visualgallery.utils.SessionManager;
import com.visualgallery.view.MainFrame;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * AuthController - MVC Controller for authentication flows.
 *
 * Bridges the Authentication views and AuthService.
 * Handles navigation between Login, Register, and Dashboard frames.
 *
 * OOP: MVC Controller Pattern, Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class AuthController {

    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    private final AuthService    authService;
    private final SessionManager sessionManager;

    public AuthController() {
        this.authService    = new AuthService();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * Processes a login attempt.
     * On success: opens MainFrame with the correct dashboard.
     * On failure: returns error message to the view.
     *
     * @param email       user email
     * @param password    plain-text password
     * @param rememberMe  whether to save email for next login
     * @param parentFrame the current login JFrame (to be disposed on success)
     * @return null on success, error message on failure
     */
    public String login(String email, String password, boolean rememberMe, JFrame parentFrame) {
        Account account = authService.login(email, password);
        if (account == null) {
            return "Email atau password salah. Silakan coba lagi.";
        }

        // Handle Remember Me
        if (rememberMe) {
            sessionManager.saveRememberMe(email);
        } else {
            sessionManager.clearRememberMe();
        }

        // Log activity
        ActivityLogger.log(account.getId(), ActivityLogger.ACTION_LOGIN,
                "User logged in: " + account.getUsername());

        // Open main frame on EDT
        JFrame frame = parentFrame;
        SwingUtilities.invokeLater(() -> {
            if (frame != null) frame.dispose();
            MainFrame mainFrame = new MainFrame(account);
            mainFrame.setVisible(true);
        });

        return null; // null = success
    }

    /**
     * Processes a registration attempt.
     *
     * @return null on success, error message on failure
     */
    public String register(String username, String email, String password,
                           String confirmPassword, String fullName) {
        String result = authService.register(username, email, password, confirmPassword, fullName);
        if ("SUCCESS".equals(result)) {
            ActivityLogger.log(null, ActivityLogger.ACTION_REGISTER,
                    "New user registered: " + username);
            return null;
        }
        return result;
    }

    /**
     * Logs out the current user and returns to the login screen.
     *
     * @param currentFrame the frame to dispose
     */
    public void logout(JFrame currentFrame) {
        Account account = sessionManager.getCurrentUser();
        if (account != null) {
            ActivityLogger.log(account.getId(), ActivityLogger.ACTION_LOGOUT,
                    "User logged out: " + account.getUsername());
        }
        authService.logout();
        SwingUtilities.invokeLater(() -> {
            if (currentFrame != null) currentFrame.dispose();
            openLoginFrame();
        });
    }

    /**
     * Opens the LoginFrame.
     */
    public void openLoginFrame() {
        SwingUtilities.invokeLater(() -> {
            com.visualgallery.view.auth.LoginFrame loginFrame =
                    new com.visualgallery.view.auth.LoginFrame(this);
            loginFrame.setVisible(true);
        });
    }

    /**
     * Returns the saved remembered email (for auto-fill).
     */
    public String getRememberedEmail() {
        return sessionManager.getRememberedEmail();
    }

    public boolean isRememberMeEnabled() {
        return sessionManager.isRememberMeEnabled();
    }
}
