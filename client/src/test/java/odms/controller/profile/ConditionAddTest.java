package odms.controller.profile;

import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConditionAddTest {
    public ConditionAdd controller;
    public Profile currentProfile;

    @Before
    public void setup() {
        List<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        currentProfile.setId(9999);
        controller = new ConditionAdd();
    }

    @Test
    public void testAddCondition() {
        Condition condition = new Condition("test", LocalDate.now(), false);
        controller.add(currentProfile, condition);
        assertEquals(currentProfile.getAllConditions().size(), 1);
    }

    @Test
    public void testGetCurrentConditions() {
        Condition condition1 = new Condition("test", LocalDate.now(), false);
        Condition condition2 = new Condition("test", LocalDate.now(), LocalDate.now(), false);
        controller.add(currentProfile, condition1);
        controller.add(currentProfile, condition2);
        assertEquals(controller.getCurrentConditions(currentProfile).size(), 1);
    }

    @Test
    public void testParseConditionNotCured(){
        Condition condition1 = new Condition("test", LocalDate.now(), false);
        Condition condition2 = controller.parseCondition("test", LocalDate.now(), false, false, currentProfile, LocalDate.now());
        assertEquals(condition1.getName(), condition2.getName());
    }

    @Test
    public void testParseConditionCured() {
        Condition condition = controller.parseCondition("test", LocalDate.now(), false, true, currentProfile, LocalDate.now());
        controller.add(currentProfile, condition);
        assertEquals(currentProfile.getCuredConditions().size(), 1);
    }

    @Test
    public void testParseConditionNullName() {
        try {
            Condition condition = controller.parseCondition(null, LocalDate.now(), false, true, currentProfile, LocalDate.now());
            fail();
        } catch (IllegalArgumentException e) {
            assert(true);
        }
    }

}
