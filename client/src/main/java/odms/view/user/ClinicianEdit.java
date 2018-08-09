package odms.view.user;

import static odms.controller.AlertController.guiPopup;
import static odms.controller.AlertController.profileCancelChanges;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import odms.model.user.User;
import odms.view.CommonView;
import odms.controller.database.DAOFactory;
import odms.controller.database.UserDAO;

import static odms.controller.AlertController.saveChanges;

public class ClinicianEdit extends CommonView {
    private static User currentUser;

    private odms.controller.user.ClinicianEdit controller = new odms.controller.user.ClinicianEdit(this);

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

    @FXML
    private Text pictureText;


    /**
     * File picker to choose only supported image types.
     *
     * @param event clicking on the choose file button.
     */

    @FXML
    private void clinicianChooseImageClicked(ActionEvent event) throws IOException {
        File chosenFile = chooseImage(pictureText);
        if (chosenFile != null) {
            String extension = getFileExtension(chosenFile).toLowerCase();
            File deleteFile;
            if ("jpg".equalsIgnoreCase(extension)) {
                deleteFile = new File(LOCALPATH + "\\" +
                        currentUser.getStaffID().toString() + ".jpg"
                );
            } else {
                deleteFile = new File(LOCALPATH + "\\" +
                        currentUser.getStaffID().toString() + ".png"
                );
            }
            if (deleteFile.delete()) {
                System.out.println("Old file deleted successfully");
            } else {
                System.out.println("Failed to delete the old file");
            }
            File pictureDestination = new File(LOCALPATH + "\\" +
                    currentUser.getStaffID().toString() + "." + extension);
            copyFileUsingStream(chosenFile, pictureDestination);
            currentUser.setPictureName(chosenFile.getName());
        }
    }


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

            UserDAO database = DAOFactory.getUserDao();
            try {
                database.update(currentUser);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            openClinicianWindow(event);
        }
    }

    public void openClinicianWindow(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfile.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        ClinicianProfile v = fxmlLoader.getController();
        v.setCurrentUser(currentUser);
        v.initialize();

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
