package org.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
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

        // === Add profile image ===
        ImageView profileImage = new ImageView(
                "file:/C:/Users/Alhaneny/Pictures/375780b7-7959-4ecf-b57a-1c9cbf9438ba.jpeg"
        );
        profileImage.setFitWidth(200);
        profileImage.setPreserveRatio(true);
        profileImage.setSmooth(true);
        profileImage.setCache(true);

        // Developer Information
        VBox infoBox = new VBox(15);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(20));

        Label developerLabel = new Label("Developed by:");
        developerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label nameLabel = new Label("Abed Thawabtah");
        nameLabel.setFont(Font.font("Arial", 14));

        Label studentIdLabel = new Label("Student ID: 202303310");
        studentIdLabel.setFont(Font.font("Arial", 14));

        Label emailLabel = new Label("Email: 202303310@gmail.com");
        emailLabel.setFont(Font.font("Arial", 14));

        Label universityLabel = new Label("Bethlehem University");
        universityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label departmentLabel = new Label("Technology Department");
        departmentLabel.setFont(Font.font("Arial", 14));

        Label courseLabel = new Label("Course: Database Management Systems (SWER351)");
        courseLabel.setFont(Font.font("Arial", 14));

        Label versionLabel = new Label("Version 1.0");
        versionLabel.setFont(Font.font("Arial", 12));

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

        infoBox.getChildren().addAll(
                developerLabel,
                nameLabel,
                studentIdLabel,
                emailLabel,
                new Label(""),
                universityLabel,
                departmentLabel,
                courseLabel,
                new Label(""),
                descriptionLabel,
                new Label(""),
                versionLabel
        );

        // Add EVERYTHING to root
        root.getChildren().addAll(titleLabel, profileImage, infoBox);
    }

    public VBox getRoot() {
        return root;
    }
}
