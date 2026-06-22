package model;

import java.awt.Color;

public class Photo {
    private int id;
    private String name;
    private Color color;
    private boolean favorite;
    private boolean saved;
    private boolean liked;
    private int likes;
    private String imagePath;

    private static final Color[] PALETTE = {
        new Color(200, 180, 220),
        new Color(180, 210, 200),
        new Color(220, 200, 180),
        new Color(180, 200, 230),
        new Color(230, 210, 180),
        new Color(200, 230, 200),
        new Color(230, 180, 200),
        new Color(210, 220, 240),
        new Color(240, 220, 200),
        new Color(200, 240, 220),
        new Color(220, 200, 240),
        new Color(240, 200, 210),
    };

    public Photo(int id, String name, int likes, String imagePath) {
        this.id = id;
        this.name = name;
        this.color = PALETTE[id % PALETTE.length];
        this.favorite = false;
        this.saved = false;
        this.liked = false;
        this.likes = likes;
        this.imagePath = imagePath;
    }
    
    public int getId() { return id; }

    public String getName() { return name; }
    public Color getColor() { return color; }
    public boolean isFavorite() { return favorite; }
    public boolean isSaved() { return saved; }
    public boolean isLiked() { return liked; }
    public int getLikes() { return likes; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String path) { this.imagePath = path; }
    public void setFavorite(boolean f) { this.favorite = f; }
    public void setSaved(boolean s) { this.saved = s; }
    public void setLiked(boolean l) { this.liked = l; }
    public void setLikes(int l) { this.likes = l; }
    
    private boolean ownPost;
    public boolean isOwnPost() { return ownPost; }
    public void setOwnPost(boolean o) { this.ownPost = o; }
}
