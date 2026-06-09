package com.visualgallery.model;

import java.util.Arrays;
import java.util.List;

/**
 * Photo - Concrete class extending Media
 *
 * Demonstrates: Inheritance, Polymorphism, Encapsulation
 *
 * Represents a photo post in Visual Gallery.
 * Supports JPG, JPEG, PNG, WEBP formats.
 *
 * OOP: Inheritance (extends Media), Polymorphism (overrides abstract methods)
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class Photo extends Media {

    // ==================== PHOTO-SPECIFIC FIELDS ====================
    private int width;
    private int height;
    private String colorProfile;
    private long fileSizeBytes;

    private static final List<String> VALID_EXTENSIONS =
            Arrays.asList(".jpg", ".jpeg", ".png", ".webp");

    // ==================== CONSTRUCTORS ====================

    public Photo() {
        super();
        setMediaType("PHOTO");
    }

    public Photo(int id, int userId, String title, String mediaPath) {
        super(id, userId, title, mediaPath, "PHOTO");
    }

    // ==================== POLYMORPHISM: Method Override ====================

    /**
     * Renders the photo in the media view panel.
     * Polymorphic override of Media.render()
     */
    @Override
    public void render() {
        System.out.println("Rendering PHOTO: " + getTitle() + " [" + getMediaPath() + "]");
    }

    /**
     * Displays photo with full metadata.
     * Polymorphic override of Media.display()
     */
    @Override
    public void display() {
        System.out.println("=== PHOTO ===");
        System.out.println("Title: " + getTitle());
        System.out.println("Resolution: " + width + "x" + height);
        System.out.println("File Size: " + (fileSizeBytes / 1024) + " KB");
        System.out.println("Likes: " + getTotalLikes());
        System.out.println("Comments: " + getTotalComments());
    }

    /**
     * Returns the type label for display.
     */
    @Override
    public String getTypeLabel() {
        return "Photo";
    }

    /**
     * Validates that the file extension is a valid photo format.
     *
     * @param filePath the file path to validate
     * @return true if extension is JPG, JPEG, PNG, or WEBP
     */
    @Override
    public boolean isValidExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) return false;
        String lower = filePath.toLowerCase();
        return VALID_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    // ==================== GETTERS / SETTERS ====================

    public int getWidth()                  { return width; }
    public void setWidth(int width)        { this.width = width; }

    public int getHeight()                 { return height; }
    public void setHeight(int height)      { this.height = height; }

    public String getColorProfile()                    { return colorProfile; }
    public void setColorProfile(String colorProfile)   { this.colorProfile = colorProfile; }

    public long getFileSizeBytes()                   { return fileSizeBytes; }
    public void setFileSizeBytes(long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public static List<String> getValidExtensions()  { return VALID_EXTENSIONS; }

    public String getResolution() {
        return width + "x" + height;
    }
}
