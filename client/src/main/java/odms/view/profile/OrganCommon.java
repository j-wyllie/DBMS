package odms.view.profile;

import java.util.Collections;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.commons.model.enums.OrganEnum;

/**
 * Organ common support functions.
 */
public class OrganCommon {
    protected ObservableList<String> observableListOrgansAvailable;

    /**
     * Populate the ListView with the organs that are available and that are not in the
     * required list.
     * @param removeStrings organ strings to remove
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
    protected void populateOrganList(
        ObservableList<String> destinationList,
        Set<OrganEnum> organs) {

        destinationList.clear();

        if (organs != null) {
            for (OrganEnum organ : organs) {
                destinationList.add(organ.getNamePlain());
            }
            Collections.sort(destinationList);
        }
    }

    /**
     * Support function to populate an observable list with organs from an organ set.
     *
     * @param destinationList list to populate
     * @param organs source list of organs to populate from
     */
    protected void populateOrganReceivingList(
        ObservableList<OrganEnum> destinationList,
        Set<OrganEnum> organs) {

        destinationList.clear();
        destinationList.addAll(organs);
        Collections.sort(destinationList);
    }
}
