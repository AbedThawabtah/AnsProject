package org.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public static class DatabaseConnection {
        private static final String URL = "jdbc:mysql://localhost:3306/libr";
        private static final String USER = "root"; // change if needed
        private static final String PASSWORD = ""; // change if needed

        static {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found!");
                e.printStackTrace();
            }
        }

        public static Connection getConnection() {
            try {
                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.err.println("Failed to connect to database: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }
}