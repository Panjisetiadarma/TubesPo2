package com.visualgallery.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FileUploader - Handles file upload, copy, and storage.
 *
 * Manages copying uploaded media files to the application's
 * uploads directory and generating thumbnails.
 *
 * OOP: Utility, Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class FileUploader {

    private static final Logger LOGGER = Logger.getLogger(FileUploader.class.getName());

    // Base directory for uploads (relative to user home)
    private static final String UPLOADS_DIR   = System.getProperty("user.home") + "/VisualGallery/uploads/";
    private static final String PHOTOS_DIR    = UPLOADS_DIR + "photos/";
    private static final String VIDEOS_DIR    = UPLOADS_DIR + "videos/";
    private static final String THUMBS_DIR    = UPLOADS_DIR + "thumbnails/";
    private static final String PROFILES_DIR  = UPLOADS_DIR + "profiles/";

    // Thumbnail dimensions
    private static final int THUMB_WIDTH  = 400;
    private static final int THUMB_HEIGHT = 400;

    static {
        // Ensure directories exist
        createDir(PHOTOS_DIR);
        createDir(VIDEOS_DIR);
        createDir(THUMBS_DIR);
        createDir(PROFILES_DIR);
    }

    private static void createDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
    }

    /**
     * Copies a photo file to the uploads/photos directory.
     * Returns the new file path, or null on failure.
     *
     * @param sourceFile the source file selected by the user
     * @return destination file path
     */
    public static String uploadPhoto(File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) return null;
        String extension = getExtension(sourceFile.getName());
        String newFilename = UUID.randomUUID().toString() + extension;
        File dest = new File(PHOTOS_DIR + newFilename);
        try {
            Files.copy(sourceFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Photo uploaded: " + dest.getAbsolutePath());
            return dest.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to upload photo", e);
            return null;
        }
    }

    /**
     * Copies a video file to the uploads/videos directory.
     * Returns the new file path, or null on failure.
     *
     * @param sourceFile the source video file
     * @return destination file path
     */
    public static String uploadVideo(File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) return null;
        String extension = getExtension(sourceFile.getName());
        String newFilename = UUID.randomUUID().toString() + extension;
        File dest = new File(VIDEOS_DIR + newFilename);
        try {
            Files.copy(sourceFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Video uploaded: " + dest.getAbsolutePath());
            return dest.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to upload video", e);
            return null;
        }
    }

    /**
     * Generates a thumbnail for a photo using Thumbnailator.
     * Returns the thumbnail path, or null on failure.
     *
     * @param photoPath the original photo path
     * @return thumbnail file path
     */
    public static String generatePhotoThumbnail(String photoPath) {
        if (photoPath == null) return null;
        File sourceFile = new File(photoPath);
        if (!sourceFile.exists()) return null;

        String thumbFilename = "thumb_" + UUID.randomUUID() + ".jpg";
        File thumbFile = new File(THUMBS_DIR + thumbFilename);
        try {
            Thumbnails.of(sourceFile)
                    .crop(Positions.CENTER)
                    .size(THUMB_WIDTH, THUMB_HEIGHT)
                    .outputFormat("jpg")
                    .toFile(thumbFile);
            LOGGER.info("Thumbnail generated: " + thumbFile.getAbsolutePath());
            return thumbFile.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate thumbnail for: " + photoPath, e);
            return null;
        }
    }

    /**
     * Generates a thumbnail/placeholder for a video (first frame simulation).
     * In a real implementation, this would use JavaCV or FFmpeg.
     * Here we use a placeholder image from resources.
     *
     * @param videoPath the video path
     * @return placeholder thumbnail path
     */
    public static String generateVideoThumbnail(String videoPath) {
        // For now, copy a default video thumbnail placeholder
        return null; // Placeholder: would use FFmpeg integration
    }

    /**
     * Copies a profile picture to the uploads/profiles directory.
     *
     * @param sourceFile    the source image file
     * @param userId        the user's ID (used in naming)
     * @return the saved file path
     */
    public static String uploadProfilePicture(File sourceFile, int userId) {
        if (sourceFile == null || !sourceFile.exists()) return null;
        String extension = getExtension(sourceFile.getName());
        String newFilename = "profile_" + userId + "_" + UUID.randomUUID() + extension;
        File dest = new File(PROFILES_DIR + newFilename);
        try {
            Thumbnails.of(sourceFile)
                    .crop(Positions.CENTER)
                    .size(200, 200)
                    .outputFormat("jpg")
                    .toFile(dest);
            return dest.getAbsolutePath();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to upload profile picture", e);
            return null;
        }
    }

    /**
     * Deletes a file from disk.
     *
     * @param filePath the file path to delete
     * @return true if deleted
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null) return false;
        File file = new File(filePath);
        if (file.exists()) return file.delete();
        return false;
    }

    /**
     * Extracts the file extension (including the dot).
     */
    private static String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx >= 0) ? filename.substring(idx).toLowerCase() : "";
    }

    // ==================== GETTERS ====================

    public static String getUploadsDir()  { return UPLOADS_DIR; }
    public static String getPhotosDir()   { return PHOTOS_DIR; }
    public static String getVideosDir()   { return VIDEOS_DIR; }
    public static String getThumbsDir()   { return THUMBS_DIR; }
    public static String getProfilesDir() { return PROFILES_DIR; }
}
