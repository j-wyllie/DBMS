package odms.controller.profile;

import static org.junit.Assert.assertEquals;

import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
@Ignore
public class ProcedureAddTest {
    public odms.view.profile.ProcedureAdd view;
    public odms.controller.profile.ProcedureAdd controller;
    public Profile currentProfile;

    @Before
    public void setup() {
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        controller = new odms.controller.profile.ProcedureAdd(view);
    }

    @Test
    public void testAddValidProcedure() {
        Procedure testProcedure = new Procedure("ABC", LocalDate.now());
        controller.addProcedure(testProcedure, currentProfile);
        assertEquals(currentProfile.getAllProcedures().get(0), testProcedure);
    }

}
