package odms.controller.profile;

import static org.junit.Assert.assertEquals;

import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.database.organ.HttpOrganDAO;
import odms.controller.database.procedure.HttpProcedureDAO;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpProcedureDAO.class)
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
        currentProfile.setId(99999);
        controller = new odms.controller.profile.ProcedureAdd();
    }

    @Test
    public void testAddValidProcedure() {
        PowerMockito.stub(PowerMockito.method(HttpProcedureDAO.class, "add"))
                .toReturn(true);
        Procedure testProcedure = new Procedure("ABC", LocalDate.now());
        List<String> organs = new ArrayList<>();
        controller.add(currentProfile,organs,testProcedure);
        assertEquals(currentProfile.getAllProcedures().get(0), testProcedure);
    }

    @Test
    public void testParseProcedure() {
        Procedure procedure = controller.parseProcedure("abc", LocalDate.now(), "", LocalDate.now());
        assertEquals("abc", procedure.getSummary());
    }

}
