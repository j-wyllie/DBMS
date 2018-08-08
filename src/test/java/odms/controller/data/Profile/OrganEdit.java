package odms.controller.data.Profile;

import odms.model.enums.OrganEnum;
import odms.model.profile.OrganConflictException;
import odms.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class OrganEdit {
    public odms.view.profile.OrganEdit view;
    public odms.controller.profile.OrganEdit controller;
    public Profile currentProfile;
    public List<String> testOrganStrings = new ArrayList<>();

    @Before
    public void setup() throws IOException{
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        controller = new odms.controller.profile.OrganEdit(view);
    }

    @Before
    @Test
    public void testValidAddOrganDonating() throws OrganConflictException {
        testOrganStrings.add("heart");
        controller.addOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansDonating().size(), 0);
    }
    @Test
    public void testValidAddOrganDonated() {
        testOrganStrings.add("HEART");
        controller.addOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansDonated().size(), 0);
    }



}
