package odms.view.profile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import odms.commons.model.profile.Profile;
import odms.view.CommonView;

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
    @FXML
    private ImageView profileImage;

    private File localPath = new File(System.getProperty("user.dir"));

    private Profile currentProfile;
    // init controller corresponding to this view
    private odms.controller.profile.ProfileGeneral controller = new odms.controller.profile.ProfileGeneral(this);
    private Boolean isOpenedByClinician;

    public Label getEmailLabel() {
        return emailLabel;
    }

    public void setEmailLabel(String string) {
        this.emailLabel.setText(emailLabel.getText() + string);
    }

    public Label getPhoneLabel() {
        return phoneLabel;
    }

    public void setPhoneLabel(String string) {
        phoneLabel.setText(phoneLabel.getText() + string);
    }

    public Label getAddressLabel() {
        return addressLabel;
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

    public Label getGivenNamesLabel() {
        return givenNamesLabel;
    }

    public void setGivenNamesLabel(String string) {
        this.givenNamesLabel.setText(givenNamesLabel.getText() + string);
    }

    public Label getLastNamesLabel() {
        return lastNamesLabel;
    }

    public void setLastNamesLabel(String string) {
        this.lastNamesLabel.setText(lastNamesLabel.getText() + string);
    }

    public Label getDobLabel() {
        return dobLabel;
    }

    public void setDobLabel(String string) {
        this.dobLabel.setText(dobLabel.getText() + string);
    }

    public Label getDodLabel() {
        return dodLabel;
    }

    public void setDodLabel(String string) {
        this.dodLabel.setText(dodLabel.getText() + string);
    }

    public Label getGenderLabel() {
        return genderLabel;
    }

    public void setGenderLabel(String string) {
        this.genderLabel.setText(genderLabel.getText() + string);
    }

    public Label getHeightLabel() {
        return heightLabel;
    }

    public void setHeightLabel(String string) {
        this.heightLabel.setText(heightLabel.getText() + string);
    }

    public Label getWeightLabel() {
        return weightLabel;
    }

    public void setWeightLabel(String string) {
        this.weightLabel.setText(weightLabel.getText() + string);
    }

    public Label getLabelGenderPreferred() {
        return labelGenderPreferred;
    }

    public void setLabelGenderPreferred(String string) {
        this.labelGenderPreferred.setText(labelGenderPreferred.getText() + string);
    }

    public Label getLabelPreferredName() {
        return labelPreferredName;
    }

    public void setLabelPreferredName(String string) {
        this.labelPreferredName.setText(labelPreferredName.getText() + string);
    }

    public Label getAgeLabel() {
        return ageLabel;
    }

    public void setAgeLabel(String string) {
        this.ageLabel.setText(ageLabel.getText() + string);
    }

    public Label getNhiLabel() {
        return nhiLabel;
    }

    public void setNhiLabel(String string) {
        this.nhiLabel.setText(nhiLabel.getText() + string);
    }

    public Label getCountryLabel() {
        return countryLabel;
    }

    public void setCountryLabel(String string) {
        this.countryLabel.setText(countryLabel.getText() + string);
    }

    public Label getCityLabel() {
        return cityLabel;
    }

    public void setCityLabel(String string) {
        this.cityLabel.setText(cityLabel.getText() + string);
    }

    public ImageView getProfileImage() {
        return profileImage;
    }

    public void setProfileImage() throws MalformedURLException {
        //setting profile photo
        if (currentProfile.getPictureName() != null) {
            File image = new File(localPath + "\\" + currentProfile.getNhi() + ".png");
            if(!image.exists()){
                image = new File(localPath + "\\" + currentProfile.getNhi() + ".jpg");
                if(!image.exists()){
                    image = new File(new File("."),"src/main/resources/profile_images/default.png");
                }
            }
            profileImage.setImage(new Image(image.toURI().toURL().toString()));
        }
    }

    private void setUpDetails() {
        controller.setCurrentProfile(currentProfile);
        controller.setLabels();
    }

    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {
        handleProfileEditButtonClicked(event, currentProfile, isOpenedByClinician);
    }

    public void initialize(Profile p, Boolean isOpenedByClinician) {
        this.isOpenedByClinician = isOpenedByClinician;
        currentProfile = p;
        setUpDetails();
    }

}
