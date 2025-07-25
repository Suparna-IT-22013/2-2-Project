module com.example.movieticket {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.movieapp.controllers to javafx.fxml;
    opens com.movieapp.database to javafx.fxml;
    opens com.example to javafx.fxml;

    exports com.example;
    exports com.movieapp.controllers;
}
