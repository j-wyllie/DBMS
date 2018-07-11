package odms.view.profile;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import odms.controller.CommonController;
import odms.controller.EditDateCell;
import odms.controller.EditingConditionsCell;
import odms.controller.GuiMain;
import odms.controller.condition.ConditionAddController;
import odms.controller.data.MedicationDataIO;
import odms.controller.data.ProfileDataIO;
import odms.controller.history.HistoryController;
import odms.controller.history.RedoController;
import odms.controller.history.UndoController;
import odms.controller.medication.MedicationHistory;
import odms.controller.procedure.ProcedureAddController;
import odms.controller.procedure.ProcedureEditController;
import odms.controller.profile.ProfileEditController;
import odms.model.history.History;
import odms.model.medications.Drug;
import odms.model.profile.Condition;
import odms.model.profile.Procedure;
import odms.model.profile.Profile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static odms.controller.AlertController.invalidUsername;
import static odms.controller.AlertController.saveChanges;
import static odms.controller.GuiMain.getCurrentDatabase;
import static odms.controller.data.MedicationDataIO.getActiveIngredients;
import static odms.controller.data.MedicationDataIO.getSuggestionList;

public class ProfileDisplayController extends CommonController {

    public Profile currentProfile;
    /**
     * Text for showing recent edits.
     */
    @FXML
    public Text editedText;
    protected ObjectProperty<Profile> currentProfileBound = new SimpleObjectProperty<>();
    private Boolean isOpenedByClinician = false;
    // Displays in IntelliJ as unused but is a false positive
    // The FXML includes operate this way and allow access to the instantiated controller.
    @FXML
    private AnchorPane profileOrganOverview;
    @FXML
    private ProfileOrganOverviewController profileOrganOverviewController;
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
    private TextArea historyView;
    @FXML
    private Label bmiLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label userIdLabel;
    @FXML
    private Button logoutButton;

    @FXML
    private Button buttonViewMedicationHistory;
    @FXML
    private Button addNewProcedureButton;

    @FXML
    private Button deleteProcedureButton;





    @FXML
    private Label receiverStatusLabel;

    @FXML
    private Label labelGenderPreferred;

    @FXML
    private Label labelPreferredName;
    private RedoController redoController = new RedoController();
    private UndoController undoController = new UndoController();

    /**
     * Called when there has been an edit to the current profile.
     */
    public void editedTextArea() {
        editedText.setText("The profile was successfully edited.");
    }








    /**
     * Scene change to log in view.
     *
     * @param event clicking on the logout button.
     */
    @FXML
    private void handleLogoutButtonClicked(ActionEvent event) throws IOException {
        showLoginScene(event);
    }

    /**
     * Button handler to undo last action.
     */
    @FXML
    private void handleUndoButtonClicked() {
        undoController.undo(GuiMain.getCurrentDatabase());
    }

    /**
     * Button handler to redo last undo action.
     */
    @FXML
    private void handleRedoButtonClicked() {
        redoController.redo(GuiMain.getCurrentDatabase());
    }

