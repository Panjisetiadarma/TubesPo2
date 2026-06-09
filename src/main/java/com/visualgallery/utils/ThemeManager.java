package com.visualgallery.utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ThemeManager - Manages Light/Dark mode theming using FlatLaf.
 *
 * Singleton providing theme switching and color palette access.
 *
 * OOP: Singleton Pattern, Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class ThemeManager {

    private static final Logger LOGGER = Logger.getLogger(ThemeManager.class.getName());
    private static final String PREF_NODE      = "com/visualgallery";
    private static final String PREF_KEY_THEME = "dark_mode";

    // ==================== SINGLETON ====================
    private static ThemeManager instance;
    private boolean darkMode;

    // ==================== COLOR PALETTE ====================
    // Light Mode
    public static final Color LIGHT_BG        = new Color(0xFFFFFF);
    public static final Color LIGHT_BG_SECONDARY = new Color(0xF5F5F5);
    public static final Color LIGHT_TEXT      = new Color(0x111111);
    public static final Color LIGHT_TEXT_MUTED = new Color(0x777777);
    public static final Color LIGHT_BORDER    = new Color(0xE0E0E0);
    public static final Color LIGHT_CARD      = new Color(0xFFFFFF);

    // Dark Mode
    public static final Color DARK_BG         = new Color(0x121212);
    public static final Color DARK_BG_SECONDARY = new Color(0x1E1E1E);
    public static final Color DARK_SURFACE    = new Color(0x252525);
    public static final Color DARK_TEXT       = new Color(0xFFFFFF);
    public static final Color DARK_TEXT_MUTED = new Color(0xAAAAAA);
    public static final Color DARK_BORDER     = new Color(0x333333);

    // Accent (always same)
    public static final Color ACCENT          = new Color(0xD4AF37);
    public static final Color ACCENT_HOVER    = new Color(0xBFA030);
    public static final Color ACCENT_LIGHT    = new Color(0xF0D060);
    public static final Color ERROR           = new Color(0xE53935);
    public static final Color SUCCESS         = new Color(0x43A047);
    public static final Color WARNING         = new Color(0xFB8C00);

    private ThemeManager() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        this.darkMode = prefs.getBoolean(PREF_KEY_THEME, false);
    }

    public static synchronized ThemeManager getInstance() {
        if (instance == null) instance = new ThemeManager();
        return instance;
    }

    // ==================== THEME SWITCHING ====================

    /**
     * Applies the current theme (light or dark) to the Swing application.
     * Should be called before any Swing components are created.
     */
    public void applyTheme() {
        try {
            if (darkMode) {
                FlatDarkLaf.setup();
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                FlatLightLaf.setup();
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            customizeUIDefaults();
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.log(Level.SEVERE, "Failed to apply FlatLaf theme", e);
        }
    }

    /**
     * Toggles between dark and light mode, then refreshes all windows.
     */
    public void toggleTheme() {
        darkMode = !darkMode;
        savePreference();
        applyTheme();
        refreshAllWindows();
    }

    /**
     * Sets dark mode explicitly.
     *
     * @param dark true for dark mode, false for light
     */
    public void setDarkMode(boolean dark) {
        this.darkMode = dark;
        savePreference();
        applyTheme();
        refreshAllWindows();
    }

    public boolean isDarkMode() { return darkMode; }

    // ==================== COLOR HELPERS ====================

    public Color getBackground()        { return darkMode ? DARK_BG : LIGHT_BG; }
    public Color getBackgroundSecondary(){ return darkMode ? DARK_BG_SECONDARY : LIGHT_BG_SECONDARY; }
    public Color getSurface()           { return darkMode ? DARK_SURFACE : LIGHT_CARD; }
    public Color getTextPrimary()       { return darkMode ? DARK_TEXT : LIGHT_TEXT; }
    public Color getTextMuted()         { return darkMode ? DARK_TEXT_MUTED : LIGHT_TEXT_MUTED; }
    public Color getBorder()            { return darkMode ? DARK_BORDER : LIGHT_BORDER; }
    public Color getAccent()            { return ACCENT; }

    // ==================== PRIVATE HELPERS ====================

    private void customizeUIDefaults() {
        // Global font via UIManager
        Font poppins = loadPoppinsFont(13f);
        if (poppins != null) {
            UIManager.put("defaultFont", poppins);
        }

        // Accent color for buttons/focus
        UIManager.put("Component.focusColor", ACCENT);
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 6);
        UIManager.put("ScrollBar.width", 8);
    }

    private Font loadPoppinsFont(float size) {
        try (var stream = getClass().getClassLoader().getResourceAsStream("fonts/Poppins-Regular.ttf")) {
            if (stream != null) {
                Font base = Font.createFont(Font.TRUETYPE_FONT, stream);
                return base.deriveFont(size);
            }
        } catch (Exception e) {
            LOGGER.warning("Poppins font not found, using system default.");
        }
        return null;
    }

    private void refreshAllWindows() {
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            window.repaint();
        }
    }

    private void savePreference() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        prefs.putBoolean(PREF_KEY_THEME, darkMode);
    }
}
