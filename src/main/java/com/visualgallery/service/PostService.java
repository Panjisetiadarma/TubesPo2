package com.visualgallery.service;

import com.visualgallery.model.Post;
import com.visualgallery.model.Comment;
import com.visualgallery.model.Notification;
import com.visualgallery.repository.PostRepository;
import com.visualgallery.repository.LikeRepository;
import com.visualgallery.repository.CommentRepository;
import com.visualgallery.repository.NotificationRepository;
import com.visualgallery.utils.SessionManager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PostService - Business logic for post, like, and comment operations.
 *
 * Implements Service Layer Pattern.
 * Coordinates between repositories and enforces business rules.
 *
 * Implements: Likable and Commentable business logic
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class PostService {

    private static final Logger LOGGER = Logger.getLogger(PostService.class.getName());

    private final PostRepository        postRepository;
    private final LikeRepository        likeRepository;
    private final CommentRepository     commentRepository;
    private final NotificationRepository notificationRepository;
    private final SessionManager        sessionManager;

    public PostService() {
        this.postRepository         = new PostRepository();
        this.likeRepository         = new LikeRepository();
        this.commentRepository      = new CommentRepository();
        this.notificationRepository = new NotificationRepository();
        this.sessionManager         = SessionManager.getInstance();
    }

    // ==================== POST OPERATIONS ====================

    /**
     * Creates a new post and saves it to the database.
     *
     * @param post the Post to create
     * @return the generated post ID, or -1 on failure
     */
    public int createPost(Post post) {
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            LOGGER.warning("Post creation failed: title is empty");
            return -1;
        }
        if (post.getMediaPath() == null || post.getMediaPath().trim().isEmpty()) {
            LOGGER.warning("Post creation failed: media path is empty");
            return -1;
        }
        int id = postRepository.save(post);
        LOGGER.info("Post created with id: " + id);
        return id;
    }

    /**
     * Gets a post by its ID, enriched with like status for current user.
     *
     * @param postId the post ID
     * @return Post or null
     */
    public Post getPostById(int postId) {
        Post post = postRepository.findById(postId);
        if (post != null && sessionManager.isLoggedIn()) {
            int userId = sessionManager.getCurrentUser().getId();
            post.setLikedByCurrentUser(likeRepository.isLikedBy(postId, userId));
        }
        return post;
    }

    /**
     * Gets the explore feed with sort and pagination.
     *
     * @param sortBy   "NEWEST", "OLDEST", "POPULAR"
     * @param page     page number (1-based)
     * @param pageSize items per page
     * @return list of posts
     */
    public List<Post> getFeedPosts(String sortBy, int page, int pageSize) {
        List<Post> posts = postRepository.findFeedPosts(sortBy, page, pageSize);
        if (sessionManager.isLoggedIn()) {
            int userId = sessionManager.getCurrentUser().getId();
            posts.forEach(p -> p.setLikedByCurrentUser(likeRepository.isLikedBy(p.getId(), userId)));
        }
        return posts;
    }

    /**
     * Searches posts by keyword and filter type.
     *
     * @param keyword    search term
     * @param filterType "ALL", "PHOTO", "VIDEO"
     * @return list of matching posts
     */
    public List<Post> searchPosts(String keyword, String filterType) {
        if (keyword == null || keyword.trim().isEmpty()) return List.of();
        List<Post> posts = postRepository.search(keyword.trim(), filterType);
        if (sessionManager.isLoggedIn()) {
            int userId = sessionManager.getCurrentUser().getId();
            posts.forEach(p -> p.setLikedByCurrentUser(likeRepository.isLikedBy(p.getId(), userId)));
        }
        return posts;
    }

    /**
     * Gets all posts by a user (their gallery).
     *
     * @param userId the user ID
     * @return list of their posts
     */
    public List<Post> getPostsByUser(int userId) {
        return postRepository.findByUserId(userId);
    }

    /**
     * Gets posts liked by a user.
     *
     * @param userId the user ID
     * @return list of liked posts
     */
    public List<Post> getLikedPostsByUser(int userId) {
        return postRepository.findLikedByUserId(userId);
    }

    /**
     * Gets posts saved by a user.
     *
     * @param userId the user ID
     * @return list of saved posts
     */
    public List<Post> getSavedPostsByUser(int userId) {
        return postRepository.findSavedByUserId(userId);
    }

    /**
     * Updates a post (title, caption, tags).
     * Only the post's owner can update it.
     *
     * @param post    the updated post
     * @param userId  the requesting user's ID
     * @return true if update succeeded
     */
    public boolean updatePost(Post post, int userId) {
        Post existing = postRepository.findById(post.getId());
        if (existing == null) return false;
        if (existing.getUserId() != userId) {
            LOGGER.warning("Unauthorized post update attempt: user=" + userId + ", post=" + post.getId());
            return false;
        }
        return postRepository.update(post);
    }

    /**
     * Deletes a post.
     * Only the owner or admin can delete.
     *
     * @param postId    the post to delete
     * @param requesterId the requesting user's ID
     * @param isAdmin   whether the requester is an admin
     * @return true if deletion succeeded
     */
    public boolean deletePost(int postId, int requesterId, boolean isAdmin) {
        Post post = postRepository.findById(postId);
        if (post == null) return false;
        if (!isAdmin && post.getUserId() != requesterId) {
            LOGGER.warning("Unauthorized delete attempt: user=" + requesterId + ", post=" + postId);
            return false;
        }
        return postRepository.softDelete(postId);
    }

    // ==================== LIKE OPERATIONS ====================

    /**
     * Toggles like/unlike for the current user on a post.
     *
     * @param postId the post ID
     * @param userId the user ID
     * @return true if liked, false if unliked
     */
    public boolean toggleLike(int postId, int userId) {
        if (likeRepository.isLikedBy(postId, userId)) {
            likeRepository.unlike(postId, userId);
            return false;
        } else {
            likeRepository.like(postId, userId);
            // Notify post owner
            Post post = postRepository.findById(postId);
            if (post != null && post.getUserId() != userId) {
                String senderUsername = sessionManager.getCurrentUser().getUsername();
                Notification notif = new Notification(
                    post.getUserId(),
                    senderUsername + " menyukai postingan Anda: \"" + post.getTitle() + "\"",
                    Notification.Type.LIKE
                );
                notif.setSenderId(userId);
                notif.setPostId(postId);
                notificationRepository.save(notif);
            }
            return true;
        }
    }

    /**
     * Checks if a user has liked a post.
     *
     * @param postId the post ID
     * @param userId the user ID
     * @return true if liked
     */
    public boolean isLikedBy(int postId, int userId) {
        return likeRepository.isLikedBy(postId, userId);
    }

    // ==================== COMMENT OPERATIONS ====================

    /**
     * Adds a comment to a post.
     *
     * @param postId      the post ID
     * @param userId      the commenter's user ID
     * @param commentText the comment text
     * @return the generated comment ID, or -1 on failure
     */
    public int addComment(int postId, int userId, String commentText) {
        if (commentText == null || commentText.trim().isEmpty()) return -1;
        Comment comment = new Comment(postId, userId, commentText.trim());
        int commentId = commentRepository.save(comment);
        if (commentId > 0) {
            // Notify post owner
            Post post = postRepository.findById(postId);
            if (post != null && post.getUserId() != userId) {
                String senderUsername = sessionManager.getCurrentUser().getUsername();
                Notification notif = new Notification(
                    post.getUserId(),
                    senderUsername + " mengomentari postingan Anda: \"" + post.getTitle() + "\"",
                    Notification.Type.COMMENT
                );
                notif.setSenderId(userId);
                notif.setPostId(postId);
                notificationRepository.save(notif);
            }
        }
        return commentId;
    }

    /**
     * Deletes a comment. Only the owner or admin can delete.
     *
     * @param commentId   the comment to delete
     * @param requesterId the requesting user's ID
     * @param isAdmin     whether requester is admin
     * @return true if deletion succeeded
     */
    public boolean deleteComment(int commentId, int requesterId, boolean isAdmin) {
        return commentRepository.delete(commentId);
    }

    /**
     * Gets all comments for a post.
     *
     * @param postId the post ID
     * @return list of comments
     */
    public List<Comment> getComments(int postId) {
        return commentRepository.findByPostId(postId);
    }

    // ==================== ADMIN STATS ====================

    public int getTotalPosts()    { return postRepository.countTotal(); }
    public int getTotalPhotos()   { return postRepository.countByType("PHOTO"); }
    public int getTotalVideos()   { return postRepository.countByType("VIDEO"); }
    public int getTotalLikes()    { return likeRepository.countTotal(); }
    public int getTotalComments() { return commentRepository.countTotal(); }

    public List<Post> getAllPostsForAdmin() {
        return postRepository.findAllForAdmin();
    }
}
