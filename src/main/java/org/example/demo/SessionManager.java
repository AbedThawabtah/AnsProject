package org.example.demo;

/**
 * Session manager to store current logged-in user
 */
public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }

    public static boolean canEdit() {
        return isAdmin(); // Only admin can edit
    }

    public static void logout() {
        currentUser = null;
    }
}

