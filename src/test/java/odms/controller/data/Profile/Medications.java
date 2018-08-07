package odms.controller.data.Profile;

import odms.controller.profile.ProfileMedicationsController;
import odms.model.profile.Profile;
import odms.view.profile.ProfileMedicationsView;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class Medications {
    public ProfileMedicationsView view;
    public ProfileMedicationsController controller;
    public Profile currentProfile;

    @Before
    public void setup() throws IOException {
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        controller = new ProfileMedicationsController(view);
    }
}
