package odms.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import odms.enums.OrganEnum;

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
    public static void invalidOrgan(OrganEnum organ) {
        String error = "Cannot donate organ received from another donor.\n" +
                String.format("Organ '%s' received previously.", organ.getNamePlain());
        Alert invalidAlert = new Alert(
            AlertType.ERROR,
            error,
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
     * Creates a popup when the username or password entered was invalid.
     */
    static void invalidUsernameOrPassword() {
        Alert invalidAlert = new Alert(
                AlertType.ERROR,
                "Incorrect username or password.",
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
     * Creates a popup with a personalized message from the controller
     */
    static void uniqueUsername() {
        Alert invalidAlert = new Alert(
                AlertType.ERROR,
                "Please enter a unique username",
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
    static boolean profileSaveChanges() {
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
    static boolean profileCancelChanges() {
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

    /**
     * Displays a popup prompting the user to confirm cancellation of changes made
     * @return true or false on whether the changes were confirmed
     */
    static boolean deleteUserConfirmation() {
        Alert cancelAlert = new Alert(
                AlertType.CONFIRMATION,
                "Are you sure you want to delete this user?",
                ButtonType.NO,
                ButtonType.YES
        );

        cancelAlert.showAndWait();

        return handleAlert(cancelAlert);
    }
}
