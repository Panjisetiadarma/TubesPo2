package com.visualgallery.model;

import java.time.LocalDateTime;

/**
 * Notification - Entity for user notifications.
 *
 * OOP: Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class Notification {

    public enum Type   { LIKE, COMMENT, SYSTEM }
    public enum Status { READ, UNREAD }

    private int id;
    private int userId;
    private Integer senderId;
    private Integer postId;
    private String message;
    private Type type;
    private Status status;
    private LocalDateTime createdAt;

    // Joined fields
    private String senderUsername;
    private String senderProfilePicture;
    private String postTitle;

    // ==================== CONSTRUCTORS ====================

    public Notification() {
        this.type      = Type.SYSTEM;
        this.status    = Status.UNREAD;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(int userId, String message, Type type) {
        this();
        this.userId  = userId;
        this.message = message;
        this.type    = type;
    }

    // ==================== UTILITY ====================

    public boolean isUnread() {
        return Status.UNREAD.equals(status);
    }

    public void markAsRead() {
        this.status = Status.READ;
    }

    // ==================== GETTERS / SETTERS ====================

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public int getUserId()                   { return userId; }
    public void setUserId(int userId)        { this.userId = userId; }

    public Integer getSenderId()                     { return senderId; }
    public void setSenderId(Integer senderId)        { this.senderId = senderId; }

    public Integer getPostId()               { return postId; }
    public void setPostId(Integer postId)    { this.postId = postId; }

    public String getMessage()               { return message; }
    public void setMessage(String message)   { this.message = message; }

    public Type getType()                    { return type; }
    public void setType(Type type)           { this.type = type; }

    public Status getStatus()                { return status; }
    public void setStatus(Status status)     { this.status = status; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)      { this.createdAt = createdAt; }

    public String getSenderUsername()                          { return senderUsername; }
    public void setSenderUsername(String senderUsername)       { this.senderUsername = senderUsername; }

    public String getSenderProfilePicture()                           { return senderProfilePicture; }
    public void setSenderProfilePicture(String senderProfilePicture)  { this.senderProfilePicture = senderProfilePicture; }

    public String getPostTitle()                   { return postTitle; }
    public void setPostTitle(String postTitle)     { this.postTitle = postTitle; }

    @Override
    public String toString() {
        return "Notification{id=" + id + ", userId=" + userId +
               ", type=" + type + ", status=" + status + "}";
    }
}
