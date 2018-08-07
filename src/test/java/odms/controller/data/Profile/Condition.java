package odms.controller.data.Profile;

import javafx.fxml.FXMLLoader;
import odms.controller.profile.ProfileConditionController;
import odms.model.profile.Profile;
import odms.view.profile.ProfileMedicalHistoryView;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class Condition {
    public ProfileMedicalHistoryView view;
    public ProfileConditionController controller;
    public Profile currentProfile;

    @Before
    public void setup() throws IOException{
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        controller = new ProfileConditionController(view);
    }

    @Test
    public void testGetCuredConditionsEmptyList() {
        assertEquals(controller.getCuredConditions(currentProfile).size(), 0);
    }

    @Test
    public void testGetCuredConditions() {
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(), LocalDate.now(),false), currentProfile);
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(),false), currentProfile);
        assertEquals(controller.getCuredConditions(currentProfile).size(), 1);
    }

    @Test
    public void testGetCurrentConditionsEmptyList() {
        assertEquals(controller.getCurrentConditions(currentProfile).size(), 0);
    }

    @Test
    public void testGetCurrentConditions() {
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(), LocalDate.now(),false), currentProfile);
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(),false), currentProfile);
        assertEquals(controller.getCurrentConditions(currentProfile).size(), 1);
    }

    @Test
    public void testAddValidCondition() {
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(),false), currentProfile);
        assertEquals(currentProfile.getAllConditions().size(), 1);
    }

    @Test
    public void testAddValidChronicCondition() {
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(),true), currentProfile);
        assertEquals(currentProfile.getAllConditions().get(0).getChronic(), true);
    }

    @Test
    public void testAddValidCuredCondition() {
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(), LocalDate.now(),false), currentProfile);
        assertEquals(controller.getCuredConditions(currentProfile).size(), 1);
    }

    @Test
    public void testRemoveValidCondition() {
        odms.model.profile.Condition c = new odms.model.profile.Condition("", LocalDate.now(),false);
        controller.addCondition(c, currentProfile);
        int initial_size = currentProfile.getAllConditions().size();
        controller.removeCondition(c, currentProfile);
        assertNotEquals(initial_size, currentProfile.getAllConditions().size());
    }

    @Test
    public void testToggleChronicTrueCondition() {
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(),false), currentProfile);
        controller.toggleChronic(currentProfile, currentProfile.getAllConditions());
        assert(currentProfile.getAllConditions().get(0).getChronic());
    }

    @Test
    public void testToggleChronicFalseCondition() {
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(),true), currentProfile);
        controller.toggleChronic(currentProfile, currentProfile.getAllConditions());
        assertFalse(currentProfile.getAllConditions().get(0).getChronic());
    }

    @Test
    public void testToggleCuredTrueCondition() {
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(),false), currentProfile);
        controller.toggleCured(currentProfile, currentProfile.getAllConditions());
        assert(currentProfile.getAllConditions().get(0).getCured());
    }

    @Test
    public void testToggleCuredFalseCondition() {
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(),LocalDate.now(),false), currentProfile);
        controller.toggleCured(currentProfile, currentProfile.getAllConditions());
        assertFalse(currentProfile.getAllConditions().get(0).getCured());
    }

    @Test
    public void testToggleCuredChronicCondition() {
        controller.addCondition(new odms.model.profile.Condition("", LocalDate.now(),true), currentProfile);
        try {
            controller.toggleCured(currentProfile, currentProfile.getAllConditions());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),"Can not cure if Chronic");
        }
    }

}
