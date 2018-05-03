package odms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import odms.data.UserDataIO;
import odms.profile.Profile;
import odms.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static odms.controller.AlertController.DonorCancelChanges;
import static odms.controller.AlertController.DonorSaveChanges;
import static odms.controller.LoginController.getCurrentUser;
import static odms.controller.AlertController.GuiPopup;
import static odms.controller.GuiMain.getUserDatabase;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;
import odms.cli.CommandUtils;
import javafx.scene.control.TextField;

public class EditClinicianProfileController extends CommonController{
    private static User currentUser = getCurrentUser();

    @FXML
    private Label clinicianFullName;

    @FXML
    private TextField givenNamesField;

    @FXML
    private TextField staffIdField;


    @FXML
    private TextField addressField;

    @FXML
    private TextField regionField;

    /*
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        showLoginScene(event);
    }

    /**
     * Button handler to cancel the changes made to the fields.
     * @param event clicking on the cancel (x) button.
     */
    @FXML
    private void handleCancelButtonClicked(ActionEvent event) throws IOException {
        boolean cancelBool = DonorCancelChanges();

        if (cancelBool) {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/ClinicianProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.show();
        }
    }

    /**
     * Button handler to save the changes made to the fields.
     * @param event clicking on the save (tick) button.
     */
    @FXML
    private void handleSaveButtonClicked(ActionEvent event) throws IOException {
        boolean saveBool = DonorSaveChanges();
        boolean error = false;
        if (saveBool) {
            String action =
                    "Clinician " + currentUser.getStaffId() + " updated details previous = " + currentUser
                            .getAttributesSummary() + " new = ";
            currentUser.setName(givenNamesField.getText());
            currentUser.setStaffId(Integer.valueOf(staffIdField.getText()));
            currentUser.setWorkAddress(addressField.getText());
            currentUser.setRegion(regionField.getText());

            action = action + currentUser.getAttributesSummary() + " at " + LocalDateTime.now();
            if (CommandUtils.getHistory().size() != 0) {
                if (CommandUtils.getPosition() != CommandUtils.getHistory().size() - 1) {
                    CommandUtils.currentSessionHistory.subList(CommandUtils.getPosition(),
                            CommandUtils.getHistory().size() - 1).clear();
                }
            }
            CommandUtils.currentSessionHistory.add(action);
            CommandUtils.historyPosition = CommandUtils.currentSessionHistory.size() - 1;

            if(error == true) {
                GuiPopup("Error. Not all fields were updated.");
            }

            UserDataIO.saveUsers(getUserDatabase(), "example/example.json");

            Parent parent = FXMLLoader.load(getClass().getResource("/view/ClinicianProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.show();
        }
        else {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.show();

        }
    }

    /**
     * Sets the current clinician attributes to the labels on start up.
     */
    @FXML
    public void initialize() {
        try {
            clinicianFullName.setText(currentUser.getName());

            if (currentUser.getName() != null) {
                givenNamesField.setText(currentUser.getName());
            }
            if (currentUser.getStaffId() != null) {
                staffIdField.setText(currentUser.getStaffId().toString());
            }

            if (currentUser.getWorkAddress() != null) {
                addressField.setText(currentUser.getWorkAddress());
            }

            if (currentUser.getRegion() != null) {
                regionField.setText(currentUser.getRegion());
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
