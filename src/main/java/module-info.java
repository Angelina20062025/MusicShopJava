module group.lab6.lab6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens group.lab6.lab6.controller to javafx.fxml;
    exports group.lab6.lab6.controller;

    opens group.lab6.lab6 to javafx.fxml;
    exports group.lab6.lab6;
}