package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.LoginController.getCurrentProfile;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;

import com.google.gson.Gson;
import javafx.application.Platform;
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

    @FXML
    private Label donorFullNameLabel;

    @FXML
    private Label donorStatusLabel;

    @FXML
    private Label givenNamesLabel;

    @FXML
    private Label lastNamesLabel;

    @FXML
    private Label irdLabel;

    @FXML
    private Label dobLabel;

    @FXML
    private Label dodLabel;

    @FXML
    private Label genderLabel;

    @FXML
    private Label heightLabel;

    @FXML
    private Label weightLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label regionLabel;

    @FXML
    private Label bloodTypeLabel;

    @FXML
    private Label smokerLabel;

    @FXML
    private Label alcoholConsumptionLabel;

    @FXML
    private Label bloodPressureLabel;

    @FXML
    private Label chronicDiseasesLabel;

    @FXML
    private Label organsLabel;

    @FXML
    private Label donationsLabel;

    @FXML
    private TextArea historyView;

    @FXML
    private Label bmiLabel;

    @FXML
    private Label ageLabel;

    @FXML
    private Label userIdLabel;


    /**
     * Scene change to log in view.
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
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) throws IOException {
        undo();
    }

    /**
     * Button handler to redo last undo action.
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) throws IOException {
        redo();
    }

    /**
     * Button handler to make fields editable.
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
                donorStatusLabel.setText("Donor Status: Registered");
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
            } else {
                dodLabel.setText(dodLabel.getText() + "NULL");
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
            if(currentProfile.getHeight() != null && currentProfile.getWeight() != null){
                bmiLabel.setText(bmiLabel.getText() + Math.round(currentProfile.calculateBMI() * 100.00) / 100.00);
            }
            if(currentProfile.getDateOfBirth() != null){
                ageLabel.setText(ageLabel.getText() + Integer.toString(currentProfile.calculateAge()));
            }
            if(currentProfile.getId() != null){
                userIdLabel.setText(userIdLabel.getText() + Integer.toString(currentProfile.getId()));
            }
            organsLabel.setText(organsLabel.getText() + currentProfile.getOrgans().toString());
            donationsLabel.setText(donationsLabel.getText() + currentProfile.getDonatedOrgans().toString());
            /*if (currentDonor.getSmoker() != null) {
                smokerLabel.setText(smokerLabel.getText() + currentDonor.getSmoker());
            }*/
            /*if (currentDonor.getAlcoholConsumption() != null) {
                alcoholConsumptionLabel.setText(alcoholConsumptionLabel.getText() + currentDonor.getAlcoholConsumption());
            }*/
            /*if (currentDonor.getBloodPressure() != null) {
                bloodPressureLabel.setText(bloodPressureLabel.getText() + currentDonor.getBloodPressure());
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
