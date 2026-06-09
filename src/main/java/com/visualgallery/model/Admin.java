package com.visualgallery.model;

/**
 * Admin - Concrete Class extending Account
 *
 * Demonstrates: Inheritance, Polymorphism, Encapsulation
 *
 * Represents an administrator account in Visual Gallery.
 * Has elevated permissions compared to the User class.
 *
 * OOP: Inheritance (extends Account), Polymorphism (overrides showDashboard)
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class Admin extends Account {

    // ==================== ADMIN-SPECIFIC FIELDS ====================
    private String fullName;
    private String adminLevel; // e.g., SUPER_ADMIN, MODERATOR

    // ==================== CONSTRUCTORS ====================

    public Admin() {
        super();
        setRole("ADMIN");
        this.adminLevel = "SUPER_ADMIN";
    }

    public Admin(int id, String username, String email, String password) {
        super(id, username, email, password, "ADMIN");
        this.adminLevel = "SUPER_ADMIN";
    }

    public Admin(int id, String username, String email, String password, String fullName) {
        super(id, username, email, password, "ADMIN");
        this.fullName   = fullName;
        this.adminLevel = "SUPER_ADMIN";
    }

    // ==================== POLYMORPHISM: Method Override ====================

    /**
     * Shows the admin dashboard panel.
     * Polymorphic behavior: differs from User's showDashboard().
     */
    @Override
    public void showDashboard() {
        System.out.println("Displaying ADMIN Dashboard for: " + getUsername());
        // Actual panel shown by AuthController based on role
    }

    /**
     * Returns display name: fullName if set, otherwise username.
     */
    @Override
    public String getDisplayName() {
        return (fullName != null && !fullName.isEmpty()) ? fullName : getUsername();
    }

    // ==================== ADMIN SPECIFIC METHODS ====================

    /**
     * Admin can delete any post.
     */
    public boolean canDeleteAnyPost() {
        return true;
    }

    /**
     * Admin can delete any comment.
     */
    public boolean canDeleteAnyComment() {
        return true;
    }

    /**
     * Admin can manage users.
     */
    public boolean canManageUsers() {
        return true;
    }

    /**
     * Admin can export data.
     */
    public boolean canExportData() {
        return true;
    }

    // ==================== GETTERS / SETTERS ====================

    public String getFullName()              { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAdminLevel()                  { return adminLevel; }
    public void setAdminLevel(String adminLevel)   { this.adminLevel = adminLevel; }
}
