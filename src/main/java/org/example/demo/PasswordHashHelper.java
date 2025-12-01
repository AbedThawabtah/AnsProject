package org.example.demo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHashHelper {

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
     * Main method to generate hashed passwords for seed data
     * Run this to get the hash for any password you want to use in seed data
     */
    public static void main(String[] args) {
        System.out.println("=== Password Hash Generator ===");
        System.out.println("Use these hashed passwords in your seed_users_with_roles.sql file\n");
        
        // Generate hashes for common test passwords
        String[] passwords = {"admin123", "staff123", "student123", "password123"};
        
        for (String password : passwords) {
            String hash = hashPassword(password);
            System.out.println("Password: " + password);
            System.out.println("Hash: " + hash);
            System.out.println("SQL: INSERT INTO users (username, password, email, role) VALUES ('username', '" + hash + "', 'email@example.com', 'role');");
            System.out.println();
        }
        
        // If you want to hash a custom password, uncomment and modify:
        // String customPassword = "your_password_here";
        // System.out.println("Custom password hash: " + hashPassword(customPassword));
    }
}