    /**
     * Button handler to make fields editable.
     *
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ProfileEdit.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ProfileEditController controller = fxmlLoader.getController();
        controller.setCurrentProfile(currentProfile);
        controller.setIsClinician(isOpenedByClinician);
        controller.initialize();

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        appStage.setScene(scene);
        appStage.show();
    }


    /**
     * sets all of the items in the fxml to their respective values
     *
     * @param currentProfile donors profile
     */
    @FXML
    private void setPage(Profile currentProfile) {

        try {
            donorFullNameLabel.setText(currentProfile.getFullName());
            donorStatusLabel.setText(donorStatusLabel.getText() + "Unregistered");
            receiverStatusLabel.setText(receiverStatusLabel.getText() + "Unregistered");

            if (currentProfile.getDonor() != null && currentProfile.getDonor()) {
                if (currentProfile.getOrgansDonated().size() > 0) {
                    donorStatusLabel.setText("Donor Status: Registered");
                }
            }

            if (currentProfile.getOrgansRequired().size() < 1) {
                currentProfile.setReceiver(false);
            } else {
                currentProfile.setReceiver(true);
            }

            if (currentProfile.isReceiver()) {
                receiverStatusLabel.setText("Receiver Status: Registered");
            }
            if (currentProfile.getGivenNames() != null) {
                givenNamesLabel.setText(givenNamesLabel.getText() + currentProfile.getGivenNames());
            }
            if (currentProfile.getPreferredName() != null) {
                labelPreferredName
                        .setText(labelPreferredName.getText() + currentProfile.getPreferredName());
            }
            if (currentProfile.getLastNames() != null) {
                lastNamesLabel.setText(lastNamesLabel.getText() + currentProfile.getLastNames());
            }
            if (currentProfile.getIrdNumber() != null) {
                irdLabel.setText(irdLabel.getText() + currentProfile.getIrdNumber());
            }
            if (currentProfile.getDateOfBirth() != null) {
                dobLabel.setText(dobLabel.getText() + currentProfile.getDateOfBirth()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            }
            if (currentProfile.getDateOfDeath() != null) {
                dodLabel.setText(dodLabel.getText() + currentProfile.getDateOfDeath()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            }
            if (currentProfile.getGender() != null) {
                genderLabel.setText(genderLabel.getText() + currentProfile.getGender());
            }
            if (currentProfile.getPreferredGender() != null) {
                labelGenderPreferred.setText(
                        labelGenderPreferred.getText() + currentProfile.getPreferredGender());
            }
            if (currentProfile.getHeight() != 0.0) {
                heightLabel.setText(heightLabel.getText() + currentProfile.getHeight() + "cm");
            }
            if (currentProfile.getWeight() != 0.0) {
                weightLabel.setText(weightLabel.getText() + currentProfile.getWeight() + "kg");
            }
            if (currentProfile.getPhone() != null) {
                phoneLabel.setText(phoneLabel.getText() + currentProfile.getPhone());
            }
            if (currentProfile.getEmail() != null) {
                emailLabel.setText(emailLabel.getText() + currentProfile.getEmail());
            }
            if (currentProfile.getAddress() != null) {
                addressLabel.setText(addressLabel.getText() + currentProfile.getAddress());
            }
            if (currentProfile.getRegion() != null) {
                regionLabel.setText(regionLabel.getText() + currentProfile.getRegion());
            }
            if (currentProfile.getAlcoholConsumption() != null) {
                alcoholConsumptionLabel.setText(
                        alcoholConsumptionLabel.getText() +
                                currentProfile.getAlcoholConsumption()
                );
            }
            if (currentProfile.getBloodPressure() != null) {
                bloodPressureLabel
                        .setText(bloodPressureLabel.getText() + currentProfile.getBloodPressure());
            }
            if (currentProfile.getBloodType() != null) {
                bloodTypeLabel.setText(bloodTypeLabel.getText() + currentProfile.getBloodType());
            }
            if (currentProfile.getHeight() != 0.0 && currentProfile.getWeight() != 0.0) {
                bmiLabel.setText(bmiLabel.getText() + currentProfile.calculateBMI());
            }
            if (currentProfile.getDateOfBirth() != null) {
                ageLabel.setText(
                        ageLabel.getText() + Integer.toString(currentProfile.calculateAge()));
            }
            if (currentProfile.getId() != null) {
                userIdLabel
                        .setText(userIdLabel.getText() + Integer.toString(currentProfile.getId()));
            }
            if (currentProfile.getIsSmoker() != null) {
                smokerLabel.setText(smokerLabel.getText() + currentProfile.getIsSmoker());
            }

            String history = ProfileDataIO.getHistory();
            history = history.replace(",", " ").replace("]", "").
                    replace("[", "").replace("\\u003d", "=");
            String[] histories = history.split("\"");

            String historyDisplay = "";

            for (String h : histories) {
                if (!h.equals("") && h.contains("profile " + currentProfile.getId() + " ")) {
                    historyDisplay += h + "\n";
                }
            }

            historyView.setText(historyDisplay);
            setMedicationSearchFieldListener();

            refreshConditionTable();

        } catch (Exception e) {
            e.printStackTrace();
            invalidUsername();
        }
        refreshConditionTable();
    }



    /**
     * Enables the relevant buttons on medications tab for how many drugs are selected
     */
    @FXML
    private void refreshPageElements() {
        ArrayList<Condition> allConditions = convertConditionObservableToArray(
                curConditionsTable.getSelectionModel().getSelectedItems());
        allConditions.addAll(convertConditionObservableToArray(
                pastConditionsTable.getSelectionModel().getSelectedItems()));

        hideItems();
        disableButtonsIfNoItems(allConditions);
    }



    /**
     * hides items that shouldn't be visible to either a donor or clinician
     */
    @FXML
    private void hideItems() {
        if (isOpenedByClinician) {
            //user is a clinician looking at donors profile, maximise functionality
            curConditionsTable.setEditable(true);
            pastConditionsTable.setEditable(true);
            toggleChronicButton.setDisable(false);
            toggleChronicButton.setVisible(true);
            toggleCuredButton.setDisable(false);
            toggleCuredButton.setVisible(true);
            addNewConditionButton.setVisible(true);
            deleteConditionButton.setVisible(true);
            addNewProcedureButton.setVisible(true);
            deleteProcedureButton.setVisible(true);
            buttonSaveMedications.setVisible(true);
            buttonDeleteMedication.setVisible(true);
            buttonShowDrugInteractions.setVisible(true);
            buttonViewActiveIngredients.setVisible(true);
            buttonAddMedication.setVisible(true);
            buttonMedicationCurrentToHistoric.setVisible(true);
            buttonMedicationHistoricToCurrent.setVisible(true);
            textFieldMedicationSearch.setVisible(true);
            tableViewActiveIngredients.setVisible(true);
            tableViewDrugInteractionsNames.setVisible(true);
            tableViewDrugInteractions.setVisible(true);
            buttonViewMedicationHistory.setVisible(true);

            logoutButton.setVisible(false);
        } else {
            // user is a standard profile, limit functionality
            curConditionsTable.setEditable(false);
            pastConditionsTable.setEditable(false);
            toggleChronicButton.setDisable(true);
            toggleChronicButton.setVisible(false);
            toggleCuredButton.setDisable(true);
            toggleCuredButton.setVisible(false);
            addNewConditionButton.setVisible(false);
            deleteConditionButton.setVisible(false);
            addNewProcedureButton.setVisible(false);
            deleteProcedureButton.setVisible(false);
            buttonSaveMedications.setVisible(false);
            buttonDeleteMedication.setVisible(false);
            buttonShowDrugInteractions.setVisible(false);
            buttonViewActiveIngredients.setVisible(false);
            buttonAddMedication.setVisible(false);
            buttonMedicationCurrentToHistoric.setVisible(false);
            buttonMedicationHistoricToCurrent.setVisible(false);
            textFieldMedicationSearch.setVisible(false);
            tableViewActiveIngredients.setVisible(false);
            tableViewDrugInteractionsNames.setVisible(false);
            tableViewDrugInteractions.setVisible(false);
            buttonViewMedicationHistory.setVisible(false);
        }
    }





    public Profile getCurrentProfile() {
        return currentProfile;
    }

    @FXML
    private void onTabOrgansSelected() {
        profileOrganOverviewController.currentProfile.bind(currentProfileBound);
        profileOrganOverviewController.populateOrganLists();
    }

    /**
     * Sets the current donor attributes to the labels on start up.
     */
    @FXML
    public void initialize() {

        if (currentProfile != null) {
            currentProfileBound.set(currentProfile);
            setPage(currentProfile);
        }

        curConditionsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        pastConditionsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);



        curChronicColumn.setSortable(false);

        refreshPageElements();

        disableTableHeaderReorder();
    }

    /**
     * sets the profile if it is being opened by a clinician If opened by clinician, set appropriate
     * boolean and profile
     *
     * @param profile to be used
     */
    public void setProfileViaClinician(Profile profile) {
        isOpenedByClinician = true;
        currentProfile = profile;
    }

    /**
     * sets the donor if it was logged in by a user If logged in normally, sets profile
     *
     * @param profile to be used
     */
    public void setProfile(Profile profile) {
        currentProfile = profile;
    }

}
