package odms.controller.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.view.profile.ProceduresDisplay;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


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

    @Ignore
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

    @Ignore
    @Test
    public void testGetFullPendingProcedures() {
        Procedure testProcedure1 = new Procedure("ABC", LocalDate.parse("9999-01-01"));
        Procedure testProcedure2 = new Procedure("ABC", LocalDate.parse("1000-01-01"));
        currentProfile.getPendingProcedures().add(testProcedure2);
        currentProfile.getPendingProcedures().add(testProcedure1);
        assertEquals(controller.getPendingProcedures(currentProfile).get(0), testProcedure1);
    }

    @Ignore
    @Test
    public void testGetFullPreviousProcedures() {
        Procedure testProcedure1 = new Procedure("ABC", LocalDate.parse("9999-01-01"));
        Procedure testProcedure2 = new Procedure("ABC", LocalDate.parse("1000-01-01"));
        currentProfile.getPreviousProcedures().add(testProcedure1);
        currentProfile.getPreviousProcedures().add(testProcedure2);
        assertEquals(controller.getPreviousProcedures(currentProfile).get(0), testProcedure2);
    }
}
