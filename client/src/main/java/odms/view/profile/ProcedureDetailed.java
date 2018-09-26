package odms.view.profile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganSelectEnum;
import odms.commons.model.locations.Hospital;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.database.DAOFactory;
import odms.controller.database.locations.HospitalDAO;
import odms.controller.profile.ProcedureEdit;
import odms.view.CommonView;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * View for the procedure details scene.
 */
@Slf4j
public class ProcedureDetailed extends CommonView {
    private static final String UNSPECIFIED = "Unspecified";

    private ObservableList<Hospital> hospitals = FXCollections.observableArrayList();
    private ObservableList<String> organsDonated = FXCollections.observableArrayList();

    @FXML
    private Label procedureSummaryLabel;
    @FXML
    private Label procedureDateLabel;
    @FXML
    private Label procedureDescriptionLabel;
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
    private Button editOrgansAffectedButton;
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

        procedureDescriptionLabel.setWrapText(true);
        descEntry.setWrapText(true);
        hospitalLabel.setWrapText(true);

        if (currentProcedure.getHospital() == null) {
            Hospital hospital = new Hospital(UNSPECIFIED, 0.0, 0.0, "", -1);
            currentProcedure.setHospital(hospital);
        }
        hospitalLabel.setText(currentProcedure.getHospital().getName());

        hospitalChoiceBox.setDisable(true);
        hospitalChoiceBox.setVisible(false);
        descEntry.setDisable(true);
        descEntry.setVisible(false);
        dateOfProcedureDatePicker.setDisable(true);
        dateOfProcedureDatePicker.setVisible(false);
        summaryEntry.setDisable(true);
        summaryEntry.setVisible(false);
        summaryEntry.setAlignment(Pos.CENTER);
        saveButton.setDisable(true);
        saveButton.setVisible(false);
        editOrgansAffectedButton.setDisable(true);
        editOrgansAffectedButton.setVisible(false);

        setHospitalDropdown();

        if (isOpenedByClinician) {
            affectedOrgansListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            List<String> organs = new ArrayList<>();
            currentProcedure.getOrgansAffected().forEach(organEnum ->
                organs.add(organEnum.getNamePlain()));
            organsDonated.addAll(organs);
            affectedOrgansListView.setItems(organsDonated);
            editButton.setVisible(true);
        } else {
            editButton.setVisible(false);
        }
    }


    /**
     * Button handler for edit button.
     */
    public void handleEditButtonClicked() {
        warningLabel.setVisible(false);
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

        procedureSummaryLabel.setText("");
        procedureDateLabel.setVisible(false);
        procedureDescriptionLabel.setVisible(false);
        procedureDescriptionLabel.setText(currentProcedure.getLongDescription());

        hospitalLabel.setVisible(false);
        editOrgansAffectedButton.setDisable(false);
        editOrgansAffectedButton.setVisible(true);

        hospitalChoiceBox.setVisible(true);
        hospitalChoiceBox.setDisable(false);
        editButton.setVisible(false);
    }

    /**
     * Button handler for save button.
     */
    public void handleSaveButtonClicked() {
        try {
            controller.save();
            descEntry.setDisable(true);
            descEntry.setVisible(false);
            dateOfProcedureDatePicker.setDisable(true);
            dateOfProcedureDatePicker.setVisible(false);
            saveButton.setDisable(true);
            saveButton.setVisible(false);
            summaryEntry.setDisable(true);
            summaryEntry.setVisible(false);
            hospitalChoiceBox.setVisible(false);
            hospitalChoiceBox.setDisable(true);

            procedureDateLabel.setVisible(true);
            procedureDescriptionLabel.setVisible(true);
            hospitalLabel.setVisible(true);
            editOrgansAffectedButton.setDisable(true);
            editOrgansAffectedButton.setVisible(false);

            editButton.setVisible(true);
            editButton.setDisable(false);

            warningLabel.setVisible(false);

            procedureSummaryLabel.setText(currentProcedure.getSummary());
            procedureDateLabel.setText(currentProcedure.getDate().toString());
            procedureDescriptionLabel.setText(currentProcedure.getLongDescription());
            hospitalLabel.setText(currentProcedure.getHospital().getName());

            parent.refreshProcedureTable();
        } catch (IllegalArgumentException e) {
            warningLabel.setVisible(true);
        }
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

    public Hospital getSelectedHospital() {
        return hospitalChoiceBox.getValue();
    }

    /**
     * Populates the hospital dropdown with hospitals from the database.
     */
    private void setHospitalDropdown() {
        HospitalDAO database = DAOFactory.getHospitalDAO();
        Hospital hospital = new Hospital(UNSPECIFIED, 0.0, 0.0, "", -1);
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
                    hospitalChoiceBox.setValue(h);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Display the Organ Edit view.
     *
     * @param event the base action event
     * @throws IOException if the fxml cannot load
     */
    public void handleEditOrgansAffected(ActionEvent event)
            throws IOException {
        OrganSelectEnum selectType = OrganSelectEnum.PROCEDURE;
        Node source = (Node) event.getSource();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ProfileOrganEdit.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        OrganEdit view = fxmlLoader.getController();
        view.setWindowType(selectType);
        view.setProcedure(currentProcedure);
        view.initialize(profile);

        Stage stage = new Stage();
        stage.setTitle(selectType.toString());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(source.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.centerOnScreen();
        stage.setOnHiding(ob -> {
            organsDonated.clear();
            List<String> organs = new ArrayList<>();
            currentProcedure.getOrgansAffected().forEach(organEnum ->
                organs.add(organEnum.getNamePlain()));
            organsDonated.addAll(organs);
        });
        stage.show();
    }
}
