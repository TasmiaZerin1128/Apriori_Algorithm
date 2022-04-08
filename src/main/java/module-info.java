module com.aprior.apriori_ui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.aprior.apriori_ui to javafx.fxml;
    exports com.aprior.apriori_ui;
}