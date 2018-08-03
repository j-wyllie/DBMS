package odms.controller.user;


import java.util.ArrayList;
import java.util.HashSet;
import javafx.collections.ObservableList;
import odms.controller.database.DAOFactory;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.view.user.UserSearchView;

public class UserSearchController {

    private final UserSearchView view;

    private ArrayList<Profile> profileSearchResults = new ArrayList<>();

    public UserSearchController(UserSearchView v) {
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


}
