package com.visualgallery.model;

import java.time.LocalDateTime;

/**
 * Media - Abstract Base Class for media content
 *
 * Demonstrates: Abstraction, Encapsulation, Inheritance
 *
 * Abstract superclass for Photo and Video entities.
 * Defines the common media structure and abstract methods
 * that subclasses must implement.
 *
 * OOP: Abstract Class, Encapsulation, Template Method Pattern
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public abstract class Media {

    // ==================== ENCAPSULATED FIELDS ====================
    private int id;
    private int userId;
    private String title;
    private String caption;
    private String mediaPath;
    private String thumbnailPath;
    private String mediaType;
    private String tags;
    private int totalLikes;
    private int totalComments;
    private boolean isDeleted;
    private LocalDateTime uploadDate;
    private LocalDateTime updatedAt;

    // Username of the post owner (for display)
    private String uploaderUsername;
    private String uploaderProfilePicture;
    private boolean likedByCurrentUser;
    private boolean savedByCurrentUser;

    // ==================== CONSTRUCTORS ====================

    protected Media() {
        this.isDeleted  = false;
        this.uploadDate = LocalDateTime.now();
        this.updatedAt  = LocalDateTime.now();
        this.totalLikes    = 0;
        this.totalComments = 0;
    }

    protected Media(int id, int userId, String title, String mediaPath, String mediaType) {
        this();
        this.id        = id;
        this.userId    = userId;
        this.title     = title;
        this.mediaPath = mediaPath;
        this.mediaType = mediaType;
    }

    // ==================== ABSTRACT METHODS (Abstraction + Polymorphism) ====================

    /**
     * Renders the media in the appropriate view component.
     * Must be overridden by Photo and Video subclasses.
     */
    public abstract void render();

    /**
     * Displays the media with metadata.
     * Must be overridden by subclasses.
     */
    public abstract void display();

    /**
     * Returns the formatted type label of this media.
     */
    public abstract String getTypeLabel();

    /**
     * Validates the file extension for this media type.
     *
     * @param filePath the file path to validate
     * @return true if the extension is valid
     */
    public abstract boolean isValidExtension(String filePath);

    // ==================== CONCRETE METHODS ====================

    /**
     * Returns a short caption preview (max 100 chars).
     */
    public String getCaptionPreview() {
        if (caption == null || caption.isEmpty()) return "";
        return caption.length() > 100 ? caption.substring(0, 97) + "..." : caption;
    }

    /**
     * Returns tags as an array.
     */
    public String[] getTagsArray() {
        if (tags == null || tags.isEmpty()) return new String[0];
        return tags.split(",");
    }

    // ==================== GETTERS / SETTERS ====================

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public int getUserId()                   { return userId; }
    public void setUserId(int userId)        { this.userId = userId; }

    public String getTitle()                 { return title; }
    public void setTitle(String title)       { this.title = title; }

    public String getCaption()               { return caption; }
    public void setCaption(String caption)   { this.caption = caption; }

    public String getMediaPath()                   { return mediaPath; }
    public void setMediaPath(String mediaPath)      { this.mediaPath = mediaPath; }

    public String getThumbnailPath()               { return thumbnailPath; }
    public void setThumbnailPath(String path)      { this.thumbnailPath = path; }

    public String getMediaType()                   { return mediaType; }
    public void setMediaType(String mediaType)     { this.mediaType = mediaType; }

    public String getTags()                  { return tags; }
    public void setTags(String tags)         { this.tags = tags; }

    public int getTotalLikes()                       { return totalLikes; }
    public void setTotalLikes(int totalLikes)        { this.totalLikes = totalLikes; }

    public int getTotalComments()                    { return totalComments; }
    public void setTotalComments(int totalComments)  { this.totalComments = totalComments; }

    public boolean isDeleted()                { return isDeleted; }
    public void setDeleted(boolean deleted)   { this.isDeleted = deleted; }

    public LocalDateTime getUploadDate()                   { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate)    { this.uploadDate = uploadDate; }

    public LocalDateTime getUpdatedAt()                    { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)      { this.updatedAt = updatedAt; }

    public String getUploaderUsername()                          { return uploaderUsername; }
    public void setUploaderUsername(String uploaderUsername)     { this.uploaderUsername = uploaderUsername; }

    public String getUploaderProfilePicture()                           { return uploaderProfilePicture; }
    public void setUploaderProfilePicture(String uploaderProfilePicture){ this.uploaderProfilePicture = uploaderProfilePicture; }

    public boolean isLikedByCurrentUser()                          { return likedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser)  { this.likedByCurrentUser = likedByCurrentUser; }

    public boolean isSavedByCurrentUser()                          { return savedByCurrentUser; }
    public void setSavedByCurrentUser(boolean savedByCurrentUser)  { this.savedByCurrentUser = savedByCurrentUser; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id +
               ", title='" + title + "', type='" + mediaType + "'}";
    }
}
