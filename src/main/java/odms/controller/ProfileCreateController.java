package odms.controller;

import static odms.controller.AlertController.invalidDate;
import static odms.controller.AlertController.invalidEntry;
import static odms.controller.AlertController.invalidIrd;
import static odms.controller.GuiMain.getCurrentDatabase;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.data.IrdNumberConflictException;
import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;
import odms.profile.Profile;

public class ProfileCreateController extends CommonController {

    private static ProfileDatabase currentDatabase = getCurrentDatabase();

    @FXML
    private TextField givenNamesField;

    @FXML
    private TextField surnamesField;

    @FXML
    private TextField dobField;

    @FXML
    private TextField irdField;

    /**
     * Scene change to profile profile view if all required fields are filled in.
     * @param event clicking on the create new account button.
     * @throws IOException throws IOException
     */
    @FXML
    private void handleCreateAccountButtonClicked(ActionEvent event) throws IOException {
        if(givenNamesField.getText().trim().equals("") || surnamesField.getText().trim().equals("") ||
                dobField.getText().trim().equals("") || irdField.getText().trim().equals("")) {
            invalidEntry();
        } else {
            try {
                String givenNames = givenNamesField.getText();
                String surnames = surnamesField.getText();
                String dob = dobField.getText();
                String ird = irdField.getText();

                Profile newProfile = new Profile(givenNames, surnames, dob, Integer.valueOf(ird));

                currentDatabase.addProfile(newProfile);
                ProfileDataIO.saveData(currentDatabase);

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProfileDisplay.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                ProfileDisplayController controller = fxmlLoader.getController();

                controller.setLoggedInProfile(currentDatabase.getProfile(newProfile.getId()));
                controller.initialize();

                Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                appStage.setScene(scene);
                appStage.show();
            } catch (IllegalArgumentException e) {
                //show error window.
                invalidEntry();
            } catch (IrdNumberConflictException e) {
                invalidIrd();
            } catch (ArrayIndexOutOfBoundsException e) {
                invalidDate();
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
        showLoginScene(event);
    }
}
