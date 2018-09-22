package odms.view.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.view.CommonView;

import java.io.IOException;

/**
 * The general profile view.
 */
public class ProfileGeneral extends CommonView {

    @FXML
    private Label phoneLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private Label givenNamesLabel;
    @FXML
    private Label lastNamesLabel;
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
    @FXML
    private Label nhiLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label emailLabel;

    private Profile currentProfile;
    // init controller corresponding to this view
    private odms.controller.profile.ProfileGeneral controller =
            new odms.controller.profile.ProfileGeneral(this);

    public void setEmailLabel(String string) {
        this.emailLabel.setText(emailLabel.getText() + string);
    }

    public void setPhoneLabel(String string) {
        phoneLabel.setText(phoneLabel.getText() + string);
    }

    public void setAddressLabel(String string) {
        this.addressLabel.setText(addressLabel.getText() + string);
    }

    public Label getRegionLabel() {
        return regionLabel;
    }

    public void setRegionLabel(String string) {
        this.regionLabel.setText(regionLabel.getText() + string);
    }

    public void setGivenNamesLabel(String string) {
        this.givenNamesLabel.setText(givenNamesLabel.getText() + string);
    }

    public void setLastNamesLabel(String string) {
        this.lastNamesLabel.setText(lastNamesLabel.getText() + string);
    }

    public void setDobLabel(String string) {
        this.dobLabel.setText(dobLabel.getText() + string);
    }

    public void setDodLabel(String string) {
        this.dodLabel.setText(dodLabel.getText() + string);
    }

    public void setGenderLabel(String string) {
        this.genderLabel.setText(genderLabel.getText() + string);
    }

    public void setHeightLabel(String string) {
        this.heightLabel.setText(heightLabel.getText() + string);
    }

    public void setWeightLabel(String string) {
        this.weightLabel.setText(weightLabel.getText() + string);
    }

    public void setLabelGenderPreferred(String string) {
        this.labelGenderPreferred.setText(labelGenderPreferred.getText() + string);
    }

    public void setLabelPreferredName(String string) {
        this.labelPreferredName.setText(labelPreferredName.getText() + string);
    }

    public void setAgeLabel(String string) {
        this.ageLabel.setText(ageLabel.getText() + string);
    }

    public void setNhiLabel(String string) {
        this.nhiLabel.setText(nhiLabel.getText() + string);
    }

    public Label getCountryLabel() {
        return countryLabel;
    }

    public Label getCityLabel() {
        return cityLabel;
    }

    private void setUpDetails() {
        controller.setCurrentProfile(currentProfile);
        controller.setLabels();
    }

    /**
     * initializes the general profile view.
     * @param p the profile being viewed.
     */
    public void initialize(Profile p) {
        currentProfile = p;
        setUpDetails();
    }
}
