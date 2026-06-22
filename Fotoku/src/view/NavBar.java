package view;

import utils.ProfileTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class NavBar extends JPanel {
    private final JTextField searchField;
    private SearchListener searchListener;

    public interface SearchListener {
        void onSearch(String query);
    }

    public NavBar() {
        setBackground(ProfileTheme.BG_NAVBAR);
        setPreferredSize(new Dimension(0, 52));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ProfileTheme.BORDER));

        // LEFT: Brand
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(0, 4, 0, 0));
        JLabel camIcon = new JLabel("\uD83D\uDCF7");
        camIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        camIcon.setForeground(ProfileTheme.TEXT_PRIMARY);
        JLabel brand = new JLabel("Profil");
        brand.setFont(ProfileTheme.FONT_BRAND);
        brand.setForeground(ProfileTheme.TEXT_PRIMARY);
        left.add(camIcon);
        left.add(brand);

        // CENTER: Search
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        searchField = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ProfileTheme.SEARCH_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(ProfileTheme.SEARCH_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        searchField.setOpaque(false);
        searchField.setBorder(BorderFactory.createEmptyBorder(2, 30, 2, 12));
        searchField.setForeground(ProfileTheme.TEXT_SECONDARY);
        searchField.setCaretColor(ProfileTheme.TEXT_PRIMARY);
        searchField.setFont(ProfileTheme.FONT_NAV);
        searchField.setPreferredSize(new Dimension(220, 32));
        searchField.setText("Cari foto...");

        searchField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Cari foto...")) {
                    searchField.setText("");
                    searchField.setForeground(ProfileTheme.TEXT_PRIMARY);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(ProfileTheme.TEXT_SECONDARY);
                    searchField.setText("Cari foto...");
                }
            }
        });

        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setOpaque(false);
        searchWrapper.setPreferredSize(new Dimension(220, 32));
        searchWrapper.add(searchField, BorderLayout.CENTER);

        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String txt = searchField.getText();
                if (!txt.equals("Cari foto...") && searchListener != null) {
                    searchListener.onSearch(txt);
                }
            }
        });
        center.add(searchWrapper);

        // RIGHT: icons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(0, 0, 0, 10));
        right.add(makeNavIcon("\uD83C\uDF10"));
        right.add(makeNavIcon("\uD83D\uDD14"));
        right.add(makeNavIcon("\uD83D\uDC64"));
        right.add(makeNavIcon("\u22EF"));

        add(left, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
    }

    private JLabel makeNavIcon(String emoji) {
        JLabel lbl = new JLabel(emoji);
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lbl.setForeground(ProfileTheme.TEXT_SECONDARY);
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.setBorder(new EmptyBorder(2, 4, 2, 4));
        lbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { lbl.setForeground(ProfileTheme.TEXT_PRIMARY); }
            @Override public void mouseExited(MouseEvent e)  { lbl.setForeground(ProfileTheme.TEXT_SECONDARY); }
        });
        return lbl;
    }

    public void setSearchListener(SearchListener l) { this.searchListener = l; }
}
