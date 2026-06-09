package com.visualgallery.view.dashboard;

import com.visualgallery.controller.PostController;
import com.visualgallery.controller.UserController;
import com.visualgallery.model.Account;
import com.visualgallery.model.Post;
import com.visualgallery.model.User;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;
import com.visualgallery.utils.WrapLayout;
import com.visualgallery.view.components.PostCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * UserDashboardPanel - The main home screen for regular users.
 *
 * Shows user stats and their most recent uploads.
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class UserDashboardPanel extends JPanel {

    private final Account currentUser;
    private final PostController postController;
    private final UserController userController;
    private final Consumer<String> navigateTo;
    private final ThemeManager tm = ThemeManager.getInstance();

    private JPanel statsPanel;
    private JPanel recentUploadsGrid;

    public UserDashboardPanel(Account currentUser, PostController postController,
                              UserController userController, Consumer<String> navigateTo) {
        this.currentUser = currentUser;
        this.postController = postController;
        this.userController = userController;
        this.navigateTo = navigateTo;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(tm.getBackground());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ─── Header ───
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel welcomeLabel = new JLabel("Selamat Datang, " + currentUser.getDisplayName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(tm.getTextPrimary());

        JLabel subtitleLabel = new JLabel("Berikut adalah ringkasan aktivitas galeri Anda.");
        subtitleLabel.setFont(UIUtils.FONT_BODY);
        subtitleLabel.setForeground(tm.getTextMuted());

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 5));
        titleBox.setOpaque(false);
        titleBox.add(welcomeLabel);
        titleBox.add(subtitleLabel);

        JButton uploadBtn = UIUtils.createPrimaryButton("+ Upload Baru");
        uploadBtn.addActionListener(e -> navigateTo.accept("UPLOAD"));

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(uploadBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ─── Center Content (Stats & Recent) ───
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // 1. Stats Cards
        statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        refreshStats(); // Load initial stats
        centerPanel.add(statsPanel);
        centerPanel.add(Box.createVerticalStrut(40));

        // 2. Recent Uploads Header
        JLabel recentLabel = new JLabel("Upload Terbaru Anda");
        recentLabel.setFont(UIUtils.FONT_HEADING);
        recentLabel.setForeground(tm.getTextPrimary());
        recentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(recentLabel);
        centerPanel.add(Box.createVerticalStrut(16));

        // 3. Recent Uploads Grid
        recentUploadsGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        recentUploadsGrid.setOpaque(false);
        recentUploadsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshRecentUploads();

        JScrollPane scrollPane = new JScrollPane(recentUploadsGrid);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void refreshStats() {
        statsPanel.removeAll();
        int[] stats = userController.getUserStats(currentUser.getId());
        // stats = [uploads, likes, comments]
        
        statsPanel.add(buildStatCard("Total Upload", String.valueOf(stats[0]), "📷"));
        statsPanel.add(buildStatCard("Total Like Diterima", String.valueOf(stats[1]), "♥"));
        statsPanel.add(buildStatCard("Total Komentar", String.valueOf(stats[2]), "💬"));
        
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private JPanel buildStatCard(String title, String value, String icon) {
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(ThemeManager.ACCENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIUtils.FONT_SMALL);
        titleLabel.setForeground(tm.getTextMuted());

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valLabel.setForeground(tm.getTextPrimary());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.WEST);
        top.add(iconLabel, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.SOUTH);

        return card;
    }

    public void refreshRecentUploads() {
        recentUploadsGrid.removeAll();
        List<Post> recent = postController.getPostsByUser(currentUser.getId());
        
        if (recent.isEmpty()) {
            JLabel empty = new JLabel("Anda belum mengupload apa pun.");
            empty.setFont(UIUtils.FONT_BODY);
            empty.setForeground(tm.getTextMuted());
            recentUploadsGrid.add(empty);
        } else {
            // Show up to 4 recent
            int count = Math.min(recent.size(), 4);
            for (int i = 0; i < count; i++) {
                recentUploadsGrid.add(new PostCard(recent.get(i), postController, navigateTo));
            }
        }
        recentUploadsGrid.revalidate();
        recentUploadsGrid.repaint();
    }

    // Called when the card is shown
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            refreshStats();
            refreshRecentUploads();
        }
    }
}
