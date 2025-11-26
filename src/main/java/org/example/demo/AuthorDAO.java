package org.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;

/**
 * Data Access Object for Author CRUD operations
 */
public class AuthorDAO implements GenericDAO<Author> {

    @Override
    public boolean add(Author author) {
        // Validate NOT NULL fields
        String firstName = author.first_nameProperty().get();
        String lastName = author.last_nameProperty().get();
        
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required and cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required and cannot be empty");
        }
        
        String sql = "INSERT INTO author (first_name, last_name, country, bio) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
           
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, firstName.trim());
                pstmt.setString(2, lastName.trim());
                
                // Handle nullable fields
                String country = author.countryProperty().get();
                if (country == null || country.trim().isEmpty()) {
                    pstmt.setNull(3, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(3, country.trim());
                }
                
                String bio = author.bioProperty().get();
                if (bio == null || bio.trim().isEmpty()) {
                    pstmt.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(4, bio.trim());
                }
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding author: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Author author) {
        // Validate NOT NULL fields
        String firstName = author.first_nameProperty().get();
        String lastName = author.last_nameProperty().get();
        
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required and cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required and cannot be empty");
        }
        
        String sql = "UPDATE author SET first_name = ?, last_name = ?, country = ?, bio = ? WHERE author_id = ?";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, firstName.trim());
                pstmt.setString(2, lastName.trim());
                
                // Handle nullable fields
                String country = author.countryProperty().get();
                if (country == null || country.trim().isEmpty()) {
                    pstmt.setNull(3, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(3, country.trim());
                }
                
                String bio = author.bioProperty().get();
                if (bio == null || bio.trim().isEmpty()) {
                    pstmt.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(4, bio.trim());
                }
                
                pstmt.setInt(5, author.author_idProperty().get());
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating author: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM author WHERE author_id = ?";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting author: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Author getById(int id) {
        String sql = "SELECT * FROM author WHERE author_id = ?";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) return null;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Author(
                            rs.getInt("author_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("country"),
                            rs.getString("bio")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting author by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Author> getAll() {
        // Always return a non-null list
        ObservableList<Author> authors = FXCollections.observableArrayList();
        String query = "SELECT * FROM author";

        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                // Return empty list, never null
                return authors;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    try {
                        Author author = new Author(
                                rs.getInt("author_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("country"),
                                rs.getString("bio"));
                        authors.add(author);
                    } catch (Exception e) {
                        // Skip invalid records, continue with others
                        System.err.println("Error creating author from result set: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all authors: " + e.getMessage());
            e.printStackTrace();
            // Return empty list on error, never null
        } catch (Exception e) {
            System.err.println("Unexpected error getting all authors: " + e.getMessage());
            e.printStackTrace();
            // Return empty list on error, never null
        }
        // Always return the list (never null)
        return authors;
    }
}

