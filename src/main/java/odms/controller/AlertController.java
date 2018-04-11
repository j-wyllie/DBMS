package odms.controller;

import static odms.controller.GuiMain.getCurrentDatabase;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import odms.data.DonorDatabase;

public class AlertController {

    private static DonorDatabase currentDatabase = getCurrentDatabase();

    /**
     * Creates a popup when the details were entered incorrectly
     */
    public static void InvalidEntry() {
        Alert invalidAlert = new Alert(AlertType.ERROR, "Please enter your details correctly.",
                ButtonType.CLOSE);
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Creates a popup when the username entered was invalid
     */
    public static void InvalidUsername() {
        Alert invalidAlert = new Alert(AlertType.ERROR, "Please enter a valid username.",
                ButtonType.CLOSE);
        invalidAlert.show();

        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Creates a popup when the IRD number entered was invalid
     */
    public static void InvalidIrd() {
        Alert invalidAlert = new Alert(AlertType.ERROR, "Please enter a valid IRD number.",
                ButtonType.CLOSE);

        invalidAlert.show();
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Creates a popup with a personalized message from the controller
     * @param message the message to be displayed
     */
    public static void GuiPopup(String message) {
        Alert invalidAlert = new Alert(AlertType.ERROR, message,
                ButtonType.CLOSE);

        invalidAlert.show();
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Displays a popup prompting the user to confirm the changes they have made.
     * @return true or false on whether the changes were confirmed
     */
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

    /**
     * Displays a popup prompting the user to confirm cancellation of changes made
     * @return true or false on whether the changes were confirmed
     */
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
