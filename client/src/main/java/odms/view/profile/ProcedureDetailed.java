package odms.view.profile;

import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.locations.Hospital;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.locations.HospitalDAO;
import odms.controller.profile.ProcedureEdit;
import odms.commons.model.enums.OrganEnum;
import odms.view.CommonView;

import java.time.LocalDate;
import java.util.ArrayList;


/**
 * View for the procedure details scene.
 */
@Slf4j
public class ProcedureDetailed extends CommonView {
    private ObservableList<Hospital> hospitals = FXCollections.observableArrayList();

    @FXML
    private Label procedureSummaryLabel;
    @FXML
    private Label procedureDateLabel;
    @FXML
    private Label procedureDescriptionLabel;
    @FXML
    private Label procedureOrgansLabel;
    @FXML
    private TextArea descEntry;
    @FXML
    private DatePicker dateOfProcedureDatePicker;
    @FXML
    private Button saveButton;
    @FXML
    private TextField summaryEntry;
    @FXML
    private Button editButton;
    @FXML
    private Label warningLabel;
    @FXML
    private Label hospitalLabel;
    @FXML
    private ChoiceBox<Hospital> hospitalChoiceBox;

    @FXML
    private ListView<String> affectedOrgansListView;

    private Procedure currentProcedure;
    private ProcedureEdit controller = new ProcedureEdit(this);
    private Profile profile;
    private ProceduresDisplay parent;

