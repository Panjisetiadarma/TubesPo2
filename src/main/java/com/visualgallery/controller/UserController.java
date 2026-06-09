package com.visualgallery.controller;

import com.visualgallery.model.User;
import com.visualgallery.service.UserService;
import com.visualgallery.utils.ActivityLogger;
import com.visualgallery.utils.FileUploader;
import com.visualgallery.utils.SessionManager;

import java.io.File;
import java.util.List;

/**
 * UserController - MVC Controller for user profile and management.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class UserController {

    private final UserService    userService;
    private final SessionManager sessionManager;

    public UserController() {
        this.userService    = new UserService();
        this.sessionManager = SessionManager.getInstance();
    }

    public User getUserById(int id) {
        return userService.getUserById(id);
    }

    public User getCurrentUser() {
        if (!sessionManager.isLoggedIn()) return null;
        return userService.getUserById(sessionManager.getCurrentUserId());
    }

    /**
     * Updates the current user's profile.
     *
     * @return null on success, error message on failure
     */
    public String updateProfile(String username, String email, String fullName, String bio) {
        if (!sessionManager.isLoggedIn()) return "Tidak ada sesi aktif.";
        int userId = sessionManager.getCurrentUserId();
        String result = userService.updateProfile(userId, username, email, fullName, bio);
        if ("SUCCESS".equals(result)) {
            // Refresh session user
            User updated = userService.getUserById(userId);
            if (updated != null) sessionManager.createSession(updated);
            ActivityLogger.log(userId, ActivityLogger.ACTION_EDIT, "Updated profile");
            return null;
        }
        return result;
    }

    /**
     * Changes the current user's password.
     *
     * @return null on success, error message on failure
     */
    public String changePassword(String oldPassword, String newPassword, String confirmPassword) {
        if (!sessionManager.isLoggedIn()) return "Tidak ada sesi aktif.";
        String result = userService.changePassword(
                sessionManager.getCurrentUserId(), oldPassword, newPassword, confirmPassword);
        return "SUCCESS".equals(result) ? null : result;
    }

    /**
     * Updates the current user's profile picture.
     *
     * @param imageFile the selected image file
     * @return null on success, error message on failure
     */
    public String updateProfilePicture(File imageFile) {
        if (!sessionManager.isLoggedIn()) return "Tidak ada sesi aktif.";
        if (imageFile == null || !imageFile.exists()) return "File tidak valid.";
        int userId = sessionManager.getCurrentUserId();
        String savedPath = FileUploader.uploadProfilePicture(imageFile, userId);
        if (savedPath == null) return "Gagal menyimpan foto profil.";
        boolean success = userService.updateProfilePicture(userId, savedPath);
        if (success) {
            User updated = userService.getUserById(userId);
            if (updated != null) sessionManager.createSession(updated);
            return null;
        }
        return "Gagal memperbarui foto profil.";
    }

    public int[] getUserStats(int userId) {
        return userService.getUserStats(userId);
    }

    // ==================== ADMIN ====================

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public List<User> searchUsers(String keyword) {
        return userService.searchUsers(keyword);
    }

    public String adminCreateUser(String username, String email, String password,
                                  String fullName, String role) {
        String result = userService.adminCreateUser(username, email, password, fullName, role);
        return "SUCCESS".equals(result) ? null : result;
    }

    public boolean deactivateUser(int userId) {
        return userService.deactivateUser(userId);
    }

    public boolean deleteUser(int userId) {
        return userService.deleteUser(userId);
    }

    public int getTotalUsers()  { return userService.getTotalUsers(); }
    public int getTotalAdmins() { return userService.getTotalAdmins(); }
}
