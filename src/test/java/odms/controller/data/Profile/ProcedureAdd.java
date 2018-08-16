package odms.controller.data.Profile;

import odms.model.profile.Procedure;
import odms.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class ProcedureAdd {
    public odms.view.profile.ProcedureAdd view;
    public odms.controller.profile.ProcedureAdd controller;
    public Profile currentProfile;

    @Before
    public void setup() throws IOException{
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        currentProfile.setId(99999);
        controller = new odms.controller.profile.ProcedureAdd(view);
    }

    @Test
    public void testAddValidProcedure() {
        Procedure testProcedure = new Procedure("ABC", LocalDate.now());
        controller.addProcedure(testProcedure, currentProfile);
        assertEquals(currentProfile.getAllProcedures().get(0), testProcedure);
    }

}
