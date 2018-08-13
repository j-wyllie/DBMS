package odms.controller.data.Profile;


import odms.commons.model.profile.Profile;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;

public class ProcedureEdit {
    public odms.view.profile.ProcedureDetailed view;
    public odms.controller.profile.ProcedureEdit controller;
    public Profile currentProfile;

    @Before
    public void setup() throws IOException{
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        controller = new odms.controller.profile.ProcedureEdit(view);
    }

}
