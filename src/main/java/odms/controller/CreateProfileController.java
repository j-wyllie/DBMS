package odms.controller;

import static odms.controller.AlertController.InvalidDate;
import static odms.controller.AlertController.InvalidEntry;
import static odms.controller.AlertController.InvalidIrd;
import static odms.controller.GuiMain.getCurrentDatabase;

import odms.commandlineview.CommandUtils;
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
import odms.data.IrdNumberConflictException;
import odms.donor.Donor;

public class CreateProfileController {

    private static DonorDatabase currentDatabase = getCurrentDatabase();

    @FXML
    private TextField givenNamesField;

    @FXML
    private TextField surnamesField;

    @FXML
    private TextField dobField;

    @FXML
    private TextField irdField;

    /**
     * Scene change to donor profile view if all required fields are filled in.
     * @param event clicking on the create new account button.
     * @throws IOException throws IOException
     */
    @FXML
    private void handleCreateAccountButtonClicked(ActionEvent event) throws IOException {
        if(givenNamesField.getText().trim().equals("") || surnamesField.getText().trim().equals("") ||
                dobField.getText().trim().equals("") || irdField.getText().trim().equals("")) {
            InvalidEntry();
        } else {
            try {
                String givenNames = givenNamesField.getText();
                String surnames = surnamesField.getText();
                String dob = dobField.getText();
                Integer ird = Integer.parseInt(irdField.getText());


                Donor newDonor = new Donor(givenNames, surnames, dob, ird);
                currentDatabase.addDonor(newDonor);
                CommandUtils.addDonorHistory(newDonor.getId());
                LoginController.setCurrentDonor(newDonor.getId());
                Parent parent = FXMLLoader.load(getClass().getResource("/view/DonorProfile.fxml"));
                Scene newScene = new Scene(parent);
                Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                appStage.setScene(newScene);
                appStage.show();
            } catch (IllegalArgumentException e) {
                //show error window.
                InvalidEntry();
            } catch (IrdNumberConflictException e) {
                InvalidIrd();
            } catch (ArrayIndexOutOfBoundsException e) {
                InvalidDate();
            }
        }
    }

    /**
     * Scene change to login view.
     * @param event clicking on the log in link.
     * @throws IOException throws IOException
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
