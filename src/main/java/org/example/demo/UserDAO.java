package org.example.demo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations including authentication and user management
 */
public class UserDAO {
    
    /**
     * Hash password using SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Create Users table if it doesn't exist
     */
    public static void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "username VARCHAR(50) PRIMARY KEY, " +
                "password VARCHAR(64) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "role ENUM('admin', 'staff', 'student') NOT NULL DEFAULT 'student'" +
                ")";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Failed to establish database connection");
                return;
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Users table created or already exists");
            }
        } catch (SQLException e) {
            System.err.println("Error creating Users table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Register a new user
     */
    public static boolean registerUser(String username, String password, String email, String role) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            return false;
        }

        // Validate role
        if (!role.equals("admin") && !role.equals("staff") && !role.equals("student")) {
            role = "student"; // default
        }

        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username.trim());
                pstmt.setString(2, hashedPassword);
                pstmt.setString(3, email.trim());
                pstmt.setString(4, role);
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticate user login
     */
    public static User authenticateUser(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                return null;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username.trim());
                pstmt.setString(2, hashedPassword);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("role")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Check if username already exists
     */
    public static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username.trim());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get all users (for admin)
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                return users;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    users.add(new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting users: " + e.getMessage());
        }
        return users;
    }
}

