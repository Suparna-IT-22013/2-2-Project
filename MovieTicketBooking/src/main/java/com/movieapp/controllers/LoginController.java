package com.movieapp.controllers;

import com.movieapp.database.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter username").showAndWait();
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter password").showAndWait();
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM admins WHERE username = ? AND password = ?\n";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Use the same path pattern as other controllers
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movieticket/booking.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Book Your Movie");
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid username or password").showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Login failed. Please try again.").showAndWait();
        }
    }
}