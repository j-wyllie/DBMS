package odms.controller;

import java.util.Collections;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.enums.OrganEnum;
import odms.profile.Profile;

public class ProfileOrganCommonController {
    protected Profile profile;

    protected ObservableList<String> observableListOrgansAvailable;

    /**
     * Populate the ListView with the organs that are available and that are not in the
     * required list.
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
     * @param organs source list of organs to populate from
     */
    protected void populateOrganList(ObservableList<String> destinationList, Set<OrganEnum> organs) {
        if (organs != null) {
            for (OrganEnum organ : organs) {
                destinationList.add(organ.getNamePlain());
            }
            Collections.sort(destinationList);
        }
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

}
