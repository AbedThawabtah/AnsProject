package org.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Signup view for new user registration
 */
public class SignupView {
    private VBox root;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField emailField;
    private ComboBox<String> roleComboBox;
    private Button signupButton;
    private Button backButton;
    private Runnable onSignupSuccess;
    private Runnable onBackClick;

    public SignupView() {
        createView();
    }

    private void createView() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("signup-view");

        // Title
        Label titleLabel = new Label("Create New Account");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.getStyleClass().add("title");

        // Form
        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(10);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        // Username
        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(250);
        form.add(usernameLabel, 0, 0);
        form.add(usernameField, 1, 0);

        // Email
        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        emailField.setPromptText("Enter email address");
        emailField.setPrefWidth(250);
        form.add(emailLabel, 0, 1);
        form.add(emailField, 1, 1);

        // Password
        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password (min 6 chars, letters & digits)");
        passwordField.setPrefWidth(250);
        form.add(passwordLabel, 0, 2);
        form.add(passwordField, 1, 2);

        // Confirm Password
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter password");
        confirmPasswordField.setPrefWidth(250);
        form.add(confirmPasswordLabel, 0, 3);
        form.add(confirmPasswordField, 1, 3);

        // Role
        Label roleLabel = new Label("Role:");
        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("student", "staff", "admin");
        roleComboBox.setValue("student");
        roleComboBox.setPrefWidth(250);
        form.add(roleLabel, 0, 4);
        form.add(roleComboBox, 1, 4);

        // Buttons
        signupButton = new Button("Sign Up");
        signupButton.setPrefWidth(120);
        signupButton.setDefaultButton(true);
        signupButton.setOnAction(e -> handleSignup());

        backButton = new Button("Back to Login");
        backButton.setPrefWidth(120);
        backButton.setOnAction(e -> {
            if (onBackClick != null) onBackClick.run();
        });

        HBox buttonBox = new HBox(10, signupButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        form.add(buttonBox, 0, 5, 2, 1);

        root.getChildren().addAll(titleLabel, form);
    }

    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        if (username.length() < 3) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Username must be at least 3 characters long.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            return;
        }

        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                "Password must be at least 6 characters long and contain both letters and digits.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match.");
            return;
        }

        if (UserDAO.usernameExists(username)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Username already exists. Please choose another.");
            return;
        }

        // Register user
        if (UserDAO.registerUser(username, password, email, role)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully! You can now login.");
            if (onSignupSuccess != null) {
                onSignupSuccess.run();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create account. Please try again.");
        }
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6) return false;
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnSignupSuccess(Runnable callback) {
        this.onSignupSuccess = callback;
    }

    public void setOnBackClick(Runnable callback) {
        this.onBackClick = callback;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

