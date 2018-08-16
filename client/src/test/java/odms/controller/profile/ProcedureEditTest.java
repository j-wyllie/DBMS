package odms.controller.profile;

import odms.commons.model.profile.Profile;
import org.junit.Before;

import java.util.ArrayList;

public class ProcedureEditTest {
    public odms.view.profile.ProcedureDetailed view;
    public ProcedureEdit controller;
    public Profile currentProfile;

    @Before
    public void setup() {
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        controller = new ProcedureEdit(view);
    }

}
