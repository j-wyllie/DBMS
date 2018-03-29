package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.LoginController.getCurrentProfile;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

import com.google.gson.Gson;
import javafx.scene.control.TextArea;
import odms.cli.CommandUtils;
import odms.data.ProfileDataIO;
import odms.profile.Profile;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DonorProfileController {

    /**
     * Label to display the user's full name.
     */
    @FXML
    private Label donorFullNameLabel;

    /**
     * Label to display the user's profile status.
     */
    @FXML
    private Label donorStatusLabel;

    /**
     * Label to display the user's given names.
     */
    @FXML
    private Label givenNamesLabel;

    /**
     * Label to display the user's surnames.
     */
    @FXML
    private Label lastNamesLabel;

    /**
     * Label to display the user's ird number.
     */
    @FXML
    private Label irdLabel;

    /**
     * Label to display the user's date of birth.
     */
    @FXML
    private Label dobLabel;

    /**
     * Label to display the user's date of death.
     */
    @FXML
    private Label dodLabel;

    /**
     * Label to display the user's gender.
     */
    @FXML
    private Label genderLabel;

    /**
     * Label to display the user's height.
     */
    @FXML
    private Label heightLabel;

    /**
     * Label to display the user's weight.
     */
    @FXML
    private Label weightLabel;

    /**
     * Label to display the user's phone number.
     */
    @FXML
    private Label phoneLabel;

    /**
     * Label to display the user's email.
     */
    @FXML
    private Label emailLabel;

    /**
     * Label to display the user's address.
     */
    @FXML
    private Label addressLabel;

    /**
     * Label to display the user's region.
     */
    @FXML
    private Label regionLabel;

    /**
     * Label to display the user's blood type.
     */
    @FXML
    private Label bloodTypeLabel;

    /**
     * Label to display the user's smoker status.
     */
    @FXML
    private Label smokerLabel;

    /**
     * Label to display the user's alcohol consumption.
     */
    @FXML
    private Label alcoholConsumptionLabel;

    /**
     * Label to display the user's blood pressure.
     */
    @FXML
    private Label bloodPressureLabel;

    /**
     * Label to display the user's region.
     */
    @FXML
    private Label chronicDiseasesLabel;

    /**
     * Label to display the user's organs to donate.
     */
    @FXML
    private Label organsLabel;

    /**
     * Label to display the user's region.
     */
    @FXML
    private Label donationsLabel;

    /**
     * View to display history
     */
    @FXML
    private TextArea historyView;



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

    /**
     * Sets the current profile attributes to the labels on start up.
     */
    @FXML
    public void initialize() {

        Profile currentProfile = getCurrentProfile();

        try {
            donorFullNameLabel
                    .setText(currentProfile.getGivenNames() + " " + currentProfile.getLastNames());

            donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");

            if (currentProfile.getRegistered() != null && currentProfile.getRegistered() == true) {
                donorStatusLabel.setText(donorStatusLabel.getText() + "Registered");
            }

            if (currentProfile.getGivenNames() != null) {
                givenNamesLabel.setText(givenNamesLabel.getText() + currentProfile.getGivenNames());
            }
            if (currentProfile.getLastNames() != null) {
                lastNamesLabel.setText(lastNamesLabel.getText() + currentProfile.getLastNames());
            }
            if (currentProfile.getIrdNumber() != null) {
                irdLabel.setText(irdLabel.getText() + currentProfile.getIrdNumber());
            }
            if (currentProfile.getDateOfBirth() != null) {
                dobLabel.setText(dobLabel.getText() + currentProfile.getDateOfBirth());
            }
            if (currentProfile.getDateOfDeath() != null) {
                dodLabel.setText(dodLabel.getText() + currentProfile.getDateOfDeath());
            }
            if (currentProfile.getGender() != null) {
                genderLabel.setText(genderLabel.getText() + currentProfile.getGender());
            }
            heightLabel.setText(heightLabel.getText() + currentProfile.getHeight());
            weightLabel.setText(weightLabel.getText() + currentProfile.getWeight());
            phoneLabel.setText(phoneLabel.getText());
            emailLabel.setText(emailLabel.getText());

            if (currentProfile.getAddress() != null) {
                addressLabel.setText(addressLabel.getText() + currentProfile.getAddress());
            }
            if (currentProfile.getRegion() != null) {
                regionLabel.setText(regionLabel.getText() + currentProfile.getRegion());
            }
            if (currentProfile.getBloodType() != null) {
                bloodTypeLabel.setText(bloodTypeLabel.getText() + currentProfile.getBloodType());
            }
            /*if (currentProfile.getSmoker() != null) {
                smokerLabel.setText(smokerLabel.getText() + currentProfile.getSmoker());
            }*/
            /*if (currentProfile.getAlcoholConsumption() != null) {
                alcoholConsumptionLabel.setText(alcoholConsumptionLabel.getText() + currentProfile.getAlcoholConsumption());
            }*/
            /*if (currentProfile.getBloodPressure() != null) {
                bloodPressureLabel.setText(bloodPressureLabel.getText() + currentProfile.getBloodPressure());
            }*/
            //chronic diseases.
            //organs to donate.
            //past donations.
            String history = ProfileDataIO.getHistory();
            Gson gson = new Gson();


            if(history.equals("")) {
                history = gson.toJson(CommandUtils.getHistory());
            } else {
                history = history.substring(0, history.length()-1);
                history = history+","+gson.toJson(CommandUtils.getHistory()).substring(1);
            }
            history = history.substring(1, history.length()-1);
            String[] actionHistory = history.split(",");

            ArrayList<String> userHistory = new ArrayList<>();

            for(String str : actionHistory){
                if(str.contains("Profile " + getCurrentProfile().getId())){
                    userHistory.add(str);
                }
            }

            historyView.setText(userHistory.toString());
        } catch (Exception e) {
            InvalidUsername();
        }
    }
}
