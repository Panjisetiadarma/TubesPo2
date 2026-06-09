package com.visualgallery.controller;

import com.visualgallery.model.Post;
import com.visualgallery.model.Comment;
import com.visualgallery.service.PostService;
import com.visualgallery.utils.ActivityLogger;
import com.visualgallery.utils.FileUploader;
import com.visualgallery.utils.SessionManager;
import com.visualgallery.utils.ValidationUtils;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

/**
 * PostController - MVC Controller for post-related operations.
 *
 * Coordinates between views and PostService.
 * Handles upload, feed, search, like, comment operations.
 *
 * OOP: MVC Controller Pattern
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class PostController {

    private static final Logger LOGGER = Logger.getLogger(PostController.class.getName());

    private final PostService    postService;
    private final SessionManager sessionManager;

    public PostController() {
        this.postService    = new PostService();
        this.sessionManager = SessionManager.getInstance();
    }

    // ==================== UPLOAD ====================

    /**
     * Uploads a photo post.
     *
     * @param title      post title
     * @param caption    post caption
     * @param tags       comma-separated tags
     * @param sourceFile the source photo file
     * @return "SUCCESS" or error message
     */
    public String uploadPhoto(String title, String caption, String tags, File sourceFile) {
        if (!sessionManager.isLoggedIn()) return "Anda harus login terlebih dahulu.";
        if (ValidationUtils.isNullOrBlank(title)) return "Judul tidak boleh kosong.";
        if (sourceFile == null || !sourceFile.exists()) return "File foto tidak valid.";
        if (!ValidationUtils.isValidPhotoFile(sourceFile.getName()))
            return "Format file tidak didukung. Gunakan JPG, JPEG, PNG, atau WEBP.";
        if (!ValidationUtils.isPhotoSizeValid(sourceFile.length()))
            return "Ukuran file terlalu besar. Maksimal " +
                   ValidationUtils.formatFileSize(ValidationUtils.MAX_PHOTO_SIZE_BYTES) + ".";

        // Copy to uploads directory
        String savedPath = FileUploader.uploadPhoto(sourceFile);
        if (savedPath == null) return "Gagal menyimpan file. Coba lagi.";

        // Generate thumbnail
        String thumbPath = FileUploader.generatePhotoThumbnail(savedPath);

        // Create post entity
        Post post = new Post();
        post.setUserId(sessionManager.getCurrentUserId());
        post.setTitle(title.trim());
        post.setCaption(caption != null ? caption.trim() : "");
        post.setTags(tags != null ? tags.trim() : "");
        post.setMediaPath(savedPath);
        post.setThumbnailPath(thumbPath);
        post.setMediaType("PHOTO");

        int id = postService.createPost(post);
        if (id > 0) {
            ActivityLogger.log(sessionManager.getCurrentUserId(), ActivityLogger.ACTION_UPLOAD,
                    "Uploaded photo: " + title);
            return "SUCCESS";
        }
        return "Gagal menyimpan postingan. Coba lagi.";
    }

    /**
     * Uploads a video post.
     *
     * @param title      post title
     * @param caption    post caption
     * @param tags       comma-separated tags
     * @param sourceFile the source video file
     * @return "SUCCESS" or error message
     */
    public String uploadVideo(String title, String caption, String tags, File sourceFile) {
        if (!sessionManager.isLoggedIn()) return "Anda harus login terlebih dahulu.";
        if (ValidationUtils.isNullOrBlank(title)) return "Judul tidak boleh kosong.";
        if (sourceFile == null || !sourceFile.exists()) return "File video tidak valid.";
        if (!ValidationUtils.isValidVideoFile(sourceFile.getName()))
            return "Format file tidak didukung. Gunakan MP4.";
        if (!ValidationUtils.isVideoSizeValid(sourceFile.length()))
            return "Ukuran file terlalu besar. Maksimal " +
                   ValidationUtils.formatFileSize(ValidationUtils.MAX_VIDEO_SIZE_BYTES) + ".";

        String savedPath = FileUploader.uploadVideo(sourceFile);
        if (savedPath == null) return "Gagal menyimpan file. Coba lagi.";

        Post post = new Post();
        post.setUserId(sessionManager.getCurrentUserId());
        post.setTitle(title.trim());
        post.setCaption(caption != null ? caption.trim() : "");
        post.setTags(tags != null ? tags.trim() : "");
        post.setMediaPath(savedPath);
        post.setMediaType("VIDEO");

        int id = postService.createPost(post);
        if (id > 0) {
            ActivityLogger.log(sessionManager.getCurrentUserId(), ActivityLogger.ACTION_UPLOAD,
                    "Uploaded video: " + title);
            return "SUCCESS";
        }
        return "Gagal menyimpan postingan. Coba lagi.";
    }

    // ==================== FEED ====================

    public List<Post> getFeedPosts(String sortBy, int page, int pageSize) {
        return postService.getFeedPosts(sortBy, page, pageSize);
    }

    public Post getPostById(int postId) {
        return postService.getPostById(postId);
    }

    public List<Post> getPostsByUser(int userId) {
        return postService.getPostsByUser(userId);
    }

    public List<Post> getLikedPostsByUser(int userId) {
        return postService.getLikedPostsByUser(userId);
    }

    public List<Post> getSavedPostsByUser(int userId) {
        return postService.getSavedPostsByUser(userId);
    }

    // ==================== SEARCH ====================

    public List<Post> searchPosts(String keyword, String filterType) {
        return postService.searchPosts(keyword, filterType);
    }

    // ==================== LIKE ====================

    /**
     * Toggles like for the current user on a post.
     *
     * @param postId the post ID
     * @return true if now liked, false if now unliked
     */
    public boolean toggleLike(int postId) {
        if (!sessionManager.isLoggedIn()) return false;
        int userId = sessionManager.getCurrentUserId();
        boolean liked = postService.toggleLike(postId, userId);
        ActivityLogger.log(userId,
                liked ? ActivityLogger.ACTION_LIKE : ActivityLogger.ACTION_UNLIKE,
                (liked ? "Liked" : "Unliked") + " post: " + postId);
        return liked;
    }

    public boolean isLikedByCurrentUser(int postId) {
        if (!sessionManager.isLoggedIn()) return false;
        return postService.isLikedBy(postId, sessionManager.getCurrentUserId());
    }

    // ==================== COMMENT ====================

    public int addComment(int postId, String commentText) {
        if (!sessionManager.isLoggedIn()) return -1;
        int userId = sessionManager.getCurrentUserId();
        int id = postService.addComment(postId, userId, commentText);
        if (id > 0) {
            ActivityLogger.log(userId, ActivityLogger.ACTION_COMMENT,
                    "Commented on post: " + postId);
        }
        return id;
    }

    public boolean deleteComment(int commentId, int postId) {
        if (!sessionManager.isLoggedIn()) return false;
        int userId = sessionManager.getCurrentUserId();
        boolean isAdmin = sessionManager.isAdmin();
        return postService.deleteComment(commentId, userId, isAdmin);
    }

    public List<Comment> getComments(int postId) {
        return postService.getComments(postId);
    }

    // ==================== DELETE / EDIT ====================

    public boolean deletePost(int postId) {
        if (!sessionManager.isLoggedIn()) return false;
        int userId   = sessionManager.getCurrentUserId();
        boolean admin = sessionManager.isAdmin();
        boolean result = postService.deletePost(postId, userId, admin);
        if (result) {
            ActivityLogger.log(userId, ActivityLogger.ACTION_DELETE,
                    "Deleted post: " + postId);
        }
        return result;
    }

    public boolean updatePost(Post post) {
        if (!sessionManager.isLoggedIn()) return false;
        boolean result = postService.updatePost(post, sessionManager.getCurrentUserId());
        if (result) {
            ActivityLogger.log(sessionManager.getCurrentUserId(), ActivityLogger.ACTION_EDIT,
                    "Edited post: " + post.getId());
        }
        return result;
    }

    // ==================== STATS (for admin) ====================

    public int getTotalPosts()    { return postService.getTotalPosts(); }
    public int getTotalPhotos()   { return postService.getTotalPhotos(); }
    public int getTotalVideos()   { return postService.getTotalVideos(); }
    public int getTotalLikes()    { return postService.getTotalLikes(); }
    public int getTotalComments() { return postService.getTotalComments(); }
    public List<Post> getAllPostsForAdmin() { return postService.getAllPostsForAdmin(); }
}
