package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import controller.PostController;
import controller.UserController;
import utils.Theme;

public class MainFrame extends JFrame {
    private java.awt.CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JPanel topHeader;

    public MainFrame() {
        setTitle("PixView");
        setSize(1200, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);
        
        setLayout(new BorderLayout());

        UserController userController = new UserController();
        PostController postController = new PostController(userController);

        SidebarPanel sidebar = new SidebarPanel(userController.getCurrentUser().getProfilePicture());
        add(sidebar, BorderLayout.WEST);

        cardLayout = new java.awt.CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // PERSISTENT TOP HEADER
        JPanel topHeaderWrapper = new JPanel(new BorderLayout());
        topHeaderWrapper.setBackground(Theme.BACKGROUND);

        // HOME VIEW
        JPanel homeView = new JPanel(new BorderLayout());
        homeView.setBackground(Theme.BACKGROUND);
        
        FeedPanel feed = new FeedPanel(postController.getPosts());
        homeView.add(feed, BorderLayout.CENTER);
        mainContentPanel.add(homeView, "HOME");

        // Header Top 
        topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(Theme.BACKGROUND);
        topHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 50, 20, 50));
        
        // Left side: Title and Subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new javax.swing.BoxLayout(titlePanel, javax.swing.BoxLayout.Y_AXIS));
        titlePanel.setBackground(Theme.BACKGROUND);
        
        javax.swing.JLabel titleLabel = new javax.swing.JLabel("Beranda");
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        titleLabel.setForeground(Theme.TEXT);
        titleLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        javax.swing.JLabel subtitleLabel = new javax.swing.JLabel("Lihat apa yang baru dari komunitas.");
        subtitleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        subtitleLabel.setForeground(Theme.TEXT_MUTED);
        subtitleLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(javax.swing.Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        // Right side: Search bar and icons
        JPanel rightActions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 15, 0));
        rightActions.setBackground(Theme.BACKGROUND);
        
        // Search bar removed as requested
        
        // Profile Icon
        component.CircleImagePanel profileImg = new component.CircleImagePanel(userController.getCurrentUser().getProfilePicture(), 40);
        profileImg.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        profileImg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sidebar.setActiveMenu("Profil");
                showHomeProfile();
            }
        });
        
        rightActions.add(profileImg);
        
        // topHeader.add(titlePanel, java.awt.BorderLayout.WEST);
        topHeader.add(rightActions, java.awt.BorderLayout.EAST);
        
        topHeaderWrapper.add(topHeader, BorderLayout.NORTH);
        topHeaderWrapper.add(mainContentPanel, BorderLayout.CENTER);

        // -- PROFILE VIEW --
        ProfilePanel profilePanel = new ProfilePanel(userController.getCurrentUser());
        mainContentPanel.add(profilePanel, "PROFILE");

        add(topHeaderWrapper, BorderLayout.CENTER);
        
        sidebar.setOnMenuClick(menuText -> {
            if ("Beranda".equals(menuText)) {
                feed.refresh(postController.getPosts());
                showHome();
            } else if ("Profil".equals(menuText)) {
                showHomeProfile();
            } else if ("Logout".equals(menuText)) {
                utils.Session.setCurrentUser(null);
                dispose();
                new LoginFrame().setVisible(true);
            } else if ("Posting".equals(menuText)) {
                profilePanel.openUploadDialog();
            } else if ("Disukai".equals(menuText)) {
                sidebar.setActiveMenu("Disukai");
                showHomeProfile();
                profilePanel.selectTab(TabBar.Tab.DISUKAI);
            } else if ("Tersimpan".equals(menuText)) {
                sidebar.setActiveMenu("Tersimpan");
                showHomeProfile();
                profilePanel.selectTab(TabBar.Tab.FAVORIT);
            }
        });
    }

    public void showHome() {
        if (topHeader != null) topHeader.setVisible(true);
        cardLayout.show(mainContentPanel, "HOME");
    }

    public void showHomeProfile() {
        if (topHeader != null) topHeader.setVisible(false);
        cardLayout.show(mainContentPanel, "PROFILE");
    }

    public void showUserProfile(model.User user) {
        // Hapus panel user profile lama jika ada, untuk mencegah memory leak
        try {
            java.awt.Component existing = null;
            for (java.awt.Component c : mainContentPanel.getComponents()) {
                if (c.getName() != null && c.getName().startsWith("USER_PROFILE_")) {
                    existing = c;
                    break;
                }
            }
            if (existing != null) {
                mainContentPanel.remove(existing);
            }
        } catch (Exception ignored) {}
        
        ProfilePanel userProfile = new ProfilePanel(user);
        userProfile.setName("USER_PROFILE_" + user.getUsername());
        mainContentPanel.add(userProfile, "USER_PROFILE_" + user.getUsername());
        if (topHeader != null) topHeader.setVisible(false);
        cardLayout.show(mainContentPanel, "USER_PROFILE_" + user.getUsername());
    }
}
