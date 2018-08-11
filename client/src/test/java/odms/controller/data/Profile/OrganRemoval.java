package odms.controller.data.Profile;

import odms.commons.model.enums.OrganEnum;
import odms.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class OrganRemoval {
    public odms.view.profile.OrganRemove view;
    public odms.controller.profile.OrganRemoval controller;
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
        currentProfile.setId(99999);
        controller = new odms.controller.profile.OrganRemoval(view);
        testOrganStrings.add("heart");
    }
    @Test
    public void testRemoveOrganRequired() {
        currentProfile.getOrgansRequired().add(OrganEnum.HEART);
        Boolean containsOrgan = currentProfile.getOrgansRequired().contains(OrganEnum.HEART);
        controller.removeOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertNotEquals(containsOrgan, currentProfile.getOrgansRequired().contains(OrganEnum.HEART));
    }

}
