package com.visualgallery.view.notifications;

import com.visualgallery.model.Account;
import com.visualgallery.model.Notification;
import com.visualgallery.service.NotificationService;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * NotificationPanel - Displays user notifications.
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class NotificationPanel extends JPanel {

    private final Account currentUser;
    private final NotificationService notifService;
    private final ThemeManager tm = ThemeManager.getInstance();

    private JPanel listPanel;
    private JLabel unreadLabel;

    public NotificationPanel(Account currentUser) {
        this.currentUser = currentUser;
        this.notifService = new NotificationService();
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(tm.getBackground());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ─── Header ───
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Notifikasi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(tm.getTextPrimary());

        unreadLabel = new JLabel("0 belum dibaca");
        unreadLabel.setFont(UIUtils.FONT_BODY);
        unreadLabel.setForeground(ThemeManager.ACCENT);

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(unreadLabel);

        JButton markAllBtn = UIUtils.createSecondaryButton("Tandai Semua Dibaca");
        markAllBtn.addActionListener(e -> markAllRead());

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(markAllBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ─── List ───
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadNotifications() {
        listPanel.removeAll();
        List<Notification> notifs = notifService.getNotificationsForUser(currentUser.getId());
        
        int unreadCount = 0;
        if (notifs.isEmpty()) {
            JLabel empty = new JLabel("Tidak ada notifikasi.");
            empty.setFont(UIUtils.FONT_BODY);
            empty.setForeground(tm.getTextMuted());
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(empty);
        } else {
            for (Notification n : notifs) {
                if (n.isUnread()) unreadCount++;
                listPanel.add(buildNotificationItem(n));
                listPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        unreadLabel.setText(unreadCount + " belum dibaca");
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel buildNotificationItem(Notification n) {
        JPanel p = UIUtils.createCardPanel();
        p.setLayout(new BorderLayout(15, 0));
        p.setMaximumSize(new Dimension(800, 80));
        if (n.isUnread()) {
            p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.ACCENT, 1),
                new EmptyBorder(10, 15, 10, 15)
            ));
        } else {
            p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(tm.getBorder(), 1),
                new EmptyBorder(10, 15, 10, 15)
            ));
        }

        // Icon
        String iconStr = switch(n.getType()) {
            case LIKE -> "♥";
            case COMMENT -> "💬";
            default -> "🔔";
        };
        JLabel icon = new JLabel(iconStr);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        icon.setForeground(n.getType() == Notification.Type.LIKE ? ThemeManager.ERROR : tm.getTextMuted());
        p.add(icon, BorderLayout.WEST);

        // Text
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        
        JLabel msgLabel = new JLabel("<html>" + n.getMessage() + "</html>");
        msgLabel.setFont(n.isUnread() ? UIUtils.FONT_BOLD : UIUtils.FONT_BODY);
        msgLabel.setForeground(tm.getTextPrimary());
        
        JLabel timeLabel = new JLabel(n.getCreatedAt().toLocalDate().toString() + " " + 
                                      n.getCreatedAt().toLocalTime().withNano(0).toString());
        timeLabel.setFont(UIUtils.FONT_SMALL);
        timeLabel.setForeground(tm.getTextMuted());

        textPanel.add(msgLabel);
        textPanel.add(timeLabel);
        p.add(textPanel, BorderLayout.CENTER);

        // Mark read action
        if (n.isUnread()) {
            p.setCursor(new Cursor(Cursor.HAND_CURSOR));
            p.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (notifService.markAsRead(n.getId())) {
                        loadNotifications(); // reload
                    }
                }
            });
        }

        return p;
    }

    private void markAllRead() {
        if (notifService.markAllAsRead(currentUser.getId())) {
            loadNotifications();
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) loadNotifications();
    }
}