    /**
     * Init variables and populate text fields.
     * @param selectedProcedure procedure object that is to be displayed
     * @param currentProfile current profile being viewed
     * @param p parent view/controller, will be a instance of ProfileProceduresView
     * @param isOpenedByClinician boolean is true is scene is opened by a clinician/admin user
     */
    public void initialize(Procedure selectedProcedure, Profile currentProfile,
            ProceduresDisplay p, Boolean isOpenedByClinician) {
        parent = p;
        profile = currentProfile;
        warningLabel.setVisible(false);
        currentProcedure = selectedProcedure;
        procedureSummaryLabel.setText(currentProcedure.getSummary());
        procedureDateLabel.setText(currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText(currentProcedure.getLongDescription());

        populateAffectedOrgansLabel();

        procedureOrgansLabel.setWrapText(true);
        procedureDescriptionLabel.setWrapText(true);
        hospitalLabel.setWrapText(true);

        if (currentProcedure.getHospital() == null) {
            Hospital hospital = new Hospital("Unspecified", 0.0, 0.0, "", -1);
            currentProcedure.setHospital(hospital);
        }
        hospitalLabel.setText(currentProcedure.getHospital().getName());

        hospitalChoiceBox.setDisable(true);
        hospitalChoiceBox.setVisible(false);
        affectedOrgansListView.setDisable(true);
        affectedOrgansListView.setVisible(false);
        descEntry.setDisable(true);
        descEntry.setVisible(false);
        dateOfProcedureDatePicker.setDisable(true);
        dateOfProcedureDatePicker.setVisible(false);
        summaryEntry.setDisable(true);
        summaryEntry.setVisible(false);
        saveButton.setDisable(true);
        saveButton.setVisible(false);

        if (isOpenedByClinician) {
            affectedOrgansListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            ObservableList<String> organsDonated = FXCollections
                    .observableArrayList();
            List<String> organs = new ArrayList<>();
            currentProcedure.getOrgansAffected().forEach(organEnum -> {
                organs.add(organEnum.getNamePlain());
            });
            organsDonated.addAll(organs);
            affectedOrgansListView.setItems(organsDonated);
            editButton.setVisible(true);
        } else {
            editButton.setVisible(false);
        }
    }

    /**
     * Populates the affected organs label.
     */
    private void populateAffectedOrgansLabel() {
        procedureOrgansLabel.setText("");
        if (!currentProcedure.getOrgansAffected().isEmpty()) {
            StringBuilder affectedOrgans = new StringBuilder();

            for (OrganEnum organ : currentProcedure.getOrgansAffected()) {
                affectedOrgans.append(organ.getNamePlain());
                affectedOrgans.append(", ");
            }

            affectedOrgans.setLength(affectedOrgans.length() - 2);
            procedureOrgansLabel.setText(affectedOrgans.toString());
        }
    }

    /**
     * Button handler for edit button.
     */
    public void handleEditButtonClicked() {
        warningLabel.setVisible(false);
        affectedOrgansListView.setDisable(false);
        affectedOrgansListView.setVisible(true);
        descEntry.setDisable(false);
        descEntry.setVisible(true);
        dateOfProcedureDatePicker.setDisable(false);
        dateOfProcedureDatePicker.setVisible(true);
        saveButton.setDisable(false);
        saveButton.setVisible(true);
        summaryEntry.setDisable(false);
        summaryEntry.setVisible(true);
        descEntry.setText(currentProcedure.getLongDescription());
        dateOfProcedureDatePicker.setValue(currentProcedure.getDate());
        summaryEntry.setText(currentProcedure.getSummary());

        procedureSummaryLabel.setVisible(false);
        procedureDateLabel.setVisible(false);
        procedureDescriptionLabel.setVisible(false);
        procedureOrgansLabel.setVisible(false);
        hospitalLabel.setVisible(false);

        hospitalChoiceBox.setVisible(true);
        hospitalChoiceBox.setDisable(false);
        setHospitalDropdown();
        editButton.setVisible(false);
    }

    /**
     * Button handler for save button.
     */
    public void handleSaveButtonClicked() {
        controller.save();
        affectedOrgansListView.setDisable(true);
        affectedOrgansListView.setVisible(false);
        descEntry.setDisable(true);
        descEntry.setVisible(false);
        dateOfProcedureDatePicker.setDisable(true);
        dateOfProcedureDatePicker.setVisible(false);
        saveButton.setDisable(true);
        saveButton.setVisible(false);
        summaryEntry.setDisable(true);
        summaryEntry.setVisible(false);

        procedureSummaryLabel.setText(currentProcedure.getSummary());
        procedureDateLabel.setText(currentProcedure.getDate().toString());
        procedureDescriptionLabel.setText(currentProcedure.getLongDescription());

        populateAffectedOrgansLabel();

        parent.refreshProcedureTable();

        procedureDateLabel.setVisible(false);
        procedureDescriptionLabel.setVisible(false);
        procedureOrgansLabel.setVisible(false);
        hospitalLabel.setVisible(false);
        saveButton.setVisible(false);
    }

    public Profile getProfile() {
        return profile;
    }

    public Procedure getCurrentProcedure() {
        return currentProcedure;
    }

    public String getDescEntry() {
        return descEntry.getText();
    }

    public String getSummaryEntry() {
        return summaryEntry.getText();
    }

    public LocalDate getDateOfProcedure() {
        return dateOfProcedureDatePicker.getValue();
    }

    public ArrayList getAffectedOrgansListView() {
        return new ArrayList<>(affectedOrgansListView.getSelectionModel().getSelectedItems());
    }

    /**
     * Populates the hospital dropdown with hospitals from the database.
     */
    private void setHospitalDropdown() {
        HospitalDAO database = DAOFactory.getHospitalDAO();

        Hospital hospital = new Hospital("Unspecified", 0.0, 0.0, "", -1);
        hospitals.add(hospital);

        try {
            hospitals.addAll(database.getAll());
            hospitalChoiceBox.setItems(hospitals);

            hospitalChoiceBox.setConverter(new StringConverter<Hospital>() {
                @Override
                public String toString(Hospital object) {
                    return object.getName();
                }

                @Override
                public Hospital fromString(String string) {
                    return null;
                }
            });

            for (Hospital h : hospitals) {
                if (h.getId().equals(currentProcedure.getHospital().getId())) {
                    hospitalChoiceBox.setValue(hospitals.get(hospitals.size() - 1));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

}
