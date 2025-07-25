package com.movieapp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML private AnchorPane contentArea;

    public void initialize() {
        loadScreen("/com/example/movieticket/welcome.fxml");
    }

    public void loadScreen(String fxmlPath) {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(pane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
