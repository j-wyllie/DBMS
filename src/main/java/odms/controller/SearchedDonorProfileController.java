package odms.controller;

import static odms.controller.AlertController.InvalidUsername;
import static odms.controller.UndoRedoController.redo;
import static odms.controller.UndoRedoController.undo;
import com.google.gson.Gson;
import javafx.scene.control.TextArea;
import odms.commandlineview.CommandUtils;
import odms.data.DonorDataIO;
import odms.donor.Donor;
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

public class SearchedDonorProfileController {

    private Donor searchedDonor;

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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/EditSearchedDonorProfile.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        EditSearchedDonorProfileController controller = fxmlLoader.<EditSearchedDonorProfileController>getController();
        controller.setSearchedDonor(searchedDonor);
        controller.initialize();

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {
        if(searchedDonor != null) {
            try {
                donorFullNameLabel
                        .setText(searchedDonor.getGivenNames() + " " + searchedDonor.getLastNames());
                donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");

                if (searchedDonor.getRegistered() != null && searchedDonor.getRegistered() == true) {
                    donorStatusLabel.setText("Donor Status: Registered");
                }
                if (searchedDonor.getGivenNames() != null) {
                    givenNamesLabel.setText(givenNamesLabel.getText() + searchedDonor.getGivenNames());
                }
                if (searchedDonor.getLastNames() != null) {
                    lastNamesLabel.setText(lastNamesLabel.getText() + searchedDonor.getLastNames());
                }
                if (searchedDonor.getIrdNumber() != null) {
                    irdLabel.setText(irdLabel.getText() + searchedDonor.getIrdNumber());
                }
                if (searchedDonor.getDateOfBirth() != null) {
                    dobLabel.setText(dobLabel.getText() + searchedDonor.getDateOfBirth());
                }
                if (searchedDonor.getDateOfDeath() != null) {
                    dodLabel.setText(dodLabel.getText() + searchedDonor.getDateOfDeath());
                } else {
                    dodLabel.setText(dodLabel.getText() + "NULL");
                }
                if (searchedDonor.getGender() != null) {
                    genderLabel.setText(genderLabel.getText() + searchedDonor.getGender());
                }
                heightLabel.setText(heightLabel.getText() + searchedDonor.getHeight());
                weightLabel.setText(weightLabel.getText() + searchedDonor.getWeight());
                phoneLabel.setText(phoneLabel.getText());
                emailLabel.setText(emailLabel.getText());

                if (searchedDonor.getAddress() != null) {
                    addressLabel.setText(addressLabel.getText() + searchedDonor.getAddress());
                }
                if (searchedDonor.getRegion() != null) {
                    regionLabel.setText(regionLabel.getText() + searchedDonor.getRegion());
                }
                if (searchedDonor.getBloodType() != null) {
                    bloodTypeLabel.setText(bloodTypeLabel.getText() + searchedDonor.getBloodType());
                }
                if (searchedDonor.getHeight() != null && searchedDonor.getWeight() != null) {
                    bmiLabel.setText(bmiLabel.getText() + Math.round(searchedDonor.calculateBMI() * 100.00) / 100.00);
                }
                if (searchedDonor.getDateOfBirth() != null) {
                    ageLabel.setText(ageLabel.getText() + Integer.toString(searchedDonor.calculateAge()));
                }
                if (searchedDonor.getId() != null) {
                    userIdLabel.setText(userIdLabel.getText() + Integer.toString(searchedDonor.getId()));
                }
                organsLabel.setText(organsLabel.getText() + searchedDonor.getOrgans().toString());
                donationsLabel.setText(donationsLabel.getText() + searchedDonor.getDonatedOrgans().toString());
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
                String history = DonorDataIO.getHistory();
                Gson gson = new Gson();

                if (history.equals("")) {
                    history = gson.toJson(CommandUtils.getHistory());
                } else {
                    history = history.substring(0, history.length() - 1);
                    history = history + "," + gson.toJson(CommandUtils.getHistory()).substring(1);
                }
                history = history.substring(1, history.length() - 1);
                String[] actionHistory = history.split(",");

                ArrayList<String> userHistory = new ArrayList<>();

                for (String str : actionHistory) {
                    if (str.contains("Donor " + searchedDonor.getId())) {
                        userHistory.add(str);
                    }
                }
                historyView.setText(userHistory.toString());
            } catch (Exception e) {
                InvalidUsername();
            }
        }
    }

    public void setSearchedDonor(Donor donor) {
        searchedDonor = donor;
    }

    public Donor getSearchedDonor(Donor donor){
        return searchedDonor;
    }
}
