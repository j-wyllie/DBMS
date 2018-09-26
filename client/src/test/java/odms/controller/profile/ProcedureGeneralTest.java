package odms.controller.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.database.procedure.HttpProcedureDAO;
import odms.view.profile.ProceduresDisplay;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpProcedureDAO.class)
@PowerMockIgnore("javax.management.*")
public class ProcedureGeneralTest {
    public ProceduresDisplay view;
    public odms.controller.profile.ProcedureGeneral controller;
    public Profile currentProfile;

    @Before
    public void setup() {
        PowerMockito.stub(PowerMockito.method(HttpProcedureDAO.class, "remove"))
                .toReturn(null);
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        currentProfile.setId(99999);
        controller = new odms.controller.profile.ProcedureGeneral(view);
    }

    @Test
    public void testRemoveValidProcedure() {
        Procedure testProcedure = new Procedure("ABC", LocalDate.now());
        testProcedure.setId(1);
        currentProfile.getAllProcedures().add(testProcedure);
        int initial_size = currentProfile.getAllProcedures().size();
        controller.removeProcedure(testProcedure, currentProfile);
        assertNotEquals(initial_size, currentProfile.getAllProcedures());
    }

    @Test
    public void testGetEmptyPreviousProcedures() {
        assertEquals(controller.getPreviousProcedures(currentProfile).size(), 0);
    }

    @Test
    public void testGetEmptyPendingProcedures() {
        assertEquals(controller.getPendingProcedures(currentProfile).size(), 0);
    }

    @Test
    public void testGetFullPendingProcedures() {
        List<Procedure> list = new ArrayList<>();
        list.add(new Procedure("ABC", LocalDate.parse("9999-01-01")));
        PowerMockito.stub(PowerMockito.method(HttpProcedureDAO.class, "getAll"))
                .toReturn(list);

        Procedure testProcedure1 = new Procedure("ABC", LocalDate.parse("9999-01-01"));
        Procedure testProcedure2 = new Procedure("ABC", LocalDate.parse("1000-01-01"));
        currentProfile.getPendingProcedures().add(testProcedure2);
        currentProfile.getPendingProcedures().add(testProcedure1);
        assertEquals(controller.getPendingProcedures(currentProfile).get(0).getDate(), testProcedure1.getDate());
    }

    @Test
    public void testGetFullPreviousProcedures() {
        List<Procedure> list = new ArrayList<>();
        list.add(new Procedure("ABC", LocalDate.parse("1000-01-01")));
        PowerMockito.stub(PowerMockito.method(HttpProcedureDAO.class, "getAll"))
                .toReturn(list);
        Procedure testProcedure1 = new Procedure("ABC", LocalDate.parse("9999-01-01"));
        Procedure testProcedure2 = new Procedure("ABC", LocalDate.parse("1000-01-01"));
        currentProfile.getPreviousProcedures().add(testProcedure1);
        currentProfile.getPreviousProcedures().add(testProcedure2);
        assertEquals(controller.getPreviousProcedures(currentProfile).get(0).getDate(), testProcedure2.getDate());
    }
}
