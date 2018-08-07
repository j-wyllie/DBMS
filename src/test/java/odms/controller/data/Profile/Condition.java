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

}
