package com.movieapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TicketPreviewController {

    @FXML private Label nameLabel;
    @FXML private Label movieLabel;
    @FXML private Label seatLabel;
    @FXML private Label sectionLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label snacksLabel;
    @FXML private Label totalLabel;

    public void setTicketDetails(String name, String movie, String seat, String section, String date, String time, String snacks, double total) {
        nameLabel.setText(name);
        movieLabel.setText(movie);
        seatLabel.setText(seat);
        sectionLabel.setText(section);
        dateLabel.setText(date);
        timeLabel.setText(time);
        snacksLabel.setText(snacks.isEmpty() ? "None" : snacks);
        totalLabel.setText(String.format("%.2f TK", total));
    }
}
