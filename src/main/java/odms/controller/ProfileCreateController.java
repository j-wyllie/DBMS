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

    private String checkDetailsEntered() {
        if (givenNamesField.getText().trim().equals("")) {
            return "Please enter Given Name(s)";
        }
        if (surnamesField.getText().trim().equals("")) {
            return "Please enter Surname(s)";

        }
        if (dobField.getText().trim().equals("")) {
            return "Please enter a Date of Birth";

        }
        if (irdField.getText().trim().equals("")) {
            return "Please enter an IRD number";
        } else {
            return "";
        }
    }

    public void initialize() {
        irdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                irdField.setText(newValue.replaceAll("[^\\d]", ""));
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
            invalidEntry(detailsCheckString);
        } else {
            try {
                String givenNames = givenNamesField.getText();
                String surnames = surnamesField.getText();
                String dob = dobField.getText();
                Integer ird = Integer.valueOf(irdField.getText());

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
                if (irdField.getText().length() > 9) {
                    invalidEntry("Entered IRD number is too long.\nPlease enter up to 9 digits");
                } else {
                    invalidEntry("Invalid IRD number entered");
                }
            } catch (IllegalArgumentException e) {
                if (!e.getMessage().equals("")) {
                    invalidEntry(e.getMessage());
                } else {
                    invalidEntry();
                }
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
