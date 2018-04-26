package odms.controller;

import java.util.ArrayList;
import java.util.EnumSet;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import odms.profile.Organ;
import odms.profile.Profile;

public class OrganRequiredController {
    private Profile profile;

    private ArrayList<String> organsAvailable;
    private ArrayList<String> organsRequired;

    private ListProperty<String> listOrgansAvailableProperty = new SimpleListProperty<>();
    private ListProperty<String> listOrgansRequiredProperty = new SimpleListProperty<>();

    @FXML
    private ListView listOrgansAvailable;

    @FXML
    private ListView listOrgansRequired;

    @FXML
    private Button btnOrganSwitch;

    private void buildOrgansAvailable() {
        ArrayList<Organ> organs = new ArrayList<>(EnumSet.allOf(Organ.class));

        for (Organ organ : organs) {
            organsAvailable.add(organ.getName());
        }
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @FXML
    public void initialize() {
        buildOrgansAvailable();

        listOrgansAvailable.itemsProperty().bind(listOrgansAvailableProperty);
    }

}
