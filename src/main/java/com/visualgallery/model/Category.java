package com.visualgallery.model;

import java.time.LocalDateTime;

/**
 * Category - Entity for post categories.
 *
 * OOP: Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class Category {

    private int id;
    private String categoryName;
    private String description;
    private LocalDateTime createdAt;

    public Category() {
        this.createdAt = LocalDateTime.now();
    }

    public Category(int id, String categoryName) {
        this();
        this.id           = id;
        this.categoryName = categoryName;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public String getCategoryName()                      { return categoryName; }
    public void setCategoryName(String categoryName)     { this.categoryName = categoryName; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)      { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return categoryName;
    }
}
