package com.visualgallery.utils;

import com.visualgallery.model.Account;

import java.util.prefs.Preferences;

/**
 * SessionManager - Singleton for managing current user session.
 *
 * Provides centralized access to the currently logged-in user.
 * Also handles "Remember Me" via Java Preferences API.
 *
 * OOP: Singleton Pattern, Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class SessionManager {

    private static final String PREF_KEY_EMAIL    = "remember_email";
    private static final String PREF_KEY_ENABLED  = "remember_enabled";
    private static final String PREF_NODE         = "com/visualgallery";

    // ==================== SINGLETON ====================
    private static SessionManager instance;
    private Account currentUser;

    private SessionManager() { }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // ==================== SESSION MANAGEMENT ====================

    /**
     * Creates a new session for the given account.
     *
     * @param account the authenticated account
     */
    public void createSession(Account account) {
        this.currentUser = account;
    }

    /**
     * Clears the current session (logout).
     */
    public void clearSession() {
        this.currentUser = null;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return Account or null if not logged in
     */
    public Account getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks whether a user is currently logged in.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if the current user is an Admin.
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Gets the current user's ID.
     * Returns -1 if not logged in.
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    // ==================== REMEMBER ME ====================

    /**
     * Saves "Remember Me" email to system preferences.
     *
     * @param email the email to persist
     */
    public void saveRememberMe(String email) {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        prefs.put(PREF_KEY_EMAIL, email);
        prefs.putBoolean(PREF_KEY_ENABLED, true);
    }

    /**
     * Clears the "Remember Me" preference.
     */
    public void clearRememberMe() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        prefs.remove(PREF_KEY_EMAIL);
        prefs.putBoolean(PREF_KEY_ENABLED, false);
    }

    /**
     * Returns the saved "Remember Me" email, or null if not set.
     */
    public String getRememberedEmail() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        boolean enabled = prefs.getBoolean(PREF_KEY_ENABLED, false);
        return enabled ? prefs.get(PREF_KEY_EMAIL, null) : null;
    }

    /**
     * Checks if "Remember Me" is currently enabled.
     */
    public boolean isRememberMeEnabled() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        return prefs.getBoolean(PREF_KEY_ENABLED, false);
    }
}
