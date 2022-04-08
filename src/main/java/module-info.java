module apriori_ui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.aprior.apriori_ui to javafx.fxml;
    exports apriori_ui;
}