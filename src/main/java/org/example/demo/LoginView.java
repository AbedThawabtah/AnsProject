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
 * Login view for user authentication
 */
public class LoginView {
    private VBox root;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button signupButton;
    private Runnable onLoginSuccess;
    private Runnable onSignupClick;

    public LoginView() {
        createView();
    }

    private void createView() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("login-view");

        // Title
        Label titleLabel = new Label("Library Management System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.getStyleClass().add("title");

        Label subtitleLabel = new Label("Please login to continue");
        subtitleLabel.setFont(Font.font("Arial", 14));

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

        // Password
        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(250);
        form.add(passwordLabel, 0, 1);
        form.add(passwordField, 1, 1);

        // Buttons
        loginButton = new Button("Login");
        loginButton.setPrefWidth(120);
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> handleLogin());

        signupButton = new Button("Sign Up");
        signupButton.setPrefWidth(120);
        signupButton.setOnAction(e -> {
            if (onSignupClick != null) onSignupClick.run();
        });

        HBox buttonBox = new HBox(10, loginButton, signupButton);
        buttonBox.setAlignment(Pos.CENTER);
        form.add(buttonBox, 0, 2, 2, 1);

        // Add enter key support
        passwordField.setOnAction(e -> handleLogin());

        root.getChildren().addAll(titleLabel, subtitleLabel, form);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter both username and password.");
            return;
        }

        User user = UserDAO.authenticateUser(username, password);
        if (user != null) {
            // Store current user
            SessionManager.setCurrentUser(user);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful! Welcome, " + user.getUsername() + ".");
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password. Please try again.");
            passwordField.clear();
        }
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }

    public void setOnSignupClick(Runnable callback) {
        this.onSignupClick = callback;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

