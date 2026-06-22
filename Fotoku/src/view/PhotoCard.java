package view;

import model.Photo;
import utils.ProfileTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import javax.imageio.ImageIO;

public class PhotoCard extends JPanel {
    private final Photo photo;
    private boolean hovered = false;
    private boolean liked = false;
    private boolean saved = false;
    private Image cachedImage; // Cache gambar agar tidak baca disk setiap repaint

    public PhotoCard(Photo photo) {
        this.photo = photo;
        this.liked = photo.isLiked();
        this.saved = photo.isSaved();
        setOpaque(false);
        setPreferredSize(new Dimension(0, 200));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setLayout(null);

        // Load image sekali saat konstruktor, bukan di paintComponent
        loadCachedImage();

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleLike();
                } else if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                    SwingUtilities.invokeLater(() -> showImageModal());
                }
            }
        });
    }

    private void loadCachedImage() {
        if (photo.getImagePath() != null && new File(photo.getImagePath()).exists()) {
            try {
                cachedImage = ImageIO.read(new File(photo.getImagePath()));
            } catch (Exception e) {
                cachedImage = null;
            }
        }
    }

    public void toggleLike() {
        liked = !liked;
        photo.setLiked(liked);
        if (liked) photo.setLikes(photo.getLikes() + 1);
        else photo.setLikes(photo.getLikes() - 1);
        
        // Update DB
        try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
            java.sql.PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            psUser.setString(1, utils.Session.getCurrentUser());
            java.sql.ResultSet rs = psUser.executeQuery();
            if (rs.next()) {
                int uid = rs.getInt("id");
                if (liked) {
                    java.sql.PreparedStatement psLike = conn.prepareStatement("INSERT IGNORE INTO post_likes (user_id, post_id) VALUES (?, ?)");
                    psLike.setInt(1, uid);
                    psLike.setInt(2, photo.getId());
                    psLike.executeUpdate();
                } else {
                    java.sql.PreparedStatement psLike = conn.prepareStatement("DELETE FROM post_likes WHERE user_id = ? AND post_id = ?");
                    psLike.setInt(1, uid);
                    psLike.setInt(2, photo.getId());
                    psLike.executeUpdate();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        repaint();
    }

    public void toggleSave() {
        saved = !saved;
        photo.setSaved(saved);
        
        try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
            java.sql.PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            psUser.setString(1, utils.Session.getCurrentUser());
            java.sql.ResultSet rs = psUser.executeQuery();
            if (rs.next()) {
                int uid = rs.getInt("id");
                if (saved) {
                    java.sql.PreparedStatement psSave = conn.prepareStatement("INSERT IGNORE INTO post_saves (user_id, post_id) VALUES (?, ?)");
                    psSave.setInt(1, uid);
                    psSave.setInt(2, photo.getId());
                    psSave.executeUpdate();
                } else {
                    java.sql.PreparedStatement psSave = conn.prepareStatement("DELETE FROM post_saves WHERE user_id = ? AND post_id = ?");
                    psSave.setInt(1, uid);
                    psSave.setInt(2, photo.getId());
                    psSave.executeUpdate();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        repaint();
    }

    private void showImageModal() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent instanceof Frame ? (Frame)parent : null, "Detail Foto", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(900, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parent);
        dialog.getContentPane().setBackground(Color.WHITE);

        // --- Left Panel: Image ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.BLACK);
        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        if (cachedImage != null) {
            imgLabel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    int w = leftPanel.getWidth();
                    int h = leftPanel.getHeight();
                    if (w > 0 && h > 0) {
                        int imgW = cachedImage.getWidth(null);
                        int imgH = cachedImage.getHeight(null);
                        double scale = Math.min((double)w/imgW, (double)h/imgH);
                        int drawW = (int)(imgW * scale);
                        int drawH = (int)(imgH * scale);
                        imgLabel.setIcon(new ImageIcon(cachedImage.getScaledInstance(drawW, drawH, Image.SCALE_SMOOTH)));
                    }
                }
            });
        } else {
            imgLabel.setText("Foto tidak tersedia");
            imgLabel.setForeground(Color.WHITE);
        }
        leftPanel.add(imgLabel, BorderLayout.CENTER);
        dialog.add(leftPanel, BorderLayout.CENTER);

        // --- Right Panel: Info & Comments ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(230, 230, 230)));

        // Right Header
        JPanel rightHeader = new JPanel(new BorderLayout());
        rightHeader.setBackground(Color.WHITE);
        rightHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Fetch owner of this post to see if we show delete button
        String ownerUsername = "";
        try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
            java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT u.username FROM posts p JOIN users u ON p.user_id = u.id WHERE p.id = ?");
            ps.setInt(1, photo.getId());
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) ownerUsername = rs.getString("username");
        } catch (Exception ex) { ex.printStackTrace(); }

        JLabel lblOwner = new JLabel(ownerUsername.isEmpty() ? "Unknown User" : ownerUsername);
        lblOwner.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rightHeader.add(lblOwner, BorderLayout.CENTER);

        if (ownerUsername.equals(utils.Session.getCurrentUser())) {
            JButton btnDelete = new JButton("Hapus");
            btnDelete.setForeground(Color.RED);
            btnDelete.setContentAreaFilled(false);
            btnDelete.setBorderPainted(false);
            btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDelete.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dialog, "Yakin ingin menghapus foto ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                        java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM posts WHERE id = ?");
                        ps.setInt(1, photo.getId());
                        ps.executeUpdate();
                        dialog.dispose();
                        PhotoCard.this.setVisible(false);
                        if (PhotoCard.this.getParent() != null) {
                            PhotoCard.this.getParent().revalidate();
                            PhotoCard.this.getParent().repaint();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Gagal menghapus: " + ex.getMessage());
                    }
                }
            });
            rightHeader.add(btnDelete, BorderLayout.EAST);
        }
        rightPanel.add(rightHeader, BorderLayout.NORTH);

        // Comments Area
        JPanel commentsContainer = new JPanel();
        commentsContainer.setLayout(new BoxLayout(commentsContainer, BoxLayout.Y_AXIS));
        commentsContainer.setBackground(Color.WHITE);
        JScrollPane commentsScroll = new JScrollPane(commentsContainer);
        commentsScroll.setBorder(null);
        commentsScroll.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(commentsScroll, BorderLayout.CENTER);

        // Load comments
        Runnable loadComments = () -> {
            commentsContainer.removeAll();
            try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                // Post caption
                java.sql.PreparedStatement psCap = conn.prepareStatement(
                    "SELECT u.username, p.caption FROM posts p JOIN users u ON p.user_id = u.id WHERE p.id = ?");
                psCap.setInt(1, photo.getId());
                java.sql.ResultSet rsCap = psCap.executeQuery();
                if (rsCap.next()) {
                    String cap = rsCap.getString("caption");
                    if (cap != null && !cap.trim().isEmpty()) {
                        JPanel pCap = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
                        pCap.setBackground(Color.WHITE);
                        pCap.add(new JLabel("<html><b>" + rsCap.getString("username") + "</b> " + cap + "</html>"));
                        commentsContainer.add(pCap);
                    }
                }
                
                // Comments
                java.sql.PreparedStatement ps = conn.prepareStatement(
                    "SELECT c.text, u.username FROM comments c JOIN users u ON c.user_id = u.id WHERE c.post_id = ? ORDER BY c.created_at ASC");
                ps.setInt(1, photo.getId());
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
                    p.setBackground(Color.WHITE);
                    p.add(new JLabel("<html><b>" + rs.getString("username") + "</b> " + rs.getString("text") + "</html>"));
                    commentsContainer.add(p);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
            commentsContainer.revalidate();
            commentsContainer.repaint();
            // Scroll to bottom
            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = commentsScroll.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });
        };
        loadComments.run();

        // Right Footer (Likes & Add Comment)
        JPanel rightFooter = new JPanel();
        rightFooter.setLayout(new BoxLayout(rightFooter, BoxLayout.Y_AXIS));
        rightFooter.setBackground(Color.WHITE);
        rightFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setBackground(Color.WHITE);
        actionRow.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftActions.setBackground(Color.WHITE);
        
        String iconDir = new java.io.File("Icon").exists() ? "Icon/" : "../Icon/";
        ImageIcon likeBeforeImg = utils.ImageUtils.loadIcon(iconDir + "LikeIconBefore.png", 22, 22);
        ImageIcon likeAfterImg = utils.ImageUtils.loadIcon(iconDir + "likeIconAfter.png", 22, 22);
        ImageIcon saveBeforeImg = utils.ImageUtils.loadIcon(iconDir + "PavoriteIconBefore.png", 22, 22);
        ImageIcon saveAfterImg = utils.ImageUtils.loadIcon(iconDir + "PavoriteIconAfter.png", 22, 22);

        JLabel btnLike = new JLabel(photo.isLiked() ? likeAfterImg : likeBeforeImg);
        btnLike.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblLikes = new JLabel(photo.getLikes() + " suka");
        lblLikes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnLike.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleLike();
                btnLike.setIcon(photo.isLiked() ? likeAfterImg : likeBeforeImg);
                lblLikes.setText(photo.getLikes() + " suka");
                if(getParent() != null) getParent().repaint();
            }
        });
        
        leftActions.add(btnLike);
        leftActions.add(lblLikes);
        
        JLabel btnSave = new JLabel(photo.isSaved() ? saveAfterImg : saveBeforeImg);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSave();
                btnSave.setIcon(photo.isSaved() ? saveAfterImg : saveBeforeImg);
                if(getParent() != null) getParent().repaint();
            }
        });
        
        actionRow.add(leftActions, BorderLayout.WEST);
        actionRow.add(btnSave, BorderLayout.EAST);
        
        rightFooter.add(actionRow);

        JPanel commentInputRow = new JPanel(new BorderLayout());
        commentInputRow.setBackground(Color.WHITE);
        commentInputRow.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        JTextField txtComment = new JTextField();
        txtComment.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JButton btnSend = new JButton("Kirim");
        btnSend.setBackground(Color.WHITE);
        btnSend.setForeground(new Color(0, 149, 246));
        btnSend.setBorderPainted(false);
        btnSend.setContentAreaFilled(false);
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSend.addActionListener(e -> {
            String text = txtComment.getText().trim();
            if (!text.isEmpty()) {
                try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                    java.sql.PreparedStatement psU = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
                    psU.setString(1, utils.Session.getCurrentUser());
                    java.sql.ResultSet rsU = psU.executeQuery();
                    if (rsU.next()) {
                        int uid = rsU.getInt("id");
                        java.sql.PreparedStatement ps = conn.prepareStatement("INSERT INTO comments (post_id, user_id, text) VALUES (?, ?, ?)");
                        ps.setInt(1, photo.getId());
                        ps.setInt(2, uid);
                        ps.setString(3, text);
                        ps.executeUpdate();
                        txtComment.setText("");
                        loadComments.run();
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
        commentInputRow.add(txtComment, BorderLayout.CENTER);
        commentInputRow.add(btnSend, BorderLayout.EAST);
        
        rightFooter.add(commentInputRow);
        rightPanel.add(rightFooter, BorderLayout.SOUTH);

        dialog.add(rightPanel, BorderLayout.EAST);
        dialog.setVisible(true);
    }

    @Override
    public Dimension getPreferredSize() {
        int w = getWidth();
        if (w == 0) return new Dimension(200, 150);
        return new Dimension(w, w * 3 / 4);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int arc = 8;

        // Card background color or cached image (NO disk I/O here!)
        if (cachedImage != null) {
            g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
            
            int imgW = cachedImage.getWidth(null);
            int imgH = cachedImage.getHeight(null);
            double scaleX = (double) w / imgW;
            double scaleY = (double) h / imgH;
            double scale = Math.max(scaleX, scaleY);
            
            int drawW = (int) (imgW * scale);
            int drawH = (int) (imgH * scale);
            int x = (w - drawW) / 2;
            int y = (h - drawH) / 2;
            
            g2.drawImage(cachedImage, x, y, drawW, drawH, null);
            g2.setClip(null);
        } else {
            g2.setColor(photo.getColor());
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
        }

        // Hover overlay (dark semi-transparent + icons)
        if (hovered) {
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

            // Like count icon
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            String likeStr = "♥ " + photo.getLikes();
            String favStr = photo.isSaved() ? "★ Tersimpan" : "";
            FontMetrics fm = g2.getFontMetrics();
            int totalW = fm.stringWidth(likeStr) + (favStr.isEmpty() ? 0 : fm.stringWidth("  " + favStr));
            int x = (w - totalW) / 2;
            int y = h / 2 + fm.getAscent() / 2;
            g2.drawString(likeStr, x, y);
            if (!favStr.isEmpty()) {
                g2.drawString("  " + favStr, x + fm.stringWidth(likeStr), y);
            }
        }

        // Liked indicator
        if (liked) {
            g2.setColor(new Color(255, 100, 100, 200));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            g2.drawString("♥", 6, 24);
        }

        // Photo name badge
        String label = photo.getName();
        g2.setFont(ProfileTheme.FONT_LABEL);
        FontMetrics fm = g2.getFontMetrics();
        int labelW = fm.stringWidth(label) + 12;
        int labelH = fm.getHeight() + 4;
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(6, h - labelH - 8, labelW, labelH, 4, 4);
        g2.setColor(Color.WHITE);
        g2.drawString(label, 12, h - 8 - fm.getDescent() + 1);

        g2.dispose();
    }
}
