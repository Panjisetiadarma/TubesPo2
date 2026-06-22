package utils;

public class Session {
    private static String currentUser = "admin"; // Default for testing if bypassed
    
    public static void setCurrentUser(String username) {
        currentUser = username;
    }
    
    public static String getCurrentUser() {
        return currentUser;
    }
}
