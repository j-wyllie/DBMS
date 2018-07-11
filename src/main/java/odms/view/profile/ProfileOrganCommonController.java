package odms.view.profile;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;

import java.util.Collections;
import java.util.Set;

public class ProfileOrganCommonController {

    protected ObjectProperty<Profile> currentProfile = new SimpleObjectProperty<>();

    protected ObservableList<String> observableListOrgansAvailable;

    /**
     * Populate the ListView with the organs that are available and that are not in the required
     * list.
     */
    protected void buildOrgansAvailable(ObservableList<String> removeStrings) {
        observableListOrgansAvailable = FXCollections.observableArrayList();
        observableListOrgansAvailable.addAll(OrganEnum.toArrayList());
        observableListOrgansAvailable.removeIf(removeStrings::contains);
    }

    /**
     * Support function to populate an observable list with organs from an organ set.
     *
     * @param destinationList list to populate
     * @param organs          source list of organs to populate from
     */
    protected void populateOrganList(ObservableList<String> destinationList,
            Set<OrganEnum> organs) {
        destinationList.clear();

        if (organs != null) {
            for (OrganEnum organ : organs) {
                destinationList.add(organ.getNamePlain());
            }
            Collections.sort(destinationList);
        }
    }

    public ObjectProperty<Profile> getCurrentProfile() {
        return currentProfile;
    }

}
