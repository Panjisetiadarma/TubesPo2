package com.visualgallery.interfaces;

/**
 * Likable - Interface for entities that support Like/Unlike operations.
 *
 * Demonstrates: Interface (OOP Principle)
 *
 * Any class implementing this interface can be liked/unliked.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public interface Likable {

    /**
     * Like this entity for a given user.
     *
     * @param userId the user liking the entity
     * @return true if the like was successful
     */
    boolean like(int userId);

    /**
     * Unlike this entity for a given user.
     *
     * @param userId the user unliking the entity
     * @return true if the unlike was successful
     */
    boolean unlike(int userId);

    /**
     * Check if a given user has liked this entity.
     *
     * @param userId the user to check
     * @return true if the user has liked this entity
     */
    boolean isLikedBy(int userId);

    /**
     * Get the total number of likes for this entity.
     *
     * @return the total like count
     */
    int getLikeCount();
}
