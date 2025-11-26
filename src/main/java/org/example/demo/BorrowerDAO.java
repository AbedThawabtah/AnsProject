package org.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;

/**
 * Data Access Object for Borrower CRUD operations
 */
public class BorrowerDAO implements GenericDAO<Borrower> {

    @Override
    public boolean add(Borrower borrower) {
        // Validate NOT NULL fields
        String firstName = borrower.first_nameProperty().get();
        String lastName = borrower.last_nameProperty().get();
        int typeId = borrower.type_idProperty().get();
        
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required and cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required and cannot be empty");
        }
        if (typeId <= 0) {
            throw new IllegalArgumentException("Type ID is required and must be greater than 0");
        }
        
        String sql = "INSERT INTO borrower (first_name, last_name, type_id, contact_info) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, firstName.trim());
                pstmt.setString(2, lastName.trim());
                pstmt.setInt(3, typeId);
                
                // Handle nullable contact_info
                String contactInfo = borrower.contact_infoProperty().get();
                if (contactInfo == null || contactInfo.trim().isEmpty()) {
                    pstmt.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(4, contactInfo.trim());
                }
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding borrower: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Borrower borrower) {
        // Validate NOT NULL fields
        String firstName = borrower.first_nameProperty().get();
        String lastName = borrower.last_nameProperty().get();
        int typeId = borrower.type_idProperty().get();
        
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required and cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required and cannot be empty");
        }
        if (typeId <= 0) {
            throw new IllegalArgumentException("Type ID is required and must be greater than 0");
        }
        
        String sql = "UPDATE borrower SET first_name = ?, last_name = ?, type_id = ?, contact_info = ? WHERE borrower_id = ?";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, firstName.trim());
                pstmt.setString(2, lastName.trim());
                pstmt.setInt(3, typeId);
                
                // Handle nullable contact_info
                String contactInfo = borrower.contact_infoProperty().get();
                if (contactInfo == null || contactInfo.trim().isEmpty()) {
                    pstmt.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(4, contactInfo.trim());
                }
                
                pstmt.setInt(5, borrower.borrower_idProperty().get());
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating borrower: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM borrower WHERE borrower_id = ?";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting borrower: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Borrower getById(int id) {
        String sql = "SELECT * FROM borrower WHERE borrower_id = ?";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) return null;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Borrower(
                            rs.getInt("borrower_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getInt("type_id"),
                            rs.getString("contact_info")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting borrower by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Borrower> getAll() {
        // Always return a non-null list
        ObservableList<Borrower> borrowers = FXCollections.observableArrayList();
        String query = "SELECT * FROM borrower";

        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                // Return empty list, never null
                return borrowers;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    try {
                        Borrower borrower = new Borrower(
                                rs.getInt("borrower_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getInt("type_id"),
                                rs.getString("contact_info"));
                        borrowers.add(borrower);
                    } catch (Exception e) {
                        // Skip invalid records, continue with others
                        System.err.println("Error creating borrower from result set: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all borrowers: " + e.getMessage());
            e.printStackTrace();
            // Return empty list on error, never null
        } catch (Exception e) {
            System.err.println("Unexpected error getting all borrowers: " + e.getMessage());
            e.printStackTrace();
            // Return empty list on error, never null
        }
        // Always return the list (never null)
        return borrowers;
    }
}

