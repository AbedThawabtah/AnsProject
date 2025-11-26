package org.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * About view showing developer information
 */
public class AboutView {
    private VBox root;

    public AboutView() {
        createView();
    }

    private void createView() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("about-view");

        // Title
        Label titleLabel = new Label("Library Management System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.getStyleClass().add("title");

        // Developer Information
        VBox infoBox = new VBox(15);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(20));

        Label developerLabel = new Label("Developed by:");
        developerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // TODO: Replace with your actual information
        Label nameLabel = new Label("Your Name");
        nameLabel.setFont(Font.font("Arial", 14));

        Label studentIdLabel = new Label("Student ID: [Your Student ID]");
        studentIdLabel.setFont(Font.font("Arial", 14));

        Label emailLabel = new Label("Email: [your.email@example.com]");
        emailLabel.setFont(Font.font("Arial", 14));

        Label universityLabel = new Label("Bethlehem University");
        universityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label departmentLabel = new Label("Technology Department");
        departmentLabel.setFont(Font.font("Arial", 14));

        Label courseLabel = new Label("Course: Database Management Systems (SWER351)");
        courseLabel.setFont(Font.font("Arial", 14));

        Label versionLabel = new Label("Version 1.0");
        versionLabel.setFont(Font.font("Arial", 12));
        versionLabel.getStyleClass().add("version");

        Label descriptionLabel = new Label(
            "This application is a comprehensive Library Management System " +
            "designed to manage books, authors, borrowers, loans, and sales. " +
            "It includes user authentication, role-based access control, " +
            "CRUD operations, and comprehensive reporting features."
        );
        descriptionLabel.setWrapText(true);
        descriptionLabel.setTextAlignment(TextAlignment.CENTER);
        descriptionLabel.setMaxWidth(600);
        descriptionLabel.setFont(Font.font("Arial", 12));
        descriptionLabel.getStyleClass().add("description");

        infoBox.getChildren().addAll(
            developerLabel,
            nameLabel,
            studentIdLabel,
            emailLabel,
            new Label(""), // Spacer
            universityLabel,
            departmentLabel,
            courseLabel,
            new Label(""), // Spacer
            descriptionLabel,
            new Label(""), // Spacer
            versionLabel
        );

        root.getChildren().addAll(titleLabel, infoBox);
    }

    public VBox getRoot() {
        return root;
    }
}

