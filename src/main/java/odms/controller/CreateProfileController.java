package odms.controller;

import static odms.controller.AlertController.InvalidEntry;
import static odms.controller.Main.getCurrentDatabase;
import static odms.controller.AlertController.InvalidEntry;
import static odms.controller.Main.getCurrentDatabase;

import odms.data.DonorDatabase;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.data.DonorDatabase;

public class CreateProfileController {

    private static DonorDatabase currentDatabase = getCurrentDatabase();

    /**
     * TextField to input the new user's given names.
     */
    @FXML
    private TextField givenNamesField;

    /**
     * TextField to input the new user's surnames.
     */
    @FXML
    private TextField surnamesField;

    /**
     * TextField to input the new user's dob.
     */
    @FXML
    private TextField dobField;

    /**
     * TextField to input the new user's ird number.
     */
    @FXML
    private TextField irdField;

    /**
     * Scene change to donor profile view if all required fields are filled in.
     * @param event clicking on the create new account button.
     * @throws IOException
     */
    @FXML
    private void handleCreateAccountButtonClicked(ActionEvent event) throws IOException {

        try {
            String givenNames = givenNamesField.getText();
            String surnames = surnamesField.getText();
            String dob = dobField.getText();
            String ird = irdField.getText();

           /* Donor newDonor = new Donor(givenNames, surnames, dob, ird);
            currentDatabase.addDonor(newDonor);*/
        }
        catch (IllegalArgumentException e) {
            //show error window.
            InvalidEntry();
        }
        finally {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
            Scene newScene = new Scene(parent);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(newScene);
            appStage.show();
        }
    }

    /**
     * Scene change to login view.
     * @param event clicking on the log in link.
     * @throws IOException
     */
    @FXML
    private void handleLoginLinkClicked(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }
}
