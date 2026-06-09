package com.visualgallery.view.components;

import com.visualgallery.model.Post;
import com.visualgallery.controller.PostController;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;
import com.visualgallery.view.post.PostDetailDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * PostCard - UI Component for displaying a Post in a grid layout.
 *
 * Demonstrates: Component-based design, Encapsulation.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class PostCard extends JPanel {

    private final Post post;
    private final PostController postController;
    private final Consumer<String> navigateTo;
    private final ThemeManager tm = ThemeManager.getInstance();

    public PostCard(Post post, PostController postController, Consumer<String> navigateTo) {
        this.post = post;
        this.postController = postController;
        this.navigateTo = navigateTo;
        buildCard();
    }

    private void buildCard() {
        setLayout(new BorderLayout());
        setBackground(tm.getSurface());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(tm.getBorder(), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Dimension constraints to keep grid uniform
        setPreferredSize(new Dimension(300, 360));

        // ─── Thumbnail Image ───
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Load in background to prevent UI freezing
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                int size = 280;
                ImageIcon icon;
                if (post.isVideo()) {
                    icon = UIUtils.createPlaceholderIcon(size, size);
                } else {
                    String imgPath = post.getThumbnailPath() != null ? post.getThumbnailPath() : post.getMediaPath();
                    icon = UIUtils.loadScaledImage(imgPath, size, size);
                }
                return createRoundedIcon(icon, 12);
            }
            @Override
            protected void done() {
                try {
                    imageLabel.setIcon(get());
                } catch (Exception ignored) {}
            }
        };
        worker.execute();

        // ─── Info Panel (Bottom) ───
        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 4, 4, 4));

        // Uploader details (Left)
        JPanel uploaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        uploaderPanel.setOpaque(false);
        
        ImageIcon avatar = UIUtils.createCircularIcon(post.getUploaderProfilePicture(), 24);
        JLabel avatarLabel = new JLabel(avatar);
        
        JLabel nameLabel = new JLabel(post.getUploaderUsername());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(tm.getTextPrimary());
        
        uploaderPanel.add(avatarLabel);
        uploaderPanel.add(nameLabel);

        // Stats details (Right)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        statsPanel.setOpaque(false);

        JLabel typeLabel = new JLabel(post.isVideo() ? "🎥" : "📷");
        typeLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        typeLabel.setForeground(tm.getTextMuted());

        JLabel likesLabel = new JLabel("♥ " + post.getTotalLikes());
        likesLabel.setFont(UIUtils.FONT_SMALL);
        likesLabel.setForeground(tm.getTextMuted());

        JLabel commentsLabel = new JLabel("💬 " + post.getTotalComments());
        commentsLabel.setFont(UIUtils.FONT_SMALL);
        commentsLabel.setForeground(tm.getTextMuted());

        statsPanel.add(typeLabel);
        statsPanel.add(likesLabel);
        statsPanel.add(commentsLabel);

        infoPanel.add(uploaderPanel, BorderLayout.WEST);
        infoPanel.add(statsPanel, BorderLayout.EAST);

        add(imageLabel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

        // ─── Interactions ───
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPostDetail();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(tm.getBackgroundSecondary());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(tm.getSurface());
            }
        });
    }

    private ImageIcon createRoundedIcon(ImageIcon icon, int radius) {
        if (icon == null || icon.getImage() == null) return null;
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        if (w <= 0 || h <= 0) return icon;

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));
        g2.drawImage(icon.getImage(), 0, 0, null);
        g2.dispose();
        return new ImageIcon(bi);
    }

    private void showPostDetail() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof JFrame frame) {
            PostDetailDialog dialog = new PostDetailDialog(frame, post, postController);
            dialog.setVisible(true);
            
            // If post was modified or deleted while dialog was open, we might need a refresh mechanism here.
            // For now, relying on user to navigate to trigger refresh.
        }
    }
}
