package com.visualgallery.model;

import java.time.LocalDateTime;

/**
 * Post - Entity representing a user post in Visual Gallery.
 *
 * A Post wraps a media item (Photo or Video) along with metadata.
 * Supports both PHOTO and VIDEO types.
 *
 * OOP: Encapsulation, Composition (has-a Media)
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class Post {

    // ==================== ENCAPSULATED FIELDS ====================
    private int id;
    private int userId;
    private String title;
    private String caption;
    private String mediaPath;
    private String thumbnailPath;
    private String mediaType; // PHOTO or VIDEO
    private String tags;
    private int totalLikes;
    private int totalComments;
    private boolean isDeleted;
    private LocalDateTime uploadDate;
    private LocalDateTime updatedAt;

    // Joined data (from other tables)
    private String uploaderUsername;
    private String uploaderFullName;
    private String uploaderProfilePicture;
    private boolean likedByCurrentUser;
    private boolean savedByCurrentUser;
    private String categoryNames; // comma-separated

    // ==================== CONSTRUCTORS ====================

    public Post() {
        this.isDeleted     = false;
        this.uploadDate    = LocalDateTime.now();
        this.updatedAt     = LocalDateTime.now();
        this.totalLikes    = 0;
        this.totalComments = 0;
    }

    public Post(int id, int userId, String title, String mediaPath, String mediaType) {
        this();
        this.id        = id;
        this.userId    = userId;
        this.title     = title;
        this.mediaPath = mediaPath;
        this.mediaType = mediaType;
    }

    // ==================== UTILITY METHODS ====================

    public boolean isPhoto() {
        return "PHOTO".equalsIgnoreCase(mediaType);
    }

    public boolean isVideo() {
        return "VIDEO".equalsIgnoreCase(mediaType);
    }

    public String getCaptionPreview(int maxLen) {
        if (caption == null || caption.isEmpty()) return "";
        return caption.length() > maxLen ? caption.substring(0, maxLen - 3) + "..." : caption;
    }

    public String[] getTagsArray() {
        if (tags == null || tags.trim().isEmpty()) return new String[0];
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

    public String getUploaderFullName()                          { return uploaderFullName; }
    public void setUploaderFullName(String uploaderFullName)     { this.uploaderFullName = uploaderFullName; }

    public String getUploaderProfilePicture()                           { return uploaderProfilePicture; }
    public void setUploaderProfilePicture(String p)                     { this.uploaderProfilePicture = p; }

    public boolean isLikedByCurrentUser()                          { return likedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser)  { this.likedByCurrentUser = likedByCurrentUser; }

    public boolean isSavedByCurrentUser()                          { return savedByCurrentUser; }
    public void setSavedByCurrentUser(boolean savedByCurrentUser)  { this.savedByCurrentUser = savedByCurrentUser; }

    public String getCategoryNames()                     { return categoryNames; }
    public void setCategoryNames(String categoryNames)   { this.categoryNames = categoryNames; }

    @Override
    public String toString() {
        return "Post{id=" + id + ", title='" + title + "', type='" + mediaType + "'}";
    }
}
