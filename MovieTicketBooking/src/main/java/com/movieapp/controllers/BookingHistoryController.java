package com.movieapp.controllers;

import com.movieapp.database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.application.Platform;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class BookingHistoryController implements Initializable {

    @FXML private TableView<BookingRecord> historyTable;
    @FXML private TableColumn<BookingRecord, String> nameCol;
    @FXML private TableColumn<BookingRecord, String> movieCol;
    @FXML private TableColumn<BookingRecord, String> seatCol;
    @FXML private TableColumn<BookingRecord, String> sectionCol;
    @FXML private TableColumn<BookingRecord, String> dateCol;
    @FXML private TableColumn<BookingRecord, String> timeCol;
    @FXML private TableColumn<BookingRecord, String> snacksCol;
    @FXML private TableColumn<BookingRecord, Double> totalCol;
    @FXML private Button deleteSelectedBtn;
    @FXML private Button deleteAllBtn;

    private ObservableList<BookingRecord> records = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        historyTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);


        nameCol.setPrefWidth(200);
        nameCol.setMinWidth(150);
        nameCol.setMaxWidth(250);

        movieCol.setPrefWidth(180);
        movieCol.setMinWidth(120);

        seatCol.setPrefWidth(80);
        seatCol.setMinWidth(60);

        sectionCol.setPrefWidth(100);
        sectionCol.setMinWidth(80);

        dateCol.setPrefWidth(120);
        dateCol.setMinWidth(100);

        timeCol.setPrefWidth(100);
        timeCol.setMinWidth(80);

        snacksCol.setPrefWidth(300);
        snacksCol.setMinWidth(200);

        totalCol.setPrefWidth(100);
        totalCol.setMinWidth(80);


        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        movieCol.setCellValueFactory(new PropertyValueFactory<>("movie"));
        seatCol.setCellValueFactory(new PropertyValueFactory<>("seat"));
        sectionCol.setCellValueFactory(new PropertyValueFactory<>("section"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        snacksCol.setCellValueFactory(new PropertyValueFactory<>("snacks"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));


        Platform.runLater(this::centerWindow);


        loadBookingHistory();
    }

    private void centerWindow() {
        try {
            Stage stage = (Stage) historyTable.getScene().getWindow();


            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();


            stage.setWidth(1400);
            stage.setHeight(700);


            double centerX = (screenBounds.getWidth() - 1400) / 2;
            double centerY = (screenBounds.getHeight() - 700) / 2;


            stage.setX(centerX);
            stage.setY(centerY);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBookingHistory() {
        records.clear();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM booking_history ORDER BY date DESC, time ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                records.add(new BookingRecord(
                        rs.getString("name"),
                        rs.getString("movie"),
                        rs.getString("seat"),
                        rs.getString("section"),
                        rs.getDate("date").toString(),
                        rs.getString("time"),
                        rs.getString("snacks"),
                        rs.getDouble("total")
                ));
            }

            historyTable.setItems(records);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteSelectedBooking() {
        BookingRecord selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a booking to delete").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this booking?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM booking_history WHERE name=? AND movie=? AND seat=? AND date=? AND time=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, selected.getName());
                stmt.setString(2, selected.getMovie());
                stmt.setString(3, selected.getSeat());
                stmt.setString(4, selected.getDate());
                stmt.setString(5, selected.getTime());
                stmt.executeUpdate();
                loadBookingHistory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void deleteAllBookings() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete ALL booking history?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM booking_history";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.executeUpdate();
                loadBookingHistory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}