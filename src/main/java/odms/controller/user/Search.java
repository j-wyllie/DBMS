package odms.controller.user;


import java.util.ArrayList;
import java.util.HashSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import odms.controller.database.DAOFactory;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;

public class Search {

    private final odms.view.user.Search view;

    private ArrayList<Profile> profileSearchResults = new ArrayList<>();

    public Search(odms.view.user.Search v) {
        view = v;
    }

    public ArrayList<Profile> performSearch(
            ObservableList<OrganEnum> selectedOrgans, String selectedType,
            String selectedGender, String searchString, String regionSearchString,
            String ageSearchString, String ageRangeString, boolean selected) {

        int ageRangeSearchInt;
        int ageSearchInt;

        if(!ageSearchString.equals("")) {
            ageSearchInt = Integer.parseInt(ageSearchString);
        } else {
            ageSearchInt = -999;
        }

        if (selected) {
            if (!ageRangeString.equals("")) {
                ageRangeSearchInt = Integer.parseInt(ageRangeString);
            } else {
                ageRangeSearchInt = -999;
            }
        } else {
            ageRangeSearchInt = -999;
        }


        profileSearchResults.clear();

        try {
             profileSearchResults.addAll(DAOFactory.getProfileDao().search(
                    searchString,
                    ageSearchInt,
                    ageRangeSearchInt,
                    regionSearchString,
                    selectedGender,
                    selectedType,
                    new HashSet<>(selectedOrgans)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return profileSearchResults;
    }

    /**
     * Returns the string value to populate the Donor/Receiver column in the clinician search
     * table.
     *
     * @return a string depicting whether to profile is a donor, receiver, or both.
     */
    public SimpleStringProperty donorReceiverProperty(Profile profile) {
        SimpleStringProperty result = new SimpleStringProperty();
        if ((profile.getDonor() != null) && profile.getDonor()) {
            if ((profile.getReceiver() != null) && profile.getReceiver()) {
                result.setValue("Donor/Receiver");
            } else {
                result.setValue("Donor");
            }
        } else if ((profile.getReceiver() != null) && profile.getReceiver()) {
            result.setValue("Receiver");

        }
        return result;
    }

}
