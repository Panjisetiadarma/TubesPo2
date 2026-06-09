package com.visualgallery.model;

import java.time.LocalDateTime;

/**
 * Like - Entity representing a like on a post.
 *
 * OOP: Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class Like {

    private int id;
    private int postId;
    private int userId;
    private LocalDateTime createdAt;

    public Like() {
        this.createdAt = LocalDateTime.now();
    }

    public Like(int postId, int userId) {
        this();
        this.postId = postId;
        this.userId = userId;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public int getPostId()                   { return postId; }
    public void setPostId(int postId)        { this.postId = postId; }

    public int getUserId()                   { return userId; }
    public void setUserId(int userId)        { this.userId = userId; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)      { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Like{id=" + id + ", postId=" + postId + ", userId=" + userId + "}";
    }
}
