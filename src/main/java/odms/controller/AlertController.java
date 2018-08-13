package odms.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import odms.model.enums.OrganEnum;

public class AlertController {

    /**
     * Creates a generic popup when details are entered incorrectly.
     */
    public static void invalidEntry() {
        invalidEntry("Please enter your details correctly.");
    }

    /**
     * Creates a message based popup when the details are entered incorrectly.
     * @param message the message to be displayed
     */
    public static void invalidEntry(String message) {
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
    public static void invalidUsername() {
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
    public static void invalidUsernameOrPassword() {
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
     * Creates a popup when the NHI number entered was invalid
     */
    public static void invalidNhi() {
        Alert invalidAlert = new Alert(
            AlertType.ERROR,
            "Please enter a valid NHI number.",
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
    public static void invalidDate() {
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
    public static void guiPopup(String message) {
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
    public static void uniqueUsername() {
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
     *
     * @return true or false on whether the changes were confirmed
     */
    public static boolean saveChanges() {
        Alert saveAlert = new Alert(
                AlertType.CONFIRMATION,
                "Do you wish to save your changes?",
                ButtonType.CANCEL,
                ButtonType.YES
        );

        saveAlert.showAndWait();

        return handleAlert(saveAlert);
    }

    /**
     * Displays a popup prompting the user to confirm the action they want to perform.
     * @return true or false on whether the changes were confirmed
     */
    public static boolean generalConfirmation(String message) {
        Alert confirmAlert = new Alert(
                AlertType.CONFIRMATION,
                message,
                ButtonType.NO,
                ButtonType.YES
        );

        confirmAlert.showAndWait();

        return handleAlert(confirmAlert);
    }

    /**
     * Displays a popup prompting the user to confirm cancellation of changes made
     * @return true or false on whether the changes were confirmed
     */
    public static boolean profileCancelChanges() {
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
    public static boolean deleteUserConfirmation() {
        Alert cancelAlert = new Alert(
                AlertType.CONFIRMATION,
                "Are you sure you want to delete this user?",
                ButtonType.NO,
                ButtonType.YES
        );

        cancelAlert.showAndWait();

        return handleAlert(cancelAlert);
    }

    /**
     * Displays a popup prompting the user to that they have unsaved changes somewhere in the
     * program
     *
     * @return true or false on whether the changes were confirmed
     */
    public static boolean unsavedChangesImport() {
        Alert cancelAlert = new Alert(
            AlertType.CONFIRMATION,
            "You have unsaved changes.\n" +
                "Do you want to continue without saving?",
            ButtonType.CANCEL,
            ButtonType.YES
        );

        cancelAlert.showAndWait();

        return handleAlert(cancelAlert);
    }
}
