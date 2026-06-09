package com.visualgallery.model;

/**
 * User - Concrete Class extending Account
 *
 * Demonstrates: Inheritance, Polymorphism, Encapsulation
 *
 * Represents a regular user account in Visual Gallery.
 * Extends the abstract Account class and provides specific
 * user-level functionality.
 *
 * OOP: Inheritance (extends Account), Polymorphism (overrides showDashboard)
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class User extends Account {

    // ==================== USER-SPECIFIC FIELDS ====================
    private String fullName;
    private int totalUploads;
    private int totalLikes;
    private int totalComments;

    // ==================== CONSTRUCTORS ====================

    public User() {
        super();
        setRole("USER");
    }

    public User(int id, String username, String email, String password) {
        super(id, username, email, password, "USER");
    }

    public User(int id, String username, String email, String password, String fullName, String bio) {
        super(id, username, email, password, "USER");
        this.fullName = fullName;
        setBio(bio);
    }

    // ==================== POLYMORPHISM: Method Override ====================

    /**
     * Shows the user dashboard panel.
     * Polymorphic behavior: differs from Admin's showDashboard().
     */
    @Override
    public void showDashboard() {
        System.out.println("Displaying USER Dashboard for: " + getUsername());
        // Actual panel shown by AuthController based on role
    }

    /**
     * Returns display name: fullName if set, otherwise username.
     */
    @Override
    public String getDisplayName() {
        return (fullName != null && !fullName.isEmpty()) ? fullName : getUsername();
    }

    // ==================== GETTERS / SETTERS ====================

    public String getFullName()              { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getTotalUploads()                     { return totalUploads; }
    public void setTotalUploads(int totalUploads)    { this.totalUploads = totalUploads; }

    public int getTotalLikes()                       { return totalLikes; }
    public void setTotalLikes(int totalLikes)        { this.totalLikes = totalLikes; }

    public int getTotalComments()                    { return totalComments; }
    public void setTotalComments(int totalComments)  { this.totalComments = totalComments; }
}
