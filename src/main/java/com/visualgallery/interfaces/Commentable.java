package com.visualgallery.interfaces;

import com.visualgallery.model.Comment;
import java.util.List;

/**
 * Commentable - Interface for entities that support comment operations.
 *
 * Demonstrates: Interface (OOP Principle)
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public interface Commentable {

    /**
     * Add a comment to this entity.
     *
     * @param userId      the commenting user's ID
     * @param commentText the text of the comment
     * @return true if the comment was added successfully
     */
    boolean addComment(int userId, String commentText);

    /**
     * Delete a comment by its ID.
     *
     * @param commentId the ID of the comment to delete
     * @param userId    the user requesting the deletion
     * @return true if deleted successfully
     */
    boolean deleteComment(int commentId, int userId);

    /**
     * Get all comments for this entity.
     *
     * @return list of comments
     */
    List<Comment> getComments();

    /**
     * Get the total number of comments.
     *
     * @return the comment count
     */
    int getCommentCount();
}
