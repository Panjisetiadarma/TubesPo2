package com.visualgallery.utils;

import java.util.regex.Pattern;

/**
 * ValidationUtils - Utility class for input validation.
 *
 * Provides static methods for validating user inputs.
 *
 * OOP: Utility Class (static methods), Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public final class ValidationUtils {

    // Regex patterns
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern USERNAME_PATTERN =
        Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^.{8,}$");

    // Max file sizes
    public static final long MAX_PHOTO_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB
    public static final long MAX_VIDEO_SIZE_BYTES = 100 * 1024 * 1024; // 100 MB

    // Private constructor to prevent instantiation
    private ValidationUtils() { }

    /**
     * Validates an email address format.
     *
     * @param email the email to validate
     * @return true if valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates a username (3-30 chars, alphanumeric + underscore).
     *
     * @param username the username to validate
     * @return true if valid
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Validates a password (min 8 characters).
     *
     * @param password the password to validate
     * @return true if valid
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Checks if a string is null or blank.
     *
     * @param value the value to check
     * @return true if null or empty
     */
    public static boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Validates a photo file extension.
     *
     * @param filename the file name to check
     * @return true if it's a supported photo format
     */
    public static boolean isValidPhotoFile(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
               lower.endsWith(".png") || lower.endsWith(".webp");
    }

    /**
     * Validates a video file extension.
     *
     * @param filename the file name to check
     * @return true if it's a supported video format
     */
    public static boolean isValidVideoFile(String filename) {
        if (filename == null) return false;
        return filename.toLowerCase().endsWith(".mp4");
    }

    /**
     * Validates the file size for a photo.
     *
     * @param fileSizeBytes the file size in bytes
     * @return true if within limit
     */
    public static boolean isPhotoSizeValid(long fileSizeBytes) {
        return fileSizeBytes > 0 && fileSizeBytes <= MAX_PHOTO_SIZE_BYTES;
    }

    /**
     * Validates the file size for a video.
     *
     * @param fileSizeBytes the file size in bytes
     * @return true if within limit
     */
    public static boolean isVideoSizeValid(long fileSizeBytes) {
        return fileSizeBytes > 0 && fileSizeBytes <= MAX_VIDEO_SIZE_BYTES;
    }

    /**
     * Sanitizes input by trimming and removing dangerous characters.
     *
     * @param input raw input
     * @return sanitized string
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Formats file size to human-readable string.
     *
     * @param bytes file size in bytes
     * @return formatted string like "1.5 MB"
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024)       return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
