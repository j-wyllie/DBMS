package odms.controller;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.user.User;

import java.io.IOException;

import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

public class ClinicianProfileController {

    //Get the default clinician
    private static User currentUser = GuiMain.getUserDatabase().getClinician(0);


    /**
     * Label for the name at the top of the window
     */
    @FXML
    private Label clinicianFullName;

    /**
     * Label to display the clinicians given names
     */
    @FXML
    private Label givenNamesLabel;

    /**
     * Label to display the clinicians last names.
     */
    @FXML
    private Label lastNamesLabel;

    /**
     * Label to display the clinicians staff ID.
     */
    @FXML
    private Label staffIdLabel;

    /**
     * Label to display the clinicians work address.
     */
    @FXML
    private Label addressLabel;

    /**
     * Label to display the clinicians region.
     */
    @FXML
    private Label regionLabel;



    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();

    }

    /**
     * Button handler to undo last action.
     *
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) throws IOException {
        //TODO
        //refresh scene.
        undo();
        //initialize();
    }

    /**
     * Button handler to redo last undo action.
     *
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) throws IOException {
        //TODO
        //refresh scene.
        redo();
        //initialize();
    }

    /**
     * Button handler to make fields editable.
     *
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/EditDonorProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    @FXML
    private void initialize(){
        clinicianFullName.setText(currentUser.getName());
        givenNamesLabel.setText(givenNamesLabel.getText() + currentUser.getName());
        staffIdLabel.setText(staffIdLabel.getText() + currentUser.getStaffId().toString());
        regionLabel.setText(regionLabel.getText() + currentUser.getRegion());
    }
}
