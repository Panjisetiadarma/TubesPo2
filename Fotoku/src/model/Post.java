package model;

import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class Post {
    /** Resolusi maksimum sisi terpanjang gambar feed (px). */
    private static final int MAX_FEED_SIZE = 1080;

    private String id;
    private User user;
    private String imageFile;
    private ImageIcon imagePath;
    private String caption;
    private int likes;
    private String uploadTime;
    private List<Comment> comments;
    private boolean isLiked;
    private boolean isSaved;

    public Post(String id, User user, String caption, String uploadTime, String imageFile) {
        this.id = id;
        this.user = user;
        this.caption = caption;
        this.uploadTime = uploadTime;
        this.likes = 0;
        this.comments = new ArrayList<>();
        this.imageFile = imageFile;
        this.isLiked = false;

        this.imagePath = loadImage(imageFile);
    }

    /**
     * Load dan scale gambar dengan kualitas tinggi.
     * Resolusi maks sisi terpanjang = MAX_FEED_SIZE (1080px).
     * Gambar yang sudah lebih kecil TIDAK di-upscale agar tidak buram.
     */
    private static ImageIcon loadImage(String imageFile) {
        if (imageFile == null || imageFile.isEmpty() || !new java.io.File(imageFile).exists()) {
            return ImageUtils.createDummyImage(600, 600, Color.LIGHT_GRAY);
        }

        try {
            BufferedImage original = javax.imageio.ImageIO.read(new java.io.File(imageFile));
            if (original == null) {
                return ImageUtils.createDummyImage(600, 600, Color.LIGHT_GRAY);
            }

            int origW = original.getWidth();
            int origH = original.getHeight();

            // Hitung dimensi target dengan aspect ratio terjaga
            int[] dims = ImageUtils.calcTargetDimensions(origW, origH, MAX_FEED_SIZE);
            int targetW = dims[0];
            int targetH = dims[1];

            // Progressive BICUBIC downscaling — kualitas tertinggi
            BufferedImage scaled = ImageUtils.highQualityScale(original, targetW, targetH);

            return new ImageIcon(scaled);

        } catch (Exception e) {
            return ImageUtils.createDummyImage(600, 600, Color.LIGHT_GRAY);
        }
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public ImageIcon getImagePath() { return imagePath; }
    public String getImageFile() { return imageFile; }
    public String getCaption() { return caption; }
    public void setCaption(String c) { this.caption = c; }
    public int getLikes() { return likes; }
    public String getUploadTime() { return uploadTime; }
    public List<Comment> getComments() { return comments; }
    public boolean isLiked() { return isLiked; }
    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { this.isSaved = saved; }

    /** Set likes count langsung dari nilai DB */
    public void setLikesCount(int count) { this.likes = count; }

    /** Set status liked oleh current user (tanpa mengubah likes count) */
    public void setLikedByCurrentUser(boolean liked) { this.isLiked = liked; }

    public void like() {
        if (!isLiked) { likes++; isLiked = true; }
    }

    public void unlike() {
        if (isLiked) { likes--; isLiked = false; }
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
