package odms.controller.data.Profile;

import odms.controller.profile.ProfileEditController;
import odms.model.profile.Profile;
import odms.view.profile.ProfileEditView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class Edit {

    public ProfileEditView view;
    public ProfileEditController controller;
    public Profile currentProfile;

    @Before
    public void setup() {
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("ird=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        view = new ProfileEditView();
        view.initialize(currentProfile);
        controller = new ProfileEditController(view);
    }

    @Test
    public void testSaveDateOfBirthNullValue() {
        try {
            controller.saveDateOfBirth();
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveDateOfBirthCorrectValue() {
        try {
            LocalDate oldValue = currentProfile.getDateOfBirth();
            view.setdobDatePicker(LocalDate.now());
            controller.saveDateOfBirth();
            assertNotEquals(oldValue, currentProfile.getDateOfBirth());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

}
