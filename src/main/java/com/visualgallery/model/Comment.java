package com.visualgallery.model;

import java.time.LocalDateTime;

/**
 * Comment - Entity representing a comment on a post.
 *
 * OOP: Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class Comment {

    private int id;
    private int postId;
    private int userId;
    private String commentText;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Joined fields
    private String commenterUsername;
    private String commenterFullName;
    private String commenterProfilePicture;

    // ==================== CONSTRUCTORS ====================

    public Comment() {
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Comment(int postId, int userId, String commentText) {
        this();
        this.postId      = postId;
        this.userId      = userId;
        this.commentText = commentText;
    }

    // ==================== GETTERS / SETTERS ====================

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public int getPostId()                   { return postId; }
    public void setPostId(int postId)        { this.postId = postId; }

    public int getUserId()                   { return userId; }
    public void setUserId(int userId)        { this.userId = userId; }

    public String getCommentText()                     { return commentText; }
    public void setCommentText(String commentText)     { this.commentText = commentText; }

    public boolean isDeleted()                { return isDeleted; }
    public void setDeleted(boolean deleted)   { this.isDeleted = deleted; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)      { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()                    { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)      { this.updatedAt = updatedAt; }

    public String getCommenterUsername()                           { return commenterUsername; }
    public void setCommenterUsername(String commenterUsername)     { this.commenterUsername = commenterUsername; }

    public String getCommenterFullName()                           { return commenterFullName; }
    public void setCommenterFullName(String commenterFullName)     { this.commenterFullName = commenterFullName; }

    public String getCommenterProfilePicture()                              { return commenterProfilePicture; }
    public void setCommenterProfilePicture(String commenterProfilePicture)  { this.commenterProfilePicture = commenterProfilePicture; }

    @Override
    public String toString() {
        return "Comment{id=" + id + ", postId=" + postId + ", userId=" + userId + "}";
    }
}
