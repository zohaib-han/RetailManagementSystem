module com.example.inventra {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.inventra.Controller to javafx.fxml;
    exports com.example.inventra;
}