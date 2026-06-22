package model;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.image.BufferedImage;
import utils.ImageUtils;

public class User {
    /** Resolusi foto profil yang ditampilkan di UI (px, persegi). */
    private static final int PROFILE_PIC_SIZE = 120;

    private String id;
    private String username;
    private ImageIcon profilePicture;
    private String location;
    private String fullName;
    private String bio;

    public User(String id, String username, String location, String fullName, String bio, String profilePicUrl) {
        this.id = id;
        this.username = username;
        this.location = location;
        this.fullName = fullName != null ? fullName : username;
        this.bio = bio != null ? bio : "";

        loadProfilePicture(profilePicUrl);
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public ImageIcon getProfilePicture() { return profilePicture; }
    public String getLocation() { return location; }
    public String getFullName() { return fullName; }
    public String getBio() { return bio; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setBio(String bio) { this.bio = bio; }
    public void setLocation(String location) { this.location = location; }

    public void reloadProfilePicture(String profilePicUrl) {
        loadProfilePicture(profilePicUrl);
    }

    /**
     * Load dan scale foto profil menjadi persegi PROFILE_PIC_SIZE × PROFILE_PIC_SIZE
     * menggunakan high-quality progressive BICUBIC scaling dengan crop tengah.
     */
    private void loadProfilePicture(String profilePicUrl) {
        if (profilePicUrl == null || profilePicUrl.isEmpty() || !new java.io.File(profilePicUrl).exists()) {
            this.profilePicture = createInitialAvatar();
            return;
        }

        try {
            BufferedImage original = javax.imageio.ImageIO.read(new java.io.File(profilePicUrl));
            if (original == null) {
                this.profilePicture = createInitialAvatar();
                return;
            }

            int origW = original.getWidth();
            int origH = original.getHeight();

            // Hitung ukuran interim yang menjaga aspek ratio, lalu crop tengah
            // agar hasilnya persegi tanpa distorsi (cover mode seperti Instagram)
            int interimSize;
            if (origW < origH) {
                // portrait: lebarkan ke PROFILE_PIC_SIZE, tinggi proporsional
                interimSize = PROFILE_PIC_SIZE;
                int interimH = (int) ((double) origH / origW * interimSize);
                BufferedImage interim = ImageUtils.highQualityScale(original, interimSize, interimH);
                // Crop vertikal tengah
                int cropY = (interimH - PROFILE_PIC_SIZE) / 2;
                this.profilePicture = new ImageIcon(interim.getSubimage(0, cropY, PROFILE_PIC_SIZE, PROFILE_PIC_SIZE));
            } else {
                // landscape atau square: tinggikan ke PROFILE_PIC_SIZE, lebar proporsional
                interimSize = PROFILE_PIC_SIZE;
                int interimW = (int) ((double) origW / origH * interimSize);
                BufferedImage interim = ImageUtils.highQualityScale(original, interimW, interimSize);
                // Crop horizontal tengah
                int cropX = (interimW - PROFILE_PIC_SIZE) / 2;
                this.profilePicture = new ImageIcon(interim.getSubimage(cropX, 0, PROFILE_PIC_SIZE, PROFILE_PIC_SIZE));
            }

        } catch (Exception e) {
            this.profilePicture = createInitialAvatar();
        }
    }

    
    //  Buat avatar placeholder bergradien dengan inisial nama sebagai fallback.
     
    private ImageIcon createInitialAvatar() {
        int size = PROFILE_PIC_SIZE;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);

        // Gradien latar berdasarkan hash nama — setiap user warna unik
        int hue = Math.abs((username != null ? username.hashCode() : 0) % 360);
        java.awt.Color c1 = java.awt.Color.getHSBColor(hue / 360f, 0.55f, 0.80f);
        java.awt.Color c2 = java.awt.Color.getHSBColor(((hue + 40) % 360) / 360f, 0.65f, 0.65f);
        java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, c1, size, size, c2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, size, size);

        // Inisial huruf besar
        String initials = (username != null && !username.isEmpty())
            ? String.valueOf(username.charAt(0)).toUpperCase() : "?";
        g2.setColor(Color.WHITE);
        g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, size / 2));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int tx = (size - fm.stringWidth(initials)) / 2;
        int ty = (size + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(initials, tx, ty);

        g2.dispose();
        return new ImageIcon(img);
    }
}
