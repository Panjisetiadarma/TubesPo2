package com.visualgallery.view;

import com.visualgallery.controller.AuthController;
import com.visualgallery.controller.PostController;
import com.visualgallery.controller.UserController;
import com.visualgallery.model.Account;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;
import com.visualgallery.view.components.Sidebar;
import com.visualgallery.view.dashboard.UserDashboardPanel;
import com.visualgallery.view.dashboard.AdminDashboardPanel;
import com.visualgallery.view.explore.ExplorePanel;
import com.visualgallery.view.upload.UploadPanel;
import com.visualgallery.view.profile.ProfilePanel;
import com.visualgallery.view.settings.SettingsPanel;
import com.visualgallery.view.notifications.NotificationPanel;
import com.visualgallery.view.admin.UserManagementPanel;
import com.visualgallery.view.admin.PostManagementPanel;
import com.visualgallery.view.admin.StatisticsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * MainFrame - The primary application window after login.
 *
 * Hosts the sidebar navigation and content panels.
 * Implements role-based panel switching (User vs Admin).
 *
 * OOP: MVC View, Composition
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class MainFrame extends JFrame {

    private final Account         currentUser;
    private final AuthController  authController;
    private final PostController  postController;
    private final UserController  userController;
    private final ThemeManager    themeManager;

    private JPanel      contentPanel;
    private CardLayout  cardLayout;
    private Sidebar     sidebar;

    // Panel IDs
    public static final String PANEL_DASHBOARD    = "DASHBOARD";
    public static final String PANEL_EXPLORE      = "EXPLORE";
    public static final String PANEL_UPLOAD       = "UPLOAD";
    public static final String PANEL_MY_GALLERY   = "MY_GALLERY";
    public static final String PANEL_NOTIFICATIONS= "NOTIFICATIONS";
    public static final String PANEL_PROFILE      = "PROFILE";
    public static final String PANEL_SETTINGS     = "SETTINGS";
    public static final String PANEL_USER_MGMT    = "USER_MANAGEMENT";
    public static final String PANEL_POST_MGMT    = "POST_MANAGEMENT";
    public static final String PANEL_STATISTICS   = "STATISTICS";

    public MainFrame(Account currentUser) {
        this.currentUser   = currentUser;
        this.authController = new AuthController();
        this.postController = new PostController();
        this.userController = new UserController();
        this.themeManager   = ThemeManager.getInstance();

        initFrame();
        buildUI();
        navigateTo(PANEL_DASHBOARD);
    }

    private void initFrame() {
        setTitle("Visual Gallery — " + currentUser.getDisplayName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 660));
        setLocationRelativeTo(null);
        setResizable(true);
        try {
            ImageIcon icon = UIUtils.loadIcon("icons/app_icon.png", 32);
            if (icon != null) setIconImage(icon.getImage());
        } catch (Exception ignored) { }
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(themeManager.getBackground());
        setContentPane(root);

        // ─── Sidebar ───
        sidebar = new Sidebar(currentUser, this::navigateTo,
                              () -> authController.logout(MainFrame.this));
        root.add(sidebar, BorderLayout.WEST);

        // ─── Content Area (CardLayout) ───
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(themeManager.getBackground());
        root.add(contentPanel, BorderLayout.CENTER);

        // ─── Register all panels ───
        addPanels();
    }

    private void addPanels() {
        if (currentUser.isAdmin()) {
            // Admin panels
            contentPanel.add(new AdminDashboardPanel(postController, userController), PANEL_DASHBOARD);
            contentPanel.add(new UserManagementPanel(userController),                 PANEL_USER_MGMT);
            contentPanel.add(new PostManagementPanel(postController),                 PANEL_POST_MGMT);
            contentPanel.add(new StatisticsPanel(postController, userController),     PANEL_STATISTICS);
            contentPanel.add(new NotificationPanel(currentUser),                      PANEL_NOTIFICATIONS);
            contentPanel.add(new SettingsPanel(currentUser, userController, themeManager, this::refreshTheme), PANEL_SETTINGS);
        } else {
            // User panels
            contentPanel.add(new UserDashboardPanel(currentUser, postController, userController, this::navigateTo), PANEL_DASHBOARD);
            contentPanel.add(new ExplorePanel(postController, currentUser),           PANEL_EXPLORE);
            contentPanel.add(new UploadPanel(postController, this::navigateTo),       PANEL_UPLOAD);
            contentPanel.add(new ProfilePanel(currentUser, postController, userController, this::navigateTo), PANEL_MY_GALLERY);
            contentPanel.add(new NotificationPanel(currentUser),                      PANEL_NOTIFICATIONS);
            contentPanel.add(new ProfilePanel(currentUser, postController, userController, this::navigateTo), PANEL_PROFILE);
            contentPanel.add(new SettingsPanel(currentUser, userController, themeManager, this::refreshTheme), PANEL_SETTINGS);
        }
    }

    /**
     * Navigates to the specified panel by ID.
     * Called by Sidebar and internal navigation links.
     *
     * @param panelId the card layout panel identifier
     */
    public void navigateTo(String panelId) {
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(contentPanel, panelId);
            sidebar.setActiveItem(panelId);
            setTitle("Visual Gallery — " + currentUser.getDisplayName() +
                     "  |  " + getPanelTitle(panelId));
        });
    }

    /**
     * Refreshes the entire UI when theme changes.
     */
    public void refreshTheme() {
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }

    private String getPanelTitle(String panelId) {
        return switch (panelId) {
            case PANEL_DASHBOARD    -> "Dashboard";
            case PANEL_EXPLORE      -> "Explore";
            case PANEL_UPLOAD       -> "Upload";
            case PANEL_MY_GALLERY   -> "Galeri Saya";
            case PANEL_NOTIFICATIONS-> "Notifikasi";
            case PANEL_PROFILE      -> "Profil";
            case PANEL_SETTINGS     -> "Pengaturan";
            case PANEL_USER_MGMT    -> "Kelola User";
            case PANEL_POST_MGMT    -> "Kelola Postingan";
            case PANEL_STATISTICS   -> "Statistik";
            default                 -> panelId;
        };
    }
}
