package com.movieapp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class WelcomeController {

    @FXML
    private ImageView logoImage;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/com/example/movieticket/images/logo.jpg"));
            logoImage.setImage(image);
        } catch (Exception e) {
            System.err.println("❌ Image not found at path: /com/example/movieticket/images/logo.jpg");
            e.printStackTrace();
        }

        loginButton.setOnAction(event -> {
            try {
                AnchorPane loginPane = FXMLLoader.load(getClass().getResource("/com/example/movieticket/login.fxml"));
                AnchorPane contentArea = (AnchorPane) logoImage.getScene().lookup("#contentArea");
                contentArea.getChildren().setAll(loginPane);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
