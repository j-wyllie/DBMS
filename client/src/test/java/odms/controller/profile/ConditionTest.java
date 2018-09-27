package odms.controller.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.controller.database.condition.HttpConditionDAO;
import odms.view.profile.ProfileMedicalHistory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpConditionDAO.class)
public class ConditionTest {
    public ProfileMedicalHistory view;
    public ConditionGeneral controller;
    public Profile currentProfile;

    @Before
    public void setup() {
        PowerMockito.stub(PowerMockito.method(HttpConditionDAO.class, "remove"))
                .toReturn(null);

        PowerMockito.stub(PowerMockito.method(HttpConditionDAO.class, "add"))
                .toReturn(null);

        PowerMockito.stub(PowerMockito.method(HttpConditionDAO.class, "update"))
                .toReturn(null);
        List<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        view = new ProfileMedicalHistory();
        currentProfile = new Profile(profileOneAttr);
        controller = new ConditionGeneral();
    }

    @Test
    public void testGetCuredConditionsEmptyList() {
        Assert.assertEquals(0, controller.getCuredConditions(currentProfile).size());
    }

    @Test
    public void testGetCuredConditions() {
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(), LocalDate.now(),false), currentProfile);
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(),false), currentProfile);
        Assert.assertEquals(1, controller.getCuredConditions(currentProfile).size());
    }

    @Test
    public void testGetCurrentConditionsEmptyList() {
        Assert.assertEquals(0, controller.getCurrentConditions(currentProfile).size());
    }

    @Test
    public void testGetCurrentConditions() {
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(), LocalDate.now(),false), currentProfile);
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(),false), currentProfile);
        Assert.assertEquals(1, controller.getCurrentConditions(currentProfile).size());
    }

    @Test
    public void testAddValidCondition() {
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(),false), currentProfile);
        assertEquals(1, currentProfile.getAllConditions().size());
    }

    @Test
    public void testAddValidChronicCondition() {
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(),true), currentProfile);
        assertEquals(true, currentProfile.getAllConditions().get(0).getChronic());
    }

    @Test
    public void testAddValidCuredCondition() {
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(), LocalDate.now(),false), currentProfile);
        Assert.assertEquals(1, controller.getCuredConditions(currentProfile).size());
    }

    @Test
    public void testRemoveValidCondition() {
        odms.commons.model.profile.Condition c = new odms.commons.model.profile.Condition("", LocalDate.now(),false);
        controller.addCondition(c, currentProfile);
        int initial_size = currentProfile.getAllConditions().size();
        controller.removeCondition(c, currentProfile);
        assertNotEquals(initial_size, currentProfile.getAllConditions().size());
    }

    @Test
    public void testToggleChronicTrueCondition() {
        odms.commons.model.profile.Condition c = new odms.commons.model.profile.Condition("", LocalDate.now(),false);
        controller.addCondition(c, currentProfile);
        controller.toggleChronic(currentProfile, currentProfile.getAllConditions());
        assert(currentProfile.getAllConditions().get(0).getChronic());
    }

    @Test
    public void testToggleChronicFalseCondition() {
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(),true), currentProfile);
        controller.toggleChronic(currentProfile, currentProfile.getAllConditions());
        assertFalse(currentProfile.getAllConditions().get(0).getChronic());
    }

    @Test
    public void testToggleCuredTrueCondition() {
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(),false), currentProfile);
        controller.toggleCured(currentProfile, currentProfile.getAllConditions());
        assert(currentProfile.getAllConditions().get(0).getCured());
    }

    @Test
    public void testToggleCuredFalseCondition() {
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(),LocalDate.now(),false), currentProfile);
        controller.toggleCured(currentProfile, currentProfile.getAllConditions());
        assertFalse(currentProfile.getAllConditions().get(0).getCured());
    }

    @Test
    public void testToggleCuredChronicCondition() {
        controller.addCondition(new odms.commons.model.profile.Condition("", LocalDate.now(),true), currentProfile);
        try {
            controller.toggleCured(currentProfile, currentProfile.getAllConditions());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Can not cure if Chronic", e.getMessage());
        }
    }

    @Test
    public void testDeleteConditions() throws IOException{
        ArrayList<Condition> conditionList = new ArrayList();
        Condition condition1 = new odms.commons.model.profile.Condition("", LocalDate.now(), LocalDate.now(), true);
        Condition condition2 = new odms.commons.model.profile.Condition("", LocalDate.now(), LocalDate.now(),true);
        Condition condition3 = new odms.commons.model.profile.Condition("", LocalDate.now(), LocalDate.now(),true);
        conditionList.add(condition1);
        conditionList.add(condition2);
        conditionList.add(condition3);
        controller.addCondition(condition1, currentProfile);
        controller.addCondition(condition2, currentProfile);
        controller.addCondition(condition3, currentProfile);
        controller.delete(currentProfile, conditionList);
        assertEquals(0, currentProfile.getAllConditions().size());
    }

}
