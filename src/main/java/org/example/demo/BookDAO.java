package org.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;

/**
 * Data Access Object for Book CRUD operations
 */
public class BookDAO implements GenericDAO<Book> {

    @Override
    public boolean add(Book book) {
        // Validate NOT NULL fields
        String title = book.titleProperty().get();
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required and cannot be empty");
        }
        
        String sql = "INSERT INTO book (title, publisher_id, category, book_type, original_price, available) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, title.trim());
                
                // Handle nullable publisher_id
                int publisherId = book.publisher_idProperty().get();
                System.out.println("Adding book with publisher ID: " + publisherId);
                if (publisherId <= 0 ) {
                    System.out.println("Publisher ID is null or invalid, setting to NULL in database.");
                    pstmt.setNull(2, java.sql.Types.INTEGER);
                }
                else {
                    pstmt.setInt(2, publisherId);
                }
                
                // Handle nullable category
                String category = book.categoryProperty().get();
                if (category == null || category.trim().isEmpty()) {
                    pstmt.setNull(3, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(3, category.trim());
                }
                
                // Handle nullable book_type
                String bookType = book.book_typeProperty().get();
                if (bookType == null || bookType.trim().isEmpty()) {
                    pstmt.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(4, bookType.trim());
                }
                
                // Handle nullable original_price
                double price = book.original_priceProperty().get();
                if (price <= 0) {
                    pstmt.setNull(5, java.sql.Types.DECIMAL);
                } else {
                    pstmt.setDouble(5, price);
                }
                
                // available defaults to 1 if not set
                int available = book.availableProperty().get();
                pstmt.setInt(6, available > 0 ? 1 : 0);
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Book book) {
        // Validate NOT NULL fields
        String title = book.titleProperty().get();
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required and cannot be empty");
        }
        
        String sql = "UPDATE book SET title = ?, publisher_id = ?, category = ?, book_type = ?, original_price = ?, available = ? WHERE book_id = ?";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, title.trim());
                
                // Handle nullable publisher_id
                int publisherId = book.publisher_idProperty().get();
                if (publisherId <= 0) {
                    pstmt.setNull(2, java.sql.Types.INTEGER);
                } else {
                    pstmt.setInt(2, publisherId);
                }
                
                // Handle nullable category
                String category = book.categoryProperty().get();
                if (category == null || category.trim().isEmpty()) {
                    pstmt.setNull(3, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(3, category.trim());
                }
                
                // Handle nullable book_type
                String bookType = book.book_typeProperty().get();
                if (bookType == null || bookType.trim().isEmpty()) {
                    pstmt.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(4, bookType.trim());
                }
                
                // Handle nullable original_price
                double price = book.original_priceProperty().get();
                if (price <= 0) {
                    pstmt.setNull(5, java.sql.Types.DECIMAL);
                } else {
                    pstmt.setDouble(5, price);
                }
                
                pstmt.setInt(6, book.availableProperty().get() > 0 ? 1 : 0);
                pstmt.setInt(7, book.book_idProperty().get());
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM book WHERE book_id = ?";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
public  int getIDbyString(String name){
        String sql = "SELECT name FROM publisher WHERE name = ?";
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) return -1;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("publisher_id");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting publisher ID by name: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Return -1 if not found or error occurs

}
    @Override
    public Book getById(int id) {
        String sql = "SELECT * FROM book WHERE book_id = ?";
        
        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) return null;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Book(
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getInt("publisher_id"),
                            rs.getString("category"),
                            rs.getString("book_type"),
                            rs.getDouble("original_price"),
                            rs.getInt("available")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Book> getAll() {
        // Always return a non-null list
        ObservableList<Book> books = FXCollections.observableArrayList();
        String query = "SELECT book_id, title, name, category, book_type, original_price, available FROM book left JOIN publisher ON book.publisher_id = publisher.publisher_id";

        try (Connection conn = HelloController.DatabaseConnection.getConnection()) {
            if (conn == null) {
                // Return empty list, never null
                return books;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    try {
                        Book book = new Book(
                                rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("name"),
                                rs.getString("category"),
                                rs.getString("book_type"),
                                rs.getDouble("original_price"),
                                rs.getInt("available"));
                        books.add(book);
                    } catch (Exception e) {
                        // Skip invalid records, continue with others
                        System.err.println("Error creating book from result set: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all books: " + e.getMessage());
            e.printStackTrace();
            // Return empty list on error, never null
        } catch (Exception e) {
            System.err.println("Unexpected error getting all books: " + e.getMessage());
            e.printStackTrace();
            // Return empty list on error, never null
        }
        // Always return the list (never null)
        return books;
    }
}

