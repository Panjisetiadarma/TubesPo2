package view;
import utils.Theme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.List;

public class SidebarPanel extends JPanel {
    
    private List<JLabel> textLabels = new ArrayList<>();
    private List<MenuItemPanel> menuItems = new ArrayList<>();
    private boolean isExpanded = true;
    private JLabel logoLabel;
    private JLabel logoIconPanel;
    private JPanel menuPanel;
    private JPanel bottomPanel;
    private java.util.function.Consumer<String> onMenuClick;

    public void setOnMenuClick(java.util.function.Consumer<String> onMenuClick) {
        this.onMenuClick = onMenuClick;
    }

    public SidebarPanel(javax.swing.ImageIcon profilePic) {
        setBackground(Theme.CARD);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));
        setPreferredSize(new Dimension(250, 0));
        setLayout(new BorderLayout());

        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Theme.CARD);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));

        logoLabel = new JLabel("Fotoku");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(Theme.TEXT);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String iconDir = new java.io.File("Icon").exists() ? "Icon/" : "../Icon/";
        ImageIcon logoImage = utils.ImageUtils.loadIcon(iconDir + "Logo.png", 32, 32);
        
        logoIconPanel = new JLabel(logoImage);
        logoIconPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoIconPanel.setVisible(false);

        JPanel logoWrapper = new JPanel();
        logoWrapper.setLayout(new BoxLayout(logoWrapper, BoxLayout.X_AXIS));
        logoWrapper.setBackground(Theme.CARD);
        logoWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoWrapper.add(Box.createRigidArea(new Dimension(10, 0)));
        logoWrapper.add(logoIconPanel);
        logoWrapper.add(logoLabel);
        menuPanel.add(logoWrapper);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        ImageIcon homeIcon = utils.ImageUtils.loadIcon(iconDir + "beranda.png", 22, 22);
        ImageIcon uploadIcon = utils.ImageUtils.loadIcon(iconDir + "upload.png", 22, 22);
        ImageIcon profileIcon = utils.ImageUtils.loadIcon(iconDir + "Profile.png", 22, 22);
        ImageIcon likeIcon = utils.ImageUtils.loadIcon(iconDir + "LikeIconBefore.png", 22, 22);
        ImageIcon favIcon = utils.ImageUtils.loadIcon(iconDir + "PavoriteIconBefore.png", 22, 22);

        Object[][] menus = {
            {homeIcon, "Beranda", "selected"}, 
            {uploadIcon, "Posting", ""},
            {profileIcon, "Profil", ""},
            {likeIcon, "Disukai", ""},
            {favIcon, "Tersimpan", ""}
        };

        for (Object[] menu : menus) {
            menuPanel.add(createMenuItem(menu[0], (String) menu[1], (String) menu[2]));
            menuPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        add(menuPanel, BorderLayout.NORTH);

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(Theme.CARD);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));
        
        ImageIcon logoutIcon = utils.ImageUtils.loadIcon(iconDir + "Logout.png", 22, 22);
        JPanel logoutMenu = createMenuItem(logoutIcon, "Logout", "bottom");
        logoutMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onMenuClick != null) onMenuClick.accept("Logout");
            }
        });
        bottomPanel.add(logoutMenu);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel perluasMenu = createMenuItem("\u203A", "Perluas", "bottom");
        perluasMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSidebar();
            }
        });
        bottomPanel.add(perluasMenu);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void toggleSidebar() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            setPreferredSize(new Dimension(250, 0));
            menuPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));
            logoLabel.setVisible(true);
            logoIconPanel.setVisible(false);
            for (JLabel label : textLabels) {
                label.setVisible(true);
            }
            for (MenuItemPanel c : menuItems) {
                c.setMaximumSize(new Dimension(210, 48));
                c.setPreferredSize(new Dimension(210, 48));
            }
        } else {
            setPreferredSize(new Dimension(80, 0));
            menuPanel.setBorder(BorderFactory.createEmptyBorder(25, 10, 20, 10));
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
            logoLabel.setVisible(false);
            logoIconPanel.setVisible(true);
            for (JLabel label : textLabels) {
                label.setVisible(false);
            }
            for (MenuItemPanel c : menuItems) {
                c.setMaximumSize(new Dimension(60, 48));
                c.setPreferredSize(new Dimension(60, 48));
            }
        }
        revalidate();
        repaint();
    }

    public void setActiveMenu(String menuText) {
        for (MenuItemPanel item : menuItems) {
            if ("Perluas".equals(item.getText()) || "Logout".equals(item.getText())) continue;
            item.setSelected(item.getText().equals(menuText));
        }
    }

    private class MenuItemPanel extends JPanel {
        private boolean isSelected;
        private JLabel iconLabel;
        private JLabel textLabel;
        private String text;

        public MenuItemPanel(Object iconObj, String text, String type) {
            this.text = text;
            this.isSelected = "selected".equals(type);
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setOpaque(false);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
            
            if (iconObj instanceof String) {
                iconLabel = new JLabel((String) iconObj, javax.swing.SwingConstants.CENTER);
                iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            } else if (iconObj instanceof ImageIcon) {
                iconLabel = new JLabel((ImageIcon) iconObj, javax.swing.SwingConstants.CENTER);
            }
            iconLabel.setPreferredSize(new Dimension(24, 24));
            iconLabel.setForeground(isSelected ? Theme.PRIMARY : Theme.TEXT);
            
            textLabel = new JLabel(text);
            textLabels.add(textLabel);
            
            add(iconLabel);
            add(Box.createRigidArea(new Dimension(15, 0)));
            add(textLabel);
            
            updateState();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isSelected) setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!"Perluas".equals(text) && !"Logout".equals(text)) {
                        setActiveMenu(text);
                        if (onMenuClick != null) onMenuClick.accept(text);
                    }
                }
            });
        }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
            updateState();
        }

        private void updateState() {
            iconLabel.setForeground(isSelected ? Theme.PRIMARY : Theme.TEXT);
            textLabel.setFont(new Font("Segoe UI", isSelected ? Font.BOLD : Font.PLAIN, 14));
            textLabel.setForeground(isSelected ? Theme.PRIMARY : Theme.TEXT);
            repaint();
        }

        public String getText() { return text; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isSelected) {
                g2.setColor(Theme.PRIMARY_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            } else {
                g2.setColor(Theme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(240, 240, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
            g2.dispose();
        }
    }

    private MenuItemPanel createMenuItem(Object icon, String text, String type) {
        MenuItemPanel panel = new MenuItemPanel(icon, text, type);
        panel.setMaximumSize(new Dimension(isExpanded ? 210 : 60, 48));
        menuItems.add(panel);
        return panel;
    }
}
