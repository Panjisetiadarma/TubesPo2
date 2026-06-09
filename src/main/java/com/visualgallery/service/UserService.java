package com.visualgallery.service;

import com.visualgallery.model.User;
import com.visualgallery.repository.UserRepository;
import com.visualgallery.utils.ValidationUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UserService - Business logic for user management.
 *
 * Implements Service Layer Pattern.
 * Handles profile updates, password changes, admin user management.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    // ==================== PROFILE ====================

    public User getUserById(int id) {
        return userRepository.findById(id);
    }

    /**
     * Updates user profile (username, email, fullName, bio).
     * Validates uniqueness of username/email.
     *
     * @return "SUCCESS" or error message
     */
    public String updateProfile(int userId, String username, String email,
                                String fullName, String bio) {
        if (username == null || username.trim().isEmpty())
            return "Username tidak boleh kosong.";
        if (!ValidationUtils.isValidUsername(username))
            return "Format username tidak valid (3-30 karakter, huruf/angka/underscore).";
        if (email == null || email.trim().isEmpty())
            return "Email tidak boleh kosong.";
        if (!ValidationUtils.isValidEmail(email))
            return "Format email tidak valid.";

        String trimUser  = username.trim();
        String trimEmail = email.trim().toLowerCase();

        // Check uniqueness (excluding self)
        User existing = userRepository.findById(userId);
        if (existing == null) return "User tidak ditemukan.";

        if (!existing.getUsername().equalsIgnoreCase(trimUser)) {
            // Username changed – check uniqueness
            User byUsername = (User) new com.visualgallery.repository.AuthRepository().findByUsername(trimUser);
            if (byUsername != null && byUsername.getId() != userId)
                return "Username '" + trimUser + "' sudah digunakan.";
        }
        if (!existing.getEmail().equalsIgnoreCase(trimEmail)) {
            // Email changed – check uniqueness
            com.visualgallery.model.Account byEmail =
                    new com.visualgallery.repository.AuthRepository().findByEmail(trimEmail);
            if (byEmail != null && byEmail.getId() != userId)
                return "Email '" + trimEmail + "' sudah terdaftar.";
        }

        existing.setUsername(trimUser);
        existing.setEmail(trimEmail);
        existing.setFullName(fullName != null ? fullName.trim() : "");
        existing.setBio(bio != null ? bio.trim() : "");

        boolean success = userRepository.update(existing);
        return success ? "SUCCESS" : "Gagal memperbarui profil.";
    }

    /**
     * Changes a user's password after verifying old password.
     *
     * @return "SUCCESS" or error message
     */
    public String changePassword(int userId, String oldPassword,
                                 String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.length() < 8)
            return "Password baru minimal 8 karakter.";
        if (!newPassword.equals(confirmPassword))
            return "Konfirmasi password tidak cocok.";

        User user = userRepository.findById(userId);
        if (user == null) return "User tidak ditemukan.";

        if (!oldPassword.equals(user.getPassword())) return "Password lama tidak benar.";

        String hashed = newPassword;
        boolean success = userRepository.updatePassword(userId, hashed);
        return success ? "SUCCESS" : "Gagal mengganti password.";
    }

    /**
     * Updates profile picture path for a user.
     *
     * @param userId      the user's ID
     * @param picturePath the saved file path
     * @return true on success
     */
    public boolean updateProfilePicture(int userId, String picturePath) {
        return userRepository.updateProfilePicture(userId, picturePath);
    }

    /**
     * Gets user statistics: [uploads, likes received, comments received].
     */
    public int[] getUserStats(int userId) {
        return userRepository.getUserStats(userId);
    }

    // ==================== ADMIN USER MANAGEMENT ====================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.search(keyword);
    }

    /**
     * Admin creates a new user with a given role.
     *
     * @return "SUCCESS" or error message
     */
    public String adminCreateUser(String username, String email, String password,
                                  String fullName, String role) {
        if (username == null || username.trim().isEmpty())
            return "Username tidak boleh kosong.";
        if (!ValidationUtils.isValidEmail(email))
            return "Format email tidak valid.";
        if (password == null || password.length() < 8)
            return "Password minimal 8 karakter.";

        String trimUser  = username.trim();
        String trimEmail = email.trim().toLowerCase();

        com.visualgallery.repository.AuthRepository authRepo =
                new com.visualgallery.repository.AuthRepository();
        if (authRepo.isUsernameExists(trimUser))
            return "Username sudah digunakan.";
        if (authRepo.isEmailExists(trimEmail))
            return "Email sudah terdaftar.";

        String hashed = password;
        User newUser = new User();
        newUser.setUsername(trimUser);
        newUser.setEmail(trimEmail);
        newUser.setPassword(hashed);
        newUser.setFullName(fullName != null ? fullName.trim() : trimUser);
        newUser.setRole(role != null ? role : "USER");

        boolean success = userRepository.save(newUser);
        return success ? "SUCCESS" : "Gagal membuat user.";
    }

    /**
     * Admin deactivates a user.
     *
     * @param userId the user to deactivate
     * @return true if successful
     */
    public boolean deactivateUser(int userId) {
        return userRepository.deactivateUser(userId);
    }

    /**
     * Admin hard-deletes a user.
     *
     * @param userId the user to delete
     * @return true if successful
     */
    public boolean deleteUser(int userId) {
        return userRepository.delete(userId);
    }

    public int getTotalUsers()  { return userRepository.countUsers(); }
    public int getTotalAdmins() { return userRepository.countAdmins(); }
}
