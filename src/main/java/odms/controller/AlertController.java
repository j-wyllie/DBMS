package odms.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import odms.data.ProfileDatabase;

import static odms.controller.GuiMain.getCurrentDatabase;

public class AlertController {

    private static ProfileDatabase currentDatabase = getCurrentDatabase();

    /**
     * Creates a popup when the details were entered incorrectly
     */
    static void InvalidEntry() {
        Alert invalidAlert = new Alert(AlertType.ERROR, "Please enter your details correctly.",
                ButtonType.CLOSE);
        invalidAlert.show();
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Creates a popup when the username entered was invalid
     */
    static void InvalidUsername() {
        Alert invalidAlert = new Alert(
            AlertType.ERROR,
            "Please enter a valid username.",
            ButtonType.CLOSE
        );
        invalidAlert.show();

        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Creates a popup when the IRD number entered was invalid
     */
    static void InvalidIrd() {
        Alert invalidAlert = new Alert(AlertType.ERROR, "Please enter a valid IRD number.",
                ButtonType.CLOSE);

        invalidAlert.show();
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Creates a popup when the date entered is an incorrect format
     */
    static void InvalidDate() {
        Alert invalidAlert = new Alert(AlertType.ERROR, "Date entered is not in the format dd-mm-yyyy.",
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
    static void GuiPopup(String message) {
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
    static boolean DonorSaveChanges() {
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
    static boolean DonorCancelChanges() {
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
