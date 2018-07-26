package odms.controller;

import static odms.App.getProfileDb;
import static odms.controller.AlertController.invalidDate;
import static odms.controller.AlertController.invalidEntry;
import static odms.controller.AlertController.invalidNhi;
import static odms.controller.GuiMain.getCurrentDatabase;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.data.NHIConflictException;
import odms.data.ProfileDataIO;
import odms.data.ProfileDatabase;
import odms.profile.Profile;

public class ProfileCreateController extends CommonController {

    private static ProfileDatabase currentDatabase = getProfileDb();

    @FXML
    private TextField givenNamesField;

    @FXML
    private TextField surnamesField;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private TextField nhiNumberField;

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
        if (!nhiNumberField.getText().matches("^[A-HJ-NP-Z]{3}\\d{4}$")) {
            return "Please enter a valid NHI (e.g. ABC1234)";
        } else {
            return "";
        }
    }

    public void initialize() {
        nhiNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            String pattern = "^[A-HJ-NP-Z]{3}\\d{4}$";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(newValue);

            if (!m.matches() && !m.hitEnd()) {
                nhiNumberField.setText(oldValue);
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
                LocalDate dob = dobDatePicker.getValue();
                String nhi = nhiNumberField.getText();

                Profile newProfile = new Profile(givenNames, surnames, dob, nhi);

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
            } catch (IllegalArgumentException e) {
                //show error window.
                invalidEntry();
            } catch (NHIConflictException e) {
                invalidNhi();
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
