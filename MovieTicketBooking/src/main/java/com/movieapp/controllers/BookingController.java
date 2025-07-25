package com.movieapp.controllers;

import com.movieapp.database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class BookingController implements Initializable {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> movieNameBox;
    @FXML private ComboBox<String> seatSectionBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeBox;
    @FXML private CheckBox popcornCheck;
    @FXML private CheckBox chickenCheck;
    @FXML private CheckBox spriteCheck;
    @FXML private Button selectSeatsBtn;
    @FXML private Button deleteTimeBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        movieNameBox.setItems(FXCollections.observableArrayList("Elio", "Utsob", " Wolf Man", "I Know What You Did Last Summer" , "Bhutopurbo"));
        seatSectionBox.setItems(FXCollections.observableArrayList("Front", "Middle", "Rear"));
        timeBox.setItems(FXCollections.observableArrayList("12:00 PM", "3:00 PM", "5:00 PM"));


        selectSeatsBtn.setDisable(true);

        nameField.textProperty().addListener((obs, oldVal, newVal) -> updateSelectSeatsButton());
        movieNameBox.setOnAction(e -> updateSelectSeatsButton());
        seatSectionBox.setOnAction(e -> updateSelectSeatsButton());
        datePicker.setOnAction(e -> updateSelectSeatsButton());
        timeBox.setOnAction(e -> updateSelectSeatsButton());
    }

    private void updateSelectSeatsButton() {
        boolean allFieldsFilled = nameField.getText() != null && !nameField.getText().trim().isEmpty() &&
                movieNameBox.getValue() != null &&
                seatSectionBox.getValue() != null &&
                datePicker.getValue() != null &&
                timeBox.getValue() != null;
        selectSeatsBtn.setDisable(!allFieldsFilled);
    }

    @FXML
    public void handleSelectSeats() {
        try {
            String name = nameField.getText();
            String movie = movieNameBox.getValue();
            String section = seatSectionBox.getValue();
            LocalDate date = datePicker.getValue();
            String time = timeBox.getValue();

            if (name == null || name.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter your name.");
                return;
            }

            if (movie == null) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please select a movie.");
                return;
            }

            if (section == null) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please select a seat section.");
                return;
            }

            if (date == null) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please select a booking date.");
                return;
            }

            if (time == null) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please select a show time.");
                return;
            }

            if (date.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.WARNING, "Invalid Date", "Please select a future date.");
                return;
            }

            // Get snack selections
            boolean popcorn = popcornCheck.isSelected();
            boolean chicken = chickenCheck.isSelected();
            boolean sprite = spriteCheck.isSelected();

            // Open seat selection window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movieticket/seatselection.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Select Your Seat - " + movie);
            stage.setMaximized(true);

            SeatSelectionController seatController = loader.getController();
            seatController.setSeatSelectionData(name, movie, section, date, time, popcorn, chicken, sprite);

            stage.show();

            // Clear the form after opening seat selection
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to open seat selection. Please try again.");
        }
    }

    @FXML
    public void deleteBookingTime() {
        String movie = movieNameBox.getValue();
        LocalDate date = datePicker.getValue();
        String time = timeBox.getValue();

        if (movie == null || date == null || time == null) {
            showAlert(Alert.AlertType.WARNING, "Invalid Selection", "Please select movie, date, and time first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete all bookings for " + movie + " on " + date + " at " + time + "?");
        confirm.setTitle("Confirm Deletion");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM booking_history WHERE movie = ? AND date = ? AND time = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, movie);
                stmt.setDate(2, Date.valueOf(date));
                stmt.setString(3, time);
                int deleted = stmt.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Deletion Complete", deleted + " booking(s) deleted successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete bookings. Please try again.");
            }
        }
    }

    @FXML
    public void handleHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movieticket/bookinghistory.fxml"));
            Scene scene = new Scene(loader.load(), 1600, 800);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Booking History");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();


        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to open booking history. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        nameField.clear();
        movieNameBox.setValue(null);
        seatSectionBox.setValue(null);
        datePicker.setValue(null);
        timeBox.setValue(null);
        popcornCheck.setSelected(false);
        chickenCheck.setSelected(false);
        spriteCheck.setSelected(false);
    }
}