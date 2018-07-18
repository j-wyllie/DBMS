package odms.view.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import odms.controller.profile.ProfileCreateController;
import odms.view.CommonView;

import java.io.IOException;
import java.time.LocalDate;

public class ProfileCreateAccountView extends CommonView {
    @FXML
    private TextField givenNamesField;

    @FXML
    private TextField surnamesField;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private TextField irdNumberField;

    private ProfileCreateController controller = new ProfileCreateController(this);
    /**
     * Scene change to profile profile view if all required fields are filled in.
     *
     * @param event clicking on the create new account button.
     * @throws IOException throws IOException
     */
    @FXML
    private void handleCreateAccountButtonClicked(ActionEvent event) throws IOException {
        ProfileGeneralViewTODOReplacesDisplayController.initialize(controller.createAccount());
        changeScene(event, "/view/ProfileDisplay.fxml");
    }

    /**
     * Scene change to login view.
     *
     * @param event clicking on the log in link.
     * @throws IOException throws IOException
     */
    @FXML
    private void handleLoginLinkClicked(ActionEvent event) throws IOException {
        //controller.login();
        changeScene(event, "/view/Login.fxml");
    }

    public void initialize() {
        irdNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                irdNumberField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public String getGivenNamesFieldValue() {
        return givenNamesField.getText();
    }

    public String getsurnamesFieldValue() {
        return surnamesField.getText();
    }

    public LocalDate getdobDatePickerValue() {
        return dobDatePicker.getValue();
    }

    public String getirdNumberFieldValue() {
        return irdNumberField.getText();
    }

}
