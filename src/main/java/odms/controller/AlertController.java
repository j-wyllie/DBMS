package odms.controller;

import static odms.controller.Main.getCurrentDatabase;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import odms.data.DonorDatabase;

public class AlertController {

    private static DonorDatabase currentDatabase = getCurrentDatabase();

    public static void InvalidEntry() {

        Alert invalidAlert = new Alert(AlertType.ERROR, "Please enter your details correctly.",
                ButtonType.CLOSE);
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    public static void InvalidUsername() {

        Alert invalidAlert = new Alert(AlertType.ERROR, "Please enter a valid username.",
                ButtonType.CLOSE);

        invalidAlert.show();

        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    public static boolean DonorSaveChanges() {
        Alert saveAlert = new Alert(AlertType.CONFIRMATION, "Do you wish to save your changes?",
                ButtonType.NO, ButtonType.YES);

        saveAlert.showAndWait();

        if (saveAlert.getResult() == ButtonType.NO) {
            saveAlert.close();
            return false;
        }
        else if (saveAlert.getResult() == ButtonType.YES) {
            saveAlert.close();
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean DonorCancelChanges() {
        Alert cancelAlert = new Alert(AlertType.CONFIRMATION, "Do you wish to cancel your changes?",
                ButtonType.NO, ButtonType.YES);

        cancelAlert.showAndWait();

        if (cancelAlert.getResult() == ButtonType.NO) {
            cancelAlert.close();
            return false;
        }
        else if (cancelAlert.getResult() == ButtonType.YES) {
            cancelAlert.close();
            return true;
        }
        else {
            return false;
        }
    }
}
