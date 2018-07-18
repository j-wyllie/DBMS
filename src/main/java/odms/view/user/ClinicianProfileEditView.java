package odms.view.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.controller.data.UserDataIO;
import odms.controller.user.ClinicianProfileEditController;
import odms.model.user.User;
import odms.view.CommonView;

import java.io.IOException;

import static odms.controller.AlertController.guiPopup;
import static odms.controller.AlertController.profileCancelChanges;
import static odms.controller.AlertController.saveChanges;
import static odms.controller.GuiMain.getUserDatabase;

public class ClinicianProfileEditView extends CommonView {
    private static User currentUser;

    private ClinicianProfileEditController controller = new ClinicianProfileEditController(this);

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
     *
     * @param event clicking on the cancel (x) button.
     */
    @FXML
    private void handleCancelButtonClicked(ActionEvent event) throws IOException {
        boolean cancelBool = profileCancelChanges();
        if (cancelBool) {
            openClinicianWindow(event);
        }
    }

    /**
     * Button handler to save the changes made to the fields.
     *
     * @param event clicking on the save (tick) button.
     */
    @FXML
    private void handleSaveButtonClicked(ActionEvent event) throws IOException {
        boolean error = false;

        if (saveChanges()) {
            controller.save();

            if (error) {
                guiPopup("Error. Not all fields were updated.");
            }

            UserDataIO.saveUsers(getUserDatabase(), "example/users.json");

            openClinicianWindow(event);
        }
    }

    public void openClinicianWindow(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        ClinicianProfileControllerTODO controller = fxmlLoader.getController();
        controller.setCurrentUser(currentUser);
        controller.initialize();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Clinician");
        stage.setScene(scene);
        stage.show();
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
            if (currentUser.getStaffID() != null) {
                staffIdField.setText(currentUser.getStaffID().toString());
            }

            if (currentUser.getWorkAddress() != null) {
                addressField.setText(currentUser.getWorkAddress());
            }

            if (currentUser.getRegion() != null) {
                regionField.setText(currentUser.getRegion());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getUser() {
        return currentUser;
    }

    public String getGivenNamesField() {
        return givenNamesField.getText();
    }

    public String getStaffIdField() {
        return staffIdField.getText();
    }

    public String getAddressField() {
        return addressField.getText();
    }

    public String getRegionField() {
        return regionField.getText();
    }
}
