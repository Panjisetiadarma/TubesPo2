package com.visualgallery.service;

import com.visualgallery.model.Account;
import com.visualgallery.repository.AuthRepository;
import com.visualgallery.utils.SessionManager;
import com.visualgallery.utils.ValidationUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AuthService - Business logic for authentication operations.
 *
 * Implements the Service Layer Pattern.
 * Handles login, register, logout, and password verification.
 * Uses plain-text passwords as requested (No encryption).
 *
 * OOP: Encapsulation, Service Layer Pattern
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    public AuthService() {
        this.authRepository = new AuthRepository();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * Authenticates a user with email and password.
     *
     * @param email    the user's email
     * @param password the plain-text password
     * @return Account object if authenticated, null if failed
     */
    public Account login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            LOGGER.warning("Login attempt with empty credentials");
            return null;
        }

        Account account = authRepository.findByEmail(email.trim().toLowerCase());
        if (account == null) {
            LOGGER.info("Login failed: account not found for email=" + email);
            return null;
        }

        // Verify password
        if (!password.equals(account.getPassword())) {
            LOGGER.info("Login failed: incorrect password for email=" + email);
            return null;
        }

        // Create session
        sessionManager.createSession(account);
        LOGGER.info("Login successful: " + account.getUsername() + " [" + account.getRole() + "]");
        return account;
    }

    /**
     * Registers a new user account.
     *
     * @param username        desired username
     * @param email           email address
     * @param password        plain-text password
     * @param confirmPassword confirmation of password
     * @param fullName        full name
     * @return result message ("SUCCESS" or error description)
     */
    public String register(String username, String email, String password,
                           String confirmPassword, String fullName) {
        // Validate inputs
        if (username == null || username.trim().isEmpty())
            return "Username tidak boleh kosong.";
        if (!ValidationUtils.isValidUsername(username))
            return "Username hanya boleh mengandung huruf, angka, dan underscore (3-30 karakter).";
        if (email == null || email.trim().isEmpty())
            return "Email tidak boleh kosong.";
        if (!ValidationUtils.isValidEmail(email))
            return "Format email tidak valid.";
        if (password == null || password.isEmpty())
            return "Password tidak boleh kosong.";
        if (password.length() < 8)
            return "Password minimal 8 karakter.";
        if (!password.equals(confirmPassword))
            return "Konfirmasi password tidak cocok.";

        String trimmedUsername = username.trim();
        String trimmedEmail    = email.trim().toLowerCase();

        // Check uniqueness
        if (authRepository.isUsernameExists(trimmedUsername))
            return "Username '" + trimmedUsername + "' sudah digunakan.";
        if (authRepository.isEmailExists(trimmedEmail))
            return "Email '" + trimmedEmail + "' sudah terdaftar.";

        // Use plain text password
        String hashedPassword = password;

        // Save to database
        boolean success = authRepository.registerUser(trimmedUsername, trimmedEmail, hashedPassword,
                fullName != null ? fullName.trim() : trimmedUsername);

        if (success) {
            LOGGER.info("Registration successful: " + trimmedUsername);
            return "SUCCESS";
        } else {
            LOGGER.severe("Registration failed for: " + trimmedUsername);
            return "Registrasi gagal. Silakan coba lagi.";
        }
    }

    /**
     * Logs out the current session.
     */
    public void logout() {
        Account account = sessionManager.getCurrentUser();
        if (account != null) {
            LOGGER.info("Logout: " + account.getUsername());
        }
        sessionManager.clearSession();
    }

    /**
     * Verifies a plain-text password against the stored password.
     *
     * @param plainPassword  the plain-text password to check
     * @param hashedPassword the stored password (now plain text)
     * @return true if passwords match
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;
        return plainPassword.equals(hashedPassword);
    }

    /**
     * Returns the password as plain text (no hashing).
     *
     * @param plainPassword the password
     * @return the same string
     */
    public String hashPassword(String plainPassword) {
        return plainPassword;
    }
}
