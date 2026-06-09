package com.visualgallery.utils;

import com.visualgallery.utils.ThemeManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * UIUtils - Utility class for UI component creation.
 *
 * Provides factory methods to create consistent, styled UI components
 * following the Visual Gallery design system.
 *
 * OOP: Utility Class (static factory methods)
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public final class UIUtils {

    private UIUtils() { }

    // ==================== FONTS ====================

    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 12);

    // ==================== BUTTONS ====================

    /**
     * Creates a styled primary (accent gold) button.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
            "arc: 20;" +
            "margin: 6,18,6,18;" +
            "background: #D4AF37;" +
            "hoverBackground: lighten(#D4AF37, 10%);" +
            "pressedBackground: darken(#D4AF37, 10%);" +
            "borderWidth: 0;" +
            "focusWidth: 0"
        );
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 40));
        return btn;
    }

    /**
     * Creates a styled secondary (outline) button.
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setForeground(ThemeManager.ACCENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
            "arc: 20;" +
            "margin: 6,18,6,18;" +
            "background: null;" +
            "borderColor: #D4AF37;" +
            "borderWidth: 1;" +
            "hoverBackground: fade(#D4AF37, 15%);" +
            "focusWidth: 0"
        );
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 38));
        return btn;
    }

    /**
     * Creates a danger (red) button.
     */
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
            "arc: 20;" +
            "margin: 6,18,6,18;" +
            "background: #E53935;" +
            "hoverBackground: lighten(#E53935, 10%);" +
            "pressedBackground: darken(#E53935, 10%);" +
            "borderWidth: 0;" +
            "focusWidth: 0"
        );
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 40));
        return btn;
    }

    /**
     * Creates a ghost/text-only button.
     */
    public static JButton createGhostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ==================== LABELS ====================

    public static JLabel createTitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        return lbl;
    }

    public static JLabel createHeadingLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        return lbl;
    }

    public static JLabel createBodyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        return lbl;
    }

    public static JLabel createMutedLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(ThemeManager.getInstance().getTextMuted());
        return lbl;
    }

    public static JLabel createAccentLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(ThemeManager.ACCENT);
        return lbl;
    }

    // ==================== TEXT FIELDS ====================

    public static JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.putClientProperty(com.formdev.flatlaf.FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
            "arc: 16;" +
            "margin: 6,12,6,12;" +
            "focusWidth: 2;" +
            "borderWidth: 1;"
        );
        return tf;
    }

    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.putClientProperty(com.formdev.flatlaf.FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        pf.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
            "arc: 16;" +
            "margin: 6,12,6,12;" +
            "focusWidth: 2;" +
            "borderWidth: 1;"
        );
        return pf;
    }

    public static JTextArea createTextArea(String placeholder, int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(FONT_BODY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
        return ta;
    }

    // ==================== PANELS ====================

    /**
     * Creates a card panel with rounded corners and shadow effect.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ThemeManager.getInstance().getSurface());
        panel.setBorder(BorderFactory.createCompoundBorder(
            new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(1,1,1,1), ThemeManager.getInstance().getBorder(), 1, 24),
            new EmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    // ==================== TABLES ====================

    /**
     * Applies modern FlatLaf styling to a JTable.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(44); // Taller rows for modern look
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(ThemeManager.getInstance().getBorder());
        table.setSelectionBackground(ThemeManager.getInstance().getBackgroundSecondary());
        table.setSelectionForeground(ThemeManager.getInstance().getTextPrimary());
        
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(ThemeManager.getInstance().getSurface());
        table.getTableHeader().setForeground(ThemeManager.getInstance().getTextMuted());
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getInstance().getBorder()));
        
        // FlatLaf specific property
        table.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
            "rowHeight: 44;" +
            "showHorizontalLines: true;" +
            "showVerticalLines: false;" +
            "intercellSpacing: 0,0;" +
            "selectionBackground: $Table.selectionBackground;" +
            "selectionInactiveBackground: $Table.selectionInactiveBackground"
        );
    }

    /**
     * Creates a styled JScrollPane for tables.
     */
    public static JScrollPane createTableScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new com.formdev.flatlaf.ui.FlatLineBorder(new Insets(1,1,1,1), ThemeManager.getInstance().getBorder(), 1, 16),
            new EmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.setBackground(ThemeManager.getInstance().getSurface());
        scrollPane.getViewport().setBackground(ThemeManager.getInstance().getSurface());
        return scrollPane;
    }

    /**
     * Creates a separator line.
     */
    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.getInstance().getBorder());
        return sep;
    }

    // ==================== IMAGE LOADING ====================

    /**
     * Loads an image from a file path and scales it to the given dimensions.
     * Returns a placeholder icon if loading fails.
     *
     * @param path   file path
     * @param width  target width
     * @param height target height
     * @return ImageIcon
     */
    public static ImageIcon loadScaledImage(String path, int width, int height) {
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    Image img = ImageIO.read(file);
                    if (img != null) {
                        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
                    }
                } catch (Exception e) {
                    // fall through to placeholder
                }
            }
        }
        return createPlaceholderIcon(width, height);
    }

    /**
     * Creates a circular cropped avatar icon from an image path.
     */
    public static ImageIcon createCircularIcon(String imagePath, int size) {
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw circle clip
        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));

        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                try {
                    Image img = ImageIO.read(file);
                    if (img != null) {
                        g2.drawImage(img, 0, 0, size, size, null);
                        g2.dispose();
                        return new ImageIcon(bi);
                    }
                } catch (Exception ignored) { }
            }
        }

        // Default avatar
        g2.setColor(new Color(0xD4AF37));
        g2.fillOval(0, 0, size, size);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("?", (size - fm.stringWidth("?")) / 2,
                       (size - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
        return new ImageIcon(bi);
    }

    /**
     * Creates a placeholder icon (grey box with camera icon text).
     */
    public static ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0x2A2A2A));
        g2.fillRect(0, 0, width, height);
        g2.setColor(new Color(0x555555));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, Math.max(12, Math.min(width, height) / 5)));
        String icon = "📷";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(icon,
            (width  - fm.stringWidth(icon)) / 2,
            (height - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
        return new ImageIcon(bi);
    }

    // ==================== DIALOGS ====================

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Berhasil",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Peringatan",
                JOptionPane.WARNING_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    // ==================== MISC ====================

    /**
     * Loads an icon from classpath resources.
     */
    public static ImageIcon loadIcon(String resourcePath, int size) {
        try {
            URL url = UIUtils.class.getClassLoader().getResource(resourcePath);
            if (url != null) {
                Image img = new ImageIcon(url).getImage();
                return new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            // return null
        }
        return null;
    }

    /**
     * Adds a hover color change to a panel.
     */
    public static void addHoverEffect(JPanel panel, Color normalColor, Color hoverColor) {
        panel.setBackground(normalColor);
        panel.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { panel.setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e)  { panel.setBackground(normalColor); }
        });
    }
}
