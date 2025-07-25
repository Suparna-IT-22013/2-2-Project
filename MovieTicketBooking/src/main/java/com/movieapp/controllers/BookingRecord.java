package com.movieapp.controllers;

import javafx.beans.property.*;

public class BookingRecord {

    private final StringProperty name;
    private final StringProperty movie;
    private final StringProperty seat;
    private final StringProperty section;
    private final StringProperty date;
    private final StringProperty time;
    private final StringProperty snacks;
    private final DoubleProperty total;

    public BookingRecord(String name, String movie, String seat, String section, String date,
                         String time, String snacks, double total) {
        this.name = new SimpleStringProperty(name);
        this.movie = new SimpleStringProperty(movie);
        this.seat = new SimpleStringProperty(seat);
        this.section = new SimpleStringProperty(section);
        this.date = new SimpleStringProperty(date);
        this.time = new SimpleStringProperty(time);
        this.snacks = new SimpleStringProperty(snacks);
        this.total = new SimpleDoubleProperty(total);
    }

    public String getName() { return name.get(); }
    public String getMovie() { return movie.get(); }
    public String getSeat() { return seat.get(); }
    public String getSection() { return section.get(); }
    public String getDate() { return date.get(); }
    public String getTime() { return time.get(); }
    public String getSnacks() { return snacks.get(); }
    public double getTotal() { return total.get(); }

    public StringProperty nameProperty() { return name; }
    public StringProperty movieProperty() { return movie; }
    public StringProperty seatProperty() { return seat; }
    public StringProperty sectionProperty() { return section; }
    public StringProperty dateProperty() { return date; }
    public StringProperty timeProperty() { return time; }
    public StringProperty snacksProperty() { return snacks; }
    public DoubleProperty totalProperty() { return total; }
}
