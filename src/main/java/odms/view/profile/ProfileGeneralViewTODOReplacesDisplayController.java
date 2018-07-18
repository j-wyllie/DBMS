package odms.view.profile;

import java.time.format.DateTimeFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import odms.model.profile.Profile;
import odms.view.CommonView;

public class ProfileGeneralViewTODOReplacesDisplayController  extends CommonView {

    @FXML
    private Label phoneLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label regionLabel;
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
    private Label labelGenderPreferred;
    @FXML
    private Label labelPreferredName;
    @FXML
    private Label ageLabel;

    public ObjectProperty<Profile> currentProfile = new SimpleObjectProperty<>();


    private void setUpDetails() {
        if (currentProfile.get().getGivenNames() != null) {
            givenNamesLabel.setText(givenNamesLabel.getText() + currentProfile.get().getGivenNames());
        }
        if (currentProfile.get().getPreferredName() != null) {
            labelPreferredName
                    .setText(labelPreferredName.getText() + currentProfile.get().getPreferredName());
        }
        if (currentProfile.get().getLastNames() != null) {
            lastNamesLabel.setText(lastNamesLabel.getText() + currentProfile.get().getLastNames());
        }
        if (currentProfile.get().getIrdNumber() != null) {
            irdLabel.setText(irdLabel.getText() + currentProfile.get().getIrdNumber());
        }
        if (currentProfile.get().getDateOfBirth() != null) {
            dobLabel.setText(dobLabel.getText() + currentProfile.get().getDateOfBirth()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if (currentProfile.get().getDateOfDeath() != null) {
            dodLabel.setText(dodLabel.getText() + currentProfile.get().getDateOfDeath()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        if (currentProfile.get().getGender() != null) {
            genderLabel.setText(genderLabel.getText() + currentProfile.get().getGender());
        }
        if (currentProfile.get().getPreferredGender() != null) {
            labelGenderPreferred.setText(
                    labelGenderPreferred.getText() + currentProfile.get().getPreferredGender());
        }
        if (currentProfile.get().getHeight() != 0.0) {
            heightLabel.setText(heightLabel.getText() + currentProfile.get().getHeight() + "cm");
        }
        if (currentProfile.get().getWeight() != 0.0) {
            weightLabel.setText(weightLabel.getText() + currentProfile.get().getWeight() + "kg");
        }
        if (currentProfile.get().getPhone() != null) {
            phoneLabel.setText(phoneLabel.getText() + currentProfile.get().getPhone());
        }
        if (currentProfile.get().getEmail() != null) {
            emailLabel.setText(emailLabel.getText() + currentProfile.get().getEmail());
        }
        if (currentProfile.get().getAddress() != null) {
            addressLabel.setText(addressLabel.getText() + currentProfile.get().getAddress());
        }
        if (currentProfile.get().getRegion() != null) {
            regionLabel.setText(regionLabel.getText() + currentProfile.get().getRegion());
        }
        if (currentProfile.get().getDateOfBirth() != null) {
            ageLabel.setText(
                    ageLabel.getText() + Integer.toString(currentProfile.get().getAge()));
        }
    }

    //todo do we need this function?????
    public void initialize() {
        if (currentProfile != null) {
            setUpDetails();
        }
    }
}
