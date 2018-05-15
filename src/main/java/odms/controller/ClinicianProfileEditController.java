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
import odms.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static odms.controller.AlertController.profileCancelChanges;
import static odms.controller.AlertController.profileSaveChanges;
import static odms.controller.AlertController.guiPopup;
import static odms.controller.GuiMain.getUserDatabase;

import odms.cli.CommandUtils;
import javafx.scene.control.TextField;

public class ClinicianProfileEditController extends CommonController{
    private static User currentUser;

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

    /**
     * Button handler to cancel the changes made to the fields.
     * @param event clicking on the cancel (x) button.
     */
    @FXML
    private void handleCancelButtonClicked(ActionEvent event) throws IOException {
        boolean cancelBool = profileCancelChanges();

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
        boolean saveBool = profileSaveChanges();
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
                guiPopup("Error. Not all fields were updated.");
            }

            UserDataIO.saveUsers(getUserDatabase(), "example/users.json");

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            ClinicianProfileController controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);
            controller.initialize();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Clinician");
            stage.setScene(scene);
            stage.show();
        }
        else {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            ClinicianProfileController controller = fxmlLoader.getController();
            controller.setCurrentUser(currentUser);
            controller.initialize();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Clinician");
            stage.setScene(scene);
            stage.show();
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

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
