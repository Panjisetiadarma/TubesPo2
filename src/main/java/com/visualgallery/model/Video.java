package com.visualgallery.model;

import java.util.Arrays;
import java.util.List;

/**
 * Video - Concrete class extending Media
 *
 * Demonstrates: Inheritance, Polymorphism, Encapsulation
 *
 * Represents a video post in Visual Gallery.
 * Supports MP4 format.
 *
 * OOP: Inheritance (extends Media), Polymorphism (overrides abstract methods)
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class Video extends Media {

    // ==================== VIDEO-SPECIFIC FIELDS ====================
    private int durationSeconds;
    private int width;
    private int height;
    private String codec;
    private int bitrate;
    private long fileSizeBytes;

    private static final List<String> VALID_EXTENSIONS = Arrays.asList(".mp4");

    // ==================== CONSTRUCTORS ====================

    public Video() {
        super();
        setMediaType("VIDEO");
    }

    public Video(int id, int userId, String title, String mediaPath) {
        super(id, userId, title, mediaPath, "VIDEO");
    }

    // ==================== POLYMORPHISM: Method Override ====================

    /**
     * Renders the video in the media player view.
     * Polymorphic override of Media.render()
     */
    @Override
    public void render() {
        System.out.println("Rendering VIDEO: " + getTitle() +
                           " [Duration: " + getFormattedDuration() + "]");
    }

    /**
     * Displays video with full metadata.
     * Polymorphic override of Media.display()
     */
    @Override
    public void display() {
        System.out.println("=== VIDEO ===");
        System.out.println("Title: " + getTitle());
        System.out.println("Duration: " + getFormattedDuration());
        System.out.println("Resolution: " + width + "x" + height);
        System.out.println("File Size: " + (fileSizeBytes / (1024 * 1024)) + " MB");
        System.out.println("Likes: " + getTotalLikes());
        System.out.println("Comments: " + getTotalComments());
    }

    /**
     * Returns the type label for display.
     */
    @Override
    public String getTypeLabel() {
        return "Video";
    }

    /**
     * Validates that the file extension is a valid video format.
     *
     * @param filePath the file path to validate
     * @return true if extension is MP4
     */
    @Override
    public boolean isValidExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) return false;
        String lower = filePath.toLowerCase();
        return VALID_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    // ==================== VIDEO-SPECIFIC METHODS ====================

    /**
     * Returns the formatted duration as MM:SS.
     */
    public String getFormattedDuration() {
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // ==================== GETTERS / SETTERS ====================

    public int getDurationSeconds()                      { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds)  { this.durationSeconds = durationSeconds; }

    public int getWidth()                  { return width; }
    public void setWidth(int width)        { this.width = width; }

    public int getHeight()                 { return height; }
    public void setHeight(int height)      { this.height = height; }

    public String getCodec()               { return codec; }
    public void setCodec(String codec)     { this.codec = codec; }

    public int getBitrate()                { return bitrate; }
    public void setBitrate(int bitrate)    { this.bitrate = bitrate; }

    public long getFileSizeBytes()                   { return fileSizeBytes; }
    public void setFileSizeBytes(long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public static List<String> getValidExtensions()  { return VALID_EXTENSIONS; }
}
