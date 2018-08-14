package odms.view.profile;

import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import odms.controller.DateTimePicker;
import odms.model.profile.Profile;
import odms.view.CommonView;

/**
 * Control the organ removal view for selecting reasoning behind removing an organ from a profile.
 */
public class OrganRemove extends CommonView {
    @FXML
    private Label dynamicLabel;

    @FXML
    private Label organLabel;

    @FXML
    private ComboBox<String> reasonSelector;

    @FXML
    private GridPane windowGrid;

    private CheckBox curedCheck = new CheckBox();

    private DateTimePicker dodPicker = new DateTimePicker();

    private Profile currentProfile;

    private OrganEdit organEditView;

    private String currentOrgan;

    private odms.controller.profile.OrganRemoval controller = new odms.controller.profile.OrganRemoval(this);

    /**
     * Confirms the changes made to the organs required and stores the reason given for this
     * change.
     *
     * @param event the ActionEvent
     */
    @FXML
    private void handleConfirmButtonAction(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        controller.confirm(getCurrentProfile());
        appStage.close();
    }

    /**
     * Removes the selected organ from the observable list of required organs displayed.
     */
    public void removeOrgan() {
        organEditView.observableListOrgansSelected.remove(currentOrgan);
        organEditView.observableListOrgansAvailable.add(currentOrgan);
    }

    /**
     * Removes all organs from the observable list of required organs displayed.
     */
    public void removeAllOrgans() {
        organEditView.observableListOrgansAvailable.addAll(
                organEditView.observableListOrgansSelected
        );
        organEditView.observableListOrgansSelected.clear();
    }

    /**
     * Closes the window when the cancel button is clicked.
     *
     * @param event the
     */
    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.close();
    }

    /**
     * Dynamically displays a date picker or checkbox based on the reason selected by the user.
     */
    @FXML
    private void handleReasonSelectionAction() {
        String reason = reasonSelector.getSelectionModel().getSelectedItem();

        switch (reason) {

            case "No longer required":
                dynamicLabel.setText("Cured : ");
                // Create cured checkbox.
                dodPicker.setVisible(false);
                curedCheck.setVisible(true);
                break;

            case "Patient deceased":
                dynamicLabel.setText("Date of death : ");
                // Create date picker for dod.
                dodPicker.setVisible(true);
                curedCheck.setVisible(false);
                break;

            default:
                dynamicLabel.setText("");
                dodPicker.setVisible(false);
                curedCheck.setVisible(false);
                break;
        }
    }

    /**
     * Sets the current profile and organ to be processed on start up. Also configures components of
     * the window.
     *
     * @param organ the organ being removed.
     * @param profile the profile the organ is being removed from.
     * @param view the source view.
     */
    public void initialize(String organ, Profile profile, OrganEdit view) {
        currentProfile = profile;
        organEditView = view;
        currentOrgan = organ;
        organLabel.setText(organLabel.getText() + organ);
        reasonSelector.getItems().addAll(
                "Error",
                "No longer required",
                "Patient deceased"
        );
        reasonSelector.setValue(reasonSelector.getItems().get(0));
        GridPane.setMargin(dodPicker, new Insets(0, 40, 0, 0));
        windowGrid.add(dodPicker, 2, 2, 2, 1);
        dodPicker.setVisible(false);
        GridPane.setMargin(curedCheck, new Insets(5, 0, 0, 0));
        windowGrid.add(curedCheck, 2, 2, 2, 1);
        curedCheck.setVisible(false);
        dodPicker.setDateTimeValue(LocalDateTime.now());
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public LocalDateTime getDOD() {
        return dodPicker.getDateTimeValue();
    }

    public String getSelection() {
        return reasonSelector.getSelectionModel().getSelectedItem();
    }
}
