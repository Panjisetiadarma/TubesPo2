package com.visualgallery.view.components;

import com.visualgallery.model.Account;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;
import com.visualgallery.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Sidebar - Navigation sidebar component.
 *
 * Shows navigation items based on the user's role.
 * Highlights the active panel and calls navigateTo on click.
 *
 * OOP: Composition, Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class Sidebar extends JPanel {

    private final Account          currentUser;
    private final Consumer<String> navigateTo;
    private final Runnable         onLogout;
    private final ThemeManager     tm;

    private final Map<String, JButton> navItems = new LinkedHashMap<>();
    private String activePanel = "";

    public Sidebar(Account currentUser, Consumer<String> navigateTo, Runnable onLogout) {
        this.currentUser = currentUser;
        this.navigateTo  = navigateTo;
        this.onLogout    = onLogout;
        this.tm          = ThemeManager.getInstance();
        buildSidebar();
    }

    private void buildSidebar() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x1A1A1A));
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x2A2A2A)));

        // ─── Top: App Logo ───
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(0x111111));
        logoPanel.setBorder(new EmptyBorder(20, 20, 20, 16));
        logoPanel.setPreferredSize(new Dimension(220, 72));

        JLabel appName = new JLabel("Visual Gallery");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(ThemeManager.ACCENT);

        JLabel roleLabel = new JLabel(currentUser.getRole());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleLabel.setForeground(new Color(0x666666));

        JPanel logoText = new JPanel(new GridLayout(2, 1));
        logoText.setOpaque(false);
        logoText.add(appName);
        logoText.add(roleLabel);
        logoPanel.add(logoText, BorderLayout.CENTER);

        add(logoPanel, BorderLayout.NORTH);

        // ─── Navigation Items ───
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(0x1A1A1A));
        navPanel.setBorder(new EmptyBorder(12, 0, 12, 0));

        buildNavItems(navPanel);

        JScrollPane navScroll = new JScrollPane(navPanel);
        navScroll.setBorder(null);
        navScroll.setOpaque(false);
        navScroll.getViewport().setOpaque(false);
        navScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(navScroll, BorderLayout.CENTER);

        // ─── Bottom: User Avatar + Logout ───
        JPanel bottomPanel = buildBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void buildNavItems(JPanel parent) {
        if (currentUser.isAdmin()) {
            addNavItem(parent, "📊", "Dashboard",          MainFrame.PANEL_DASHBOARD);
            addNavItem(parent, "👥", "Kelola User",        MainFrame.PANEL_USER_MGMT);
            addNavItem(parent, "🖼", "Kelola Postingan",   MainFrame.PANEL_POST_MGMT);
            addNavItem(parent, "📈", "Statistik",          MainFrame.PANEL_STATISTICS);
            addNavItem(parent, "🔔", "Notifikasi",         MainFrame.PANEL_NOTIFICATIONS);
            addDivider(parent);
            addNavItem(parent, "⚙", "Pengaturan",         MainFrame.PANEL_SETTINGS);
        } else {
            addNavItem(parent, "🏠", "Beranda",            MainFrame.PANEL_DASHBOARD);
            addNavItem(parent, "🔍", "Explore",            MainFrame.PANEL_EXPLORE);
            addNavItem(parent, "⬆", "Upload",             MainFrame.PANEL_UPLOAD);
            addNavItem(parent, "🖼", "Galeri Saya",        MainFrame.PANEL_MY_GALLERY);
            addNavItem(parent, "🔔", "Notifikasi",         MainFrame.PANEL_NOTIFICATIONS);
            addDivider(parent);
            addNavItem(parent, "👤", "Profil",             MainFrame.PANEL_PROFILE);
            addNavItem(parent, "⚙", "Pengaturan",         MainFrame.PANEL_SETTINGS);
        }
    }

    private void addNavItem(JPanel parent, String icon, String label, String panelId) {
        JButton item = new JButton(icon + "   " + label);
        item.setHorizontalAlignment(SwingConstants.LEFT);
        item.setFont(new Font("Segoe UI", Font.BOLD, 13));
        item.setForeground(new Color(0x888888));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        item.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
            "arc: 20;" +
            "margin: 8,16,8,16;" +
            "borderWidth: 0;" +
            "focusWidth: 0;" +
            "background: null;" + 
            "hoverBackground: #2A2A2A;" +
            "pressedBackground: #333333;"
        );
        
        item.addActionListener(e -> navigateTo.accept(panelId));

        navItems.put(panelId, item);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(4, 16, 4, 16));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        wrapper.add(item, BorderLayout.CENTER);
        
        parent.add(wrapper);
    }

    private void addDivider(JPanel parent) {
        JPanel divider = new JPanel();
        divider.setBackground(new Color(0x2A2A2A));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setBorder(new EmptyBorder(0, 16, 0, 16));
        parent.add(Box.createVerticalStrut(8));
        parent.add(divider);
        parent.add(Box.createVerticalStrut(8));
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setBackground(new Color(0x111111));
        bottom.setBorder(new EmptyBorder(16, 16, 16, 16));
        bottom.setPreferredSize(new Dimension(220, 110));

        // User info row
        JPanel userRow = new JPanel(new BorderLayout(10, 0));
        userRow.setOpaque(false);

        // Avatar
        ImageIcon avatar = UIUtils.createCircularIcon(currentUser.getProfilePicture(), 36);
        JLabel avatarLabel = new JLabel(avatar);

        // Name + role
        JPanel namePanel = new JPanel(new GridLayout(2, 1));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(currentUser.getDisplayName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new Color(0xDDDDDD));

        JLabel roleLabel = new JLabel("@" + currentUser.getUsername());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleLabel.setForeground(new Color(0x666666));

        namePanel.add(nameLabel);
        namePanel.add(roleLabel);

        userRow.add(avatarLabel, BorderLayout.WEST);
        userRow.add(namePanel, BorderLayout.CENTER);
        bottom.add(userRow, BorderLayout.CENTER);

        // Logout button
        JButton logoutBtn = new JButton("Keluar");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoutBtn.setForeground(new Color(0x888888));
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            if (UIUtils.showConfirm(SwingUtilities.getWindowAncestor(this),
                    "Apakah Anda yakin ingin keluar?")) {
                onLogout.run();
            }
        });
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { logoutBtn.setForeground(ThemeManager.ERROR); }
            @Override public void mouseExited(MouseEvent e)  { logoutBtn.setForeground(new Color(0x888888)); }
        });
        bottom.add(logoutBtn, BorderLayout.SOUTH);

        return bottom;
    }

    /**
     * Highlights the active sidebar item.
     *
     * @param panelId the active panel ID
     */
    public void setActiveItem(String panelId) {
        if (navItems.containsKey(activePanel)) {
            JButton prev = navItems.get(activePanel);
            prev.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
                "arc: 20; margin: 8,16,8,16; borderWidth: 0; focusWidth: 0; background: null; hoverBackground: #2A2A2A; pressedBackground: #333333;");
            prev.setForeground(new Color(0x888888));
        }
        activePanel = panelId;

        if (navItems.containsKey(panelId)) {
            JButton active = navItems.get(panelId);
            active.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
                "arc: 20; margin: 8,16,8,16; borderWidth: 0; focusWidth: 0; background: #333333; hoverBackground: #333333;");
            active.setForeground(Color.WHITE);
        }
    }
}
