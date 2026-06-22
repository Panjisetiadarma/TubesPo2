package view;

import utils.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class TabBar extends JPanel {
    public enum Tab { SEMUA, DISUKAI, FAVORIT }
    public interface TabListener { void onTabSelected(Tab tab); }

    private Tab activeTab = Tab.SEMUA;
    private TabListener listener;

    private final TabButton btnSemua;
    private final TabButton btnDisukai;
    private final TabButton btnFavorit;

    public TabBar() {
        setBackground(Theme.CARD);
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        setPreferredSize(new Dimension(0, 46));
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        String iconDir = new java.io.File("Icon").exists() ? "Icon/" : "../Icon/";
        ImageIcon likeIcon = utils.ImageUtils.loadIcon(iconDir + "LikeIconBefore.png", 16, 16);
        ImageIcon favIcon = utils.ImageUtils.loadIcon(iconDir + "PavoriteIconBefore.png", 16, 16);

        btnSemua     = new TabButton("\u229E  Semua Foto", Tab.SEMUA, null);
        btnDisukai   = new TabButton("  Disukai", Tab.DISUKAI, likeIcon);
        btnFavorit   = new TabButton("  Disimpan", Tab.FAVORIT, favIcon);
        btnSemua.setActive(true);

        add(btnSemua);
        add(btnDisukai);
        add(btnFavorit);
    }

    public void setTabListener(TabListener l) { this.listener = l; }

    private class TabButton extends JButton {
        private final Tab tab;
        private boolean active = false;
        private boolean hovered = false;
        private final ImageIcon icon;

        TabButton(String text, Tab tab, ImageIcon icon) {
            super(text);
            this.tab = tab;
            this.icon = icon;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            int iconW = icon != null ? icon.getIconWidth() + 5 : 0;
            setPreferredSize(new Dimension(getFontMetrics(getFont()).stringWidth(text) + iconW + 20, 46));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    setActiveTab(tab);
                    if (listener != null) listener.onTabSelected(tab);
                }
            });
        }

        void setActive(boolean a) { this.active = a; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int iconW = icon != null ? icon.getIconWidth() + 5 : 0;
            int totalW = fm.stringWidth(getText()) + iconW;
            int tx = (w - totalW) / 2;
            int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
            
            if (active) {
                g2.setColor(Theme.PRIMARY);
                if (icon != null) {
                    icon.paintIcon(this, g2, tx, (h - icon.getIconHeight())/2);
                    tx += iconW;
                }
                g2.drawString(getText(), tx, ty);
                // Draw underline
                g2.fillRect(0, h - 3, w, 3);
            } else {
                g2.setColor(hovered ? Theme.TEXT : Theme.TEXT_MUTED);
                if (icon != null) {
                    icon.paintIcon(this, g2, tx, (h - icon.getIconHeight())/2);
                    tx += iconW;
                }
                g2.drawString(getText(), tx, ty);
            }
            
            g2.dispose();
        }
    }

    public void setActiveTab(Tab tab) {
        activeTab = tab;
        btnSemua.setActive(tab == Tab.SEMUA);
        btnDisukai.setActive(tab == Tab.DISUKAI);
        btnFavorit.setActive(tab == Tab.FAVORIT);
        repaint();
        if (listener != null) listener.onTabSelected(tab);
    }

    public Tab getActiveTab() { return activeTab; }
}
