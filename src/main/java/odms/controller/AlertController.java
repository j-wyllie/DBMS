package odms.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertController {

    /**
     * Creates a popup when the details were entered incorrectly
     */
    static void invalidEntry() {
        Alert invalidAlert = new Alert(
            AlertType.ERROR,
            "Please enter your details correctly.",
            ButtonType.CLOSE
        );

        invalidAlert.show();
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Creates a popup when the details were entered incorrectly
     */
    public static void invalidOrgan() {
        Alert invalidAlert = new Alert(
            AlertType.ERROR,
            "A user cannot require and donate the same organ.",
            ButtonType.CLOSE
        );

        invalidAlert.show();
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Creates a popup when the username entered was invalid
     */
    static void invalidUsername() {
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
    static void invalidIrd() {
        Alert invalidAlert = new Alert(
            AlertType.ERROR,
            "Please enter a valid IRD number.",
            ButtonType.CLOSE
        );

        invalidAlert.show();
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Creates a popup when the date entered is an incorrect format
     */
    static void invalidDate() {
        Alert invalidAlert = new Alert(
            AlertType.ERROR,
            "Date entered is not in the format dd-mm-yyyy.",
                ButtonType.CLOSE
        );

        invalidAlert.show();
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }


    /**
     * Creates a popup with a personalized message from the controller
     * @param message the message to be displayed
     */
    static void guiPopup(String message) {
        Alert invalidAlert = new Alert(
            AlertType.ERROR,
            message,
            ButtonType.CLOSE
        );

        invalidAlert.show();
        if (invalidAlert.getResult() == ButtonType.CLOSE) {
            invalidAlert.close();
        }
    }

    /**
     * Displays a popup prompting the user to confirm the changes they have made.
     * @return true or false on whether the changes were confirmed
     */
    static boolean donorSaveChanges() {
        Alert saveAlert = new Alert(
            AlertType.CONFIRMATION,
            "Do you wish to save your changes?",
            ButtonType.NO,
            ButtonType.YES
        );

        saveAlert.showAndWait();

        return handleAlert(saveAlert);
    }

    /**
     * Displays a popup prompting the user to confirm cancellation of changes made
     * @return true or false on whether the changes were confirmed
     */
    static boolean donorCancelChanges() {
        Alert cancelAlert = new Alert(
            AlertType.CONFIRMATION,
            "Do you wish to cancel your changes?",
            ButtonType.NO,
            ButtonType.YES
        );

        cancelAlert.showAndWait();

        return handleAlert(cancelAlert);
    }

    /**
     * Handle alert window responses
     * @param alert to handle
     * @return boolean of action chosen
     */
    private static boolean handleAlert(Alert alert) {
        if (alert.getResult() == ButtonType.YES) {
            alert.close();
            return true;
        } else {
            System.out.println(false);
            return false;
        }
    }
}
