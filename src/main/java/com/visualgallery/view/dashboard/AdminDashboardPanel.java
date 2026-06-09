package com.visualgallery.view.dashboard;

import com.visualgallery.controller.PostController;
import com.visualgallery.controller.UserController;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AdminDashboardPanel - The main home screen for Admin users.
 *
 * Shows system-wide statistics (total users, posts, likes, etc.).
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class AdminDashboardPanel extends JPanel {

    private final PostController postController;
    private final UserController userController;
    private final ThemeManager tm = ThemeManager.getInstance();

    private JPanel statsGrid;

    public AdminDashboardPanel(PostController postController, UserController userController) {
        this.postController = postController;
        this.userController = userController;
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

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(tm.getTextPrimary());

        JLabel subtitleLabel = new JLabel("Ringkasan statistik sistem Visual Gallery.");
        subtitleLabel.setFont(UIUtils.FONT_BODY);
        subtitleLabel.setForeground(tm.getTextMuted());

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 5));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(subtitleLabel);

        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh Data");
        refreshBtn.addActionListener(e -> refreshStats());

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ─── Center Content (Stats Grid) ───
        statsGrid = new JPanel(new GridLayout(3, 3, 20, 20));
        statsGrid.setOpaque(false);
        
        refreshStats();

        JScrollPane scrollPane = new JScrollPane(statsGrid);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshStats() {
        statsGrid.removeAll();
        
        // Fetch data
        int totalUsers    = userController.getTotalUsers();
        int totalAdmins   = userController.getTotalAdmins();
        int totalPosts    = postController.getTotalPosts();
        int totalPhotos   = postController.getTotalPhotos();
        int totalVideos   = postController.getTotalVideos();
        int totalLikes    = postController.getTotalLikes();
        int totalComments = postController.getTotalComments();

        // Build Cards
        statsGrid.add(buildStatCard("Total User", String.valueOf(totalUsers), "👥"));
        statsGrid.add(buildStatCard("Total Admin", String.valueOf(totalAdmins), "🛡️"));
        statsGrid.add(buildStatCard("Total Postingan", String.valueOf(totalPosts), "🖼️"));
        
        statsGrid.add(buildStatCard("Total Foto", String.valueOf(totalPhotos), "📷"));
        statsGrid.add(buildStatCard("Total Video", String.valueOf(totalVideos), "🎥"));
        statsGrid.add(buildStatCard("Total Interaksi", String.valueOf(totalLikes + totalComments), "⚡"));
        
        statsGrid.add(buildStatCard("Total Like", String.valueOf(totalLikes), "♥"));
        statsGrid.add(buildStatCard("Total Komentar", String.valueOf(totalComments), "💬"));
        
        // Empty panel for alignment
        JPanel empty = new JPanel();
        empty.setOpaque(false);
        statsGrid.add(empty);

        statsGrid.revalidate();
        statsGrid.repaint();
    }

    private JPanel buildStatCard(String title, String value, String icon) {
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIUtils.FONT_BODY);
        titleLabel.setForeground(tm.getTextMuted());

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valLabel.setForeground(tm.getTextPrimary());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.WEST);
        top.add(iconLabel, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.SOUTH);

        return card;
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) refreshStats();
    }
}
