package odms.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertController {

    public static void InvalidEntry() {

        Alert invalidAlert = new Alert(AlertType.ERROR, "Please enter your details correctly.",
                ButtonType.OK);
        if (invalidAlert.getResult() == ButtonType.OK) {
            invalidAlert.close();
        }
    }

    public static void InvalidUsername() {

        Alert invalidAlert = new Alert(AlertType.ERROR, "Please enter a valid username.",
                ButtonType.OK);
        if (invalidAlert.getResult() == ButtonType.OK) {
            invalidAlert.close();
        }
    }
}
