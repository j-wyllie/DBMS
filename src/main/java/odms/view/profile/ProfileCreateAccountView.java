package odms.view.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.controller.profile.ProfileCreateController;
import odms.model.profile.Profile;
import odms.view.CommonView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileCreateAccountView extends CommonView {
    @FXML
    private TextField givenNamesField;

    @FXML
    private TextField surnamesField;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private TextField nhiField;

    private ProfileCreateController controller = new ProfileCreateController(this);

    /**
     * Scene change to profile view if all required fields are filled in.
     *
     * @param event clicking on the create new account button.
     * @throws IOException throws IOException
     */
    @FXML
    private void handleCreateAccountButtonClicked(ActionEvent event) throws IOException {
        if(controller.createAccount() == null) {
            return;
        }
        System.out.println(controller.createAccount());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ProfileDisplay.fxml"));
        ProfileDisplayControllerTODO v = fxmlLoader.getController();
        v.initialize(controller.createAccount());
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
        nhiField.textProperty().addListener((observable, oldValue, newValue) -> {
            String pattern = "^[A-HJ-NP-Z]{3}\\d{4}$";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(newValue);

            if (!m.matches() && !m.hitEnd()) {
                nhiField.setText(oldValue);
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

    public String getNhiField() {
        return nhiField.getText();
    }

    public void setGivenNamesFieldValue(String s) {
        givenNamesField.setText(s);
    }

    public void setsurnamesFieldValue(String s) {
        surnamesField.setText(s);
    }

    public void setNhiField(String s) {
        nhiField.setText(s);
    }

    public void setdobDatePickerValue(LocalDate l) {
        dobDatePicker.setValue(l);
    }

    public ProfileCreateController getController() {
        return controller;
    }
}
