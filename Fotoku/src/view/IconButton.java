package view;

import utils.ProfileTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class IconButton extends JButton {
    private boolean hovered = false;
    private boolean selected = false;
    private final String symbol;

    public IconButton(String symbol) {
        this.symbol = symbol;
        setPreferredSize(new Dimension(32, 28));
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
        });
    }

    public void setSelected(boolean s) { this.selected = s; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        if (selected) {
            g2.setColor(ProfileTheme.TAB_ACTIVE_BG);
            g2.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 6, 6));
        } else if (hovered) {
            g2.setColor(new Color(60, 60, 75));
            g2.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 6, 6));
        }

        g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(symbol)) / 2;
        int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.setColor(selected ? ProfileTheme.TEXT_PRIMARY : ProfileTheme.TEXT_SECONDARY);
        g2.drawString(symbol, tx, ty);

        g2.dispose();
    }
}
