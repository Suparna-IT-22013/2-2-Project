package com.movieapp.controllers;

import com.movieapp.database.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class SeatSelectionController implements Initializable {

    @FXML private Label movieLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label sectionLabel;
    @FXML private Label selectedSeatLabel;
    @FXML private GridPane seatGrid;
    @FXML private Button confirmButton;
    @FXML private Button backButton;

    private String movieName;
    private String section;
    private LocalDate date;
    private String time;
    private String customerName;
    private boolean popcorn, chicken, sprite;
    private Button selectedSeatButton;
    private String selectedSeat;
    private Set<String> bookedSeats;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookedSeats = new HashSet<>();
        confirmButton.setDisable(true);
        selectedSeatLabel.setText("No seat selected");
    }

    public void setSeatSelectionData(String customerName, String movieName, String section,
                                     LocalDate date, String time, boolean popcorn, boolean chicken, boolean sprite) {
        this.customerName = customerName;
        this.movieName = movieName;
        this.section = section;
        this.date = date;
        this.time = time;
        this.popcorn = popcorn;
        this.chicken = chicken;
        this.sprite = sprite;

        movieLabel.setText("Movie: " + movieName);
        dateLabel.setText("Date: " + date.toString());
        timeLabel.setText("Time: " + time);
        sectionLabel.setText("Section: " + section);

        loadBookedSeats();
        createSeatLayout();
    }

    private void loadBookedSeats() {
        bookedSeats.clear();
        String sql = "SELECT seat FROM booking_history WHERE movie = ? AND section = ? AND date = ? AND time = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movieName);
            stmt.setString(2, section);
            stmt.setString(3, date.toString());
            stmt.setString(4, time);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookedSeats.add(rs.getString("seat"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSeatLayout() {
        seatGrid.getChildren().clear();
        seatGrid.setHgap(8);
        seatGrid.setVgap(8);
        seatGrid.setPadding(new Insets(20));
        seatGrid.setAlignment(Pos.CENTER);

        // Define seat ranges based on section
        int startSeat = 1, endSeat = 45;
        if ("Middle".equalsIgnoreCase(section)) {
            startSeat = 46;
            endSeat = 115;
        } else if ("Rear".equalsIgnoreCase(section)) {
            startSeat = 116;
            endSeat = 150;
        }


        Label screenLabel = new Label("SCREEN");
        screenLabel.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 16px; -fx-font-weight: bold;");
        screenLabel.setMaxWidth(Double.MAX_VALUE);
        screenLabel.setAlignment(Pos.CENTER);
        seatGrid.add(screenLabel, 0, 0, 4, 1);

        Region spacer = new Region();
        spacer.setPrefHeight(20);
        seatGrid.add(spacer, 0, 1, 4, 1);


        int row = 2;
        int col = 0;
        for (int i = startSeat; i <= endSeat; i++) {
            String seatId = "S" + i;
            Button seatButton = createSeatButton(seatId);
            seatGrid.add(seatButton, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
                if ((row - 2) % 2 == 0 && row < (2 + (endSeat - startSeat + 1) / 4)) {
                    Region aisleSpace = new Region();
                    aisleSpace.setPrefHeight(10);
                    seatGrid.add(aisleSpace, 0, row, 4, 1);
                    row++;
                }
            }
        }

        addLegend(row + 1);
    }

    private Button createSeatButton(String seatId) {
        Button seatButton = new Button(seatId);
        seatButton.setPrefSize(60, 40);
        seatButton.setFont(Font.font("System", FontWeight.BOLD, 12));

        if (bookedSeats.contains(seatId)) {
            seatButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-border-color: #7f8c8d; -fx-border-width: 2px;");
            seatButton.setDisable(true);
        } else {
            seatButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #34495e; -fx-border-width: 2px;");
            seatButton.setOnAction(e -> selectSeat(seatButton, seatId));

            seatButton.setOnMouseEntered(e -> {
                if (!seatButton.equals(selectedSeatButton)) {
                    seatButton.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: black; -fx-border-color: #3498db; -fx-border-width: 2px;");
                }
            });

            seatButton.setOnMouseExited(e -> {
                if (!seatButton.equals(selectedSeatButton)) {
                    seatButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #34495e; -fx-border-width: 2px;");
                }
            });
        }

        return seatButton;
    }

    private void selectSeat(Button seatButton, String seatId) {
        if (selectedSeatButton != null) {
            selectedSeatButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #34495e; -fx-border-width: 2px;");
        }

        selectedSeatButton = seatButton;
        selectedSeat = seatId;
        seatButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-color: #2980b9; -fx-border-width: 2px;");
        selectedSeatLabel.setText("Selected: " + seatId);
        confirmButton.setDisable(false);
    }

    private void addLegend(int row) {
        Label legendTitle = new Label("Legend:");
        legendTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        seatGrid.add(legendTitle, 0, row, 4, 1);

        HBox legendBox = new HBox(20);
        legendBox.setAlignment(Pos.CENTER);
        legendBox.setPadding(new Insets(10, 0, 0, 0));

        Rectangle availableRect = new Rectangle(20, 15);
        availableRect.setFill(Color.WHITE);
        availableRect.setStroke(Color.web("#34495e"));
        availableRect.setStrokeWidth(2);
        Label availableLabel = new Label("Available");
        HBox availableBox = new HBox(5, availableRect, availableLabel);
        availableBox.setAlignment(Pos.CENTER_LEFT);

        Rectangle bookedRect = new Rectangle(20, 15);
        bookedRect.setFill(Color.web("#95a5a6"));
        bookedRect.setStroke(Color.web("#7f8c8d"));
        bookedRect.setStrokeWidth(2);
        Label bookedLabel = new Label("Booked");
        HBox bookedBox = new HBox(5, bookedRect, bookedLabel);
        bookedBox.setAlignment(Pos.CENTER_LEFT);

        Rectangle selectedRect = new Rectangle(20, 15);
        selectedRect.setFill(Color.web("#3498db"));
        selectedRect.setStroke(Color.web("#2980b9"));
        selectedRect.setStrokeWidth(2);
        Label selectedLabel = new Label("Selected");
        HBox selectedBox = new HBox(5, selectedRect, selectedLabel);
        selectedBox.setAlignment(Pos.CENTER_LEFT);

        legendBox.getChildren().addAll(availableBox, bookedBox, selectedBox);
        seatGrid.add(legendBox, 0, row + 1, 4, 1);
    }

    @FXML
    public void handleConfirmBooking() {
        if (selectedSeat == null) {
            showAlert(Alert.AlertType.WARNING, "No Seat Selected", "Please select a seat first.");
            return;
        }

        try {
            int total = 500;
            StringBuilder snacks = new StringBuilder();
            if (popcorn) { total += 120; snacks.append("Popcorn "); }
            if (chicken) { total += 300; snacks.append("Chicken Fry "); }
            if (sprite) { total += 40; snacks.append("Sprite "); }

            double vat = total * 0.05;
            double finalAmount = total + vat;

            try (Connection conn = DBConnection.getConnection()) {
                String checkSql = "SELECT * FROM booking_history WHERE movie = ? AND seat = ? AND date = ? AND time = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, movieName);
                checkStmt.setString(2, selectedSeat);
                checkStmt.setDate(3, java.sql.Date.valueOf(date));
                checkStmt.setString(4, time);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    showAlert(Alert.AlertType.WARNING, "Seat Already Booked", "This seat was just booked by someone else. Please select another seat.");
                    loadBookedSeats();
                    createSeatLayout();
                    return;
                }

                String sql = "INSERT INTO booking_history (name, movie, seat, section, date, time, snacks, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, customerName);
                stmt.setString(2, movieName);
                stmt.setString(3, selectedSeat);
                stmt.setString(4, section);
                stmt.setDate(5, java.sql.Date.valueOf(date));
                stmt.setString(6, time);
                stmt.setString(7, snacks.toString().trim());
                stmt.setDouble(8, finalAmount);
                stmt.executeUpdate();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movieticket/ticketpreview.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Ticket Preview");

            TicketPreviewController previewController = loader.getController();
            previewController.setTicketDetails(customerName, movieName, selectedSeat, section,
                    date.toString(), time, snacks.toString().trim(), finalAmount);
            stage.show();

            Stage currentStage = (Stage) confirmButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Booking Failed", "An error occurred while processing your booking. Please try again.");
        }
    }

    @FXML
    public void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
