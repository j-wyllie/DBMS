package odms.controller.Profile;

import static odms.controller.AlertController.invalidDate;
import static odms.controller.AlertController.invalidEntry;
import static odms.controller.AlertController.invalidIrd;
import static odms.controller.GuiMain.getCurrentDatabase;

import java.io.IOException;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.controller.AlertController;
import odms.controller.CommonController;
import odms.controller.data.IrdNumberConflictException;
import odms.controller.data.ProfileDataIO;
import odms.Model.Data.ProfileDatabase;
import odms.Model.profile.Profile;

public class ProfileCreateController extends CommonController {

    private static ProfileDatabase currentDatabase = getCurrentDatabase();

    @FXML
    private TextField givenNamesField;

    @FXML
    private TextField surnamesField;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private TextField irdNumberField;

    private String checkDetailsEntered() {
        if (givenNamesField.getText().isEmpty()) {
            return "Please enter Given Name(s)";
        }
        if (surnamesField.getText().isEmpty()) {
            return "Please enter Surname(s)";

        }
        if (dobDatePicker.getEditor().getText().isEmpty()) {
            return "Please enter a Date of Birth";
        }
        if (irdNumberField.getText().isEmpty()) {
            return "Please enter an IRD number";
        } else {
            return "";
        }
    }

    public void initialize() {
        irdNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                irdNumberField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Scene change to profile profile view if all required fields are filled in.
     * @param event clicking on the create new account button.
     * @throws IOException throws IOException
     */
    @FXML
    private void handleCreateAccountButtonClicked(ActionEvent event) throws IOException {
        String detailsCheckString = checkDetailsEntered();

        if (!detailsCheckString.equals("")) {
            AlertController.invalidEntry(detailsCheckString);
        } else {
            try {
                String givenNames = givenNamesField.getText();
                String surnames = surnamesField.getText();
                LocalDate dob = dobDatePicker.getValue();
                Integer ird = Integer.valueOf(irdNumberField.getText());

                Profile newProfile = new Profile(givenNames, surnames, dob, ird);

                currentDatabase.addProfile(newProfile);
                ProfileDataIO.saveData(currentDatabase);

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                        "/view/ProfileDisplay.fxml")
                );

                Scene scene = new Scene(fxmlLoader.load());
                ProfileDisplayController controller = fxmlLoader.getController();
                controller.setProfile(currentDatabase.getProfile(newProfile.getId()));
                controller.initialize();

                Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                appStage.setScene(scene);
                appStage.show();
            } catch (NumberFormatException e) {
                if (irdNumberField.getText().length() > 9) {
                    AlertController.invalidEntry("Entered IRD number is too long.\nPlease enter up to 9 digits");
                } else {
                    AlertController.invalidEntry("Invalid IRD number entered");
                }
            } catch (IllegalArgumentException e) {
                //show error window.
                AlertController.invalidEntry();
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
