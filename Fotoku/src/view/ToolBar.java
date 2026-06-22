package view;

import utils.ProfileTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class ToolBar extends JPanel {
    private final JLabel countLabel;
    private final IconButton btnGrid3;
    private final IconButton btnGrid2;
    private final IconButton btnList;
    private final JButton addBtn;

    public interface ViewModeListener { void onViewChanged(int cols); }

    public ToolBar(int photoCount) {
        setBackground(ProfileTheme.BG_DARK);
        setPreferredSize(new Dimension(0, 48));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 12, 8, 12));

        // Left: count
        countLabel = new JLabel(photoCount + " foto");
        countLabel.setFont(ProfileTheme.FONT_COUNT);
        countLabel.setForeground(ProfileTheme.TEXT_SECONDARY);

        // Right
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);

        // + Tambah Foto button
        addBtn = new JButton("+ Tambah Foto") {
            private boolean h = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { h = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { h = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(h ? ProfileTheme.BTN_ADD_HOVER : ProfileTheme.BTN_ADD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(Color.WHITE);
                g2.setFont(ProfileTheme.FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        addBtn.setOpaque(false);
        addBtn.setContentAreaFilled(false);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(132, 30));
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // View mode buttons
        btnGrid3 = new IconButton("⊞");
        btnGrid2 = new IconButton("‖");
        btnList  = new IconButton("☰");
        btnGrid3.setSelected(true);

        right.add(addBtn);
        right.add(btnGrid3);
        right.add(btnGrid2);
        right.add(btnList);

        add(countLabel, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    public void setAddPhotoCallback(Runnable callback) {
        // Remove previous listeners to avoid double-firing
        for (ActionListener al : addBtn.getActionListeners()) addBtn.removeActionListener(al);
        addBtn.addActionListener(e -> callback.run());
    }

    public void setViewModeListener(ViewModeListener l) {
        btnGrid3.addActionListener(e -> { selectView(3); l.onViewChanged(3); });
        btnGrid2.addActionListener(e -> { selectView(2); l.onViewChanged(2); });
        btnList.addActionListener(e  -> { selectView(1); l.onViewChanged(1); });
    }

    private void selectView(int cols) {
        btnGrid3.setSelected(cols == 3);
        btnGrid2.setSelected(cols == 2);
        btnList.setSelected(cols == 1);
    }

    public void setCount(int n) {
        countLabel.setText(n + " foto");
    }
}
