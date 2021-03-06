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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpProcedureDAO.class)
public class ProcedureGeneralTest {
    public ProceduresDisplay view;
    public odms.controller.profile.ProcedureGeneral controller;
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
        controller = new odms.controller.profile.ProcedureGeneral(view);
    }

    @Test
    public void testRemoveValidProcedure() {
        PowerMockito.stub(PowerMockito.method(HttpProcedureDAO.class, "remove"))
                .toReturn(null);
        Procedure testProcedure = new Procedure("ABC", LocalDate.now());
        testProcedure.setId(1);
        currentProfile.getAllProcedures().add(testProcedure);
        int initial_size = currentProfile.getAllProcedures().size();
        controller.removeProcedure(testProcedure);
        assertNotEquals(initial_size, currentProfile.getAllProcedures());
    }

    @Test
    public void testGetEmptyPreviousProcedures() {
        assertEquals(0, controller.getPreviousProcedures(currentProfile).size());
    }

    @Test
    public void testGetEmptyPendingProcedures() {
        assertEquals(0, controller.getPendingProcedures(currentProfile).size());
    }

    @Test
    public void testGetFullPendingProcedures() {
        Procedure testProcedure1 = new Procedure("ABC", LocalDate.parse("9999-01-01"));
        Procedure testProcedure2 = new Procedure("ABC", LocalDate.parse("1000-01-01"));
        List<Procedure> procedureList = new ArrayList<>();
        procedureList.add(testProcedure1);
        procedureList.add(testProcedure2);
        PowerMockito.stub(PowerMockito.method(HttpProcedureDAO.class, "getAll"))
                .toReturn(procedureList);
        currentProfile.getPendingProcedures().add(testProcedure2);
        currentProfile.getPendingProcedures().add(testProcedure1);
        assertEquals(controller.getPendingProcedures(currentProfile).get(0), testProcedure1);
    }

    @Test
    public void testGetFullPreviousProcedures() {
        Procedure testProcedure1 = new Procedure("ABC", LocalDate.parse("9999-01-01"));
        Procedure testProcedure2 = new Procedure("ABC", LocalDate.parse("1000-01-01"));
        List<Procedure> procedureList = new ArrayList<>();
        procedureList.add(testProcedure1);
        procedureList.add(testProcedure2);
        PowerMockito.stub(PowerMockito.method(HttpProcedureDAO.class, "getAll"))
                .toReturn(procedureList);
        currentProfile.getPreviousProcedures().add(testProcedure1);
        currentProfile.getPreviousProcedures().add(testProcedure2);
        assertEquals(controller.getPreviousProcedures(currentProfile).get(1), testProcedure2);
    }
}
