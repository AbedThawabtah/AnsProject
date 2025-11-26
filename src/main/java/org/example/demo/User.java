package org.example.demo;

import javafx.beans.property.*;

public class User {
    private StringProperty username;
    private StringProperty password; // hashed
    private StringProperty email;
    private StringProperty role; // 'admin', 'staff', 'student'

    public User(String username, String password, String email, String role) {
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.email = new SimpleStringProperty(email);
        this.role = new SimpleStringProperty(role);
    }

    // Getters
    public String getUsername() { return username.get(); }
    public String getPassword() { return password.get(); }
    public String getEmail() { return email.get(); }
    public String getRole() { return role.get(); }

    // Property getters for binding
    public StringProperty usernameProperty() { return username; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty emailProperty() { return email; }
    public StringProperty roleProperty() { return role; }

    // Setters
    public void setUsername(String username) { this.username.set(username); }
    public void setPassword(String password) { this.password.set(password); }
    public void setEmail(String email) { this.email.set(email); }
    public void setRole(String role) { this.role.set(role); }
}

