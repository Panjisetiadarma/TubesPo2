package com.visualgallery.model;

import java.time.LocalDateTime;

/**
 * Account - Abstract Base Class
 *
 * Demonstrates: Abstraction, Encapsulation, Inheritance
 *
 * This abstract class defines the common structure for all
 * account types (User, Admin) in the Visual Gallery application.
 *
 * OOP: Abstract Class, Encapsulation (private fields + getters/setters)
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public abstract class Account {

    // ==================== ENCAPSULATED FIELDS ====================
    private int id;
    private String username;
    private String email;
    private String password;
    private String role;
    private String profilePicture;
    private String bio;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==================== CONSTRUCTORS ====================

    protected Account() {
        this.isActive  = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    protected Account(int id, String username, String email, String password, String role) {
        this();
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // ==================== ABSTRACT METHODS (Abstraction) ====================

    /**
     * Show the appropriate dashboard for this account type.
     * Subclasses must implement this method (Polymorphism via Override).
     */
    public abstract void showDashboard();

    /**
     * Get the display name for this account type.
     */
    public abstract String getDisplayName();

    // ==================== CONCRETE METHODS ====================

    /**
     * Checks if this account belongs to the given role.
     *
     * @param roleToCheck the role to check
     * @return true if account has the role
     */
    public boolean hasRole(String roleToCheck) {
        return this.role != null && this.role.equalsIgnoreCase(roleToCheck);
    }

    /**
     * Checks if this account is an Admin.
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    /**
     * Checks if this account is a regular User.
     */
    public boolean isUser() {
        return "USER".equalsIgnoreCase(this.role);
    }

    // ==================== GETTERS / SETTERS ====================

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public String getUsername()              { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }

    public String getPassword()              { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole()                  { return role; }
    public void setRole(String role)         { this.role = role; }

    public String getProfilePicture()                      { return profilePicture; }
    public void setProfilePicture(String profilePicture)   { this.profilePicture = profilePicture; }

    public String getBio()                   { return bio; }
    public void setBio(String bio)           { this.bio = bio; }

    public boolean isActive()                { return isActive; }
    public void setActive(boolean active)    { this.isActive = active; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)      { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()                    { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)      { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id +
               ", username='" + username + "', email='" + email +
               "', role='" + role + "'}";
    }
}
