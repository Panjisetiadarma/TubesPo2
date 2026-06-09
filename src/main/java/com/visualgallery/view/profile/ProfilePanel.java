package com.visualgallery.view.profile;

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
 * ProfilePanel - Displays user profile and their gallery/liked posts.
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class ProfilePanel extends JPanel {

    private final Account currentUser;
    private final PostController postController;
    private final UserController userController;
    private final Consumer<String> navigateTo;
    private final ThemeManager tm = ThemeManager.getInstance();

    private JPanel gridPanel;
    private JLabel totalPostsLabel;
    private JLabel totalLikesLabel;
    private JTabbedPane tabbedPane;

    public ProfilePanel(Account currentUser, PostController postController,
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

        // ─── Top: Profile Header ───
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        
        // Avatar
        JLabel avatarLabel = new JLabel(UIUtils.createCircularIcon(currentUser.getProfilePicture(), 120));
        avatarLabel.setBorder(new EmptyBorder(0, 0, 0, 40));
        headerPanel.add(avatarLabel, BorderLayout.WEST);

        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(currentUser.getDisplayName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        nameLabel.setForeground(tm.getTextPrimary());

        JLabel usernameLabel = new JLabel("@" + currentUser.getUsername());
        usernameLabel.setFont(UIUtils.FONT_HEADING);
        usernameLabel.setForeground(tm.getTextMuted());

        JTextArea bioArea = new JTextArea(currentUser.getBio() != null ? currentUser.getBio() : "Belum ada bio.");
        bioArea.setFont(UIUtils.FONT_BODY);
        bioArea.setForeground(tm.getTextPrimary());
        bioArea.setEditable(false);
        bioArea.setOpaque(false);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setMaximumSize(new Dimension(600, 60));

        // Stats Row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalPostsLabel = new JLabel("0 Postingan");
        totalPostsLabel.setFont(UIUtils.FONT_BOLD);
        totalPostsLabel.setForeground(tm.getTextPrimary());

        totalLikesLabel = new JLabel("0 Suka");
        totalLikesLabel.setFont(UIUtils.FONT_BOLD);
        totalLikesLabel.setForeground(tm.getTextPrimary());

        statsRow.add(totalPostsLabel);
        statsRow.add(totalLikesLabel);

        // Edit Profile Button
        JButton editBtn = UIUtils.createSecondaryButton("Edit Profil");
        editBtn.addActionListener(e -> navigateTo.accept("SETTINGS"));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(editBtn);

        infoPanel.add(nameLabel);
        infoPanel.add(usernameLabel);
        infoPanel.add(Box.createVerticalStrut(12));
        infoPanel.add(bioArea);
        infoPanel.add(Box.createVerticalStrut(16));
        infoPanel.add(statsRow);
        infoPanel.add(Box.createVerticalStrut(16));
        infoPanel.add(btnRow);

        headerPanel.add(infoPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ─── Center: Tabs (Posts, Liked) ───
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_BOLD);
        tabbedPane.setFocusable(false);
        
        // Custom Tab Style (flat)
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabsOverlapBorder", true);
        
        gridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        gridPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(new EmptyBorder(10, 30, 10, 30));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        tabbedPane.addTab("Postingan Saya", scrollPane);
        
        tabbedPane.addChangeListener(e -> refreshGallery());
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void refreshGallery() {
        gridPanel.removeAll();
        List<Post> posts;
        
        int tabIdx = tabbedPane.getSelectedIndex();
        if (tabIdx == 0) {
            posts = postController.getPostsByUser(currentUser.getId());
        } else {
            posts = postController.getLikedPostsByUser(currentUser.getId());
        }

        if (posts.isEmpty()) {
            JLabel empty = new JLabel(tabIdx == 0 ? "Belum ada postingan." : "Belum ada postingan yang disukai.");
            empty.setFont(UIUtils.FONT_BODY);
            empty.setForeground(tm.getTextMuted());
            gridPanel.add(empty);
        } else {
            for (Post p : posts) {
                gridPanel.add(new PostCard(p, postController, navigateTo));
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void loadStats() {
        int[] stats = userController.getUserStats(currentUser.getId());
        totalPostsLabel.setText(stats[0] + " Postingan");
        totalLikesLabel.setText(stats[1] + " Suka");
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            loadStats();
            refreshGallery();
        }
    }
}
