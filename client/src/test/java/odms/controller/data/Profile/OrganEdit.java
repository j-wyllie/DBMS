package odms.controller.data.Profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        currentProfile.setId(99999);
        controller = new odms.controller.profile.OrganEdit(view);
        testOrganStrings.add("heart");
    }

    @Test
    public void testValidAddOrganDonating() throws OrganConflictException {

        controller.addOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansDonating().size(), 1);
    }
    @Test
    public void testValidAddOrganDonated() {
        controller.addOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansDonated().size(), 1);
    }
    @Test
    public void testValidAddOrganRequired() {
        controller.addOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansRequired().size(), 1);
    }
    @Test
    public void testMultipleAddOrganDonating() throws OrganConflictException {
        testOrganStrings.add("lung");
        controller.addOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansDonating().size(), 2);
    }
    @Test
    public void testMultipleAddOrganDonated() {
        testOrganStrings.add("lung");
        controller.addOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansDonated().size(), 2);
    }
    @Test
    public void testMultipleAddOrganRequired() {
        testOrganStrings.add("lung");
        controller.addOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansRequired().size(), 2);
    }
    @Test
    public void testRemoveOrganDonating() throws OrganConflictException{
        controller.addOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        Boolean containsOrgan = currentProfile.getOrgansDonating().contains(OrganEnum.HEART);
        controller.removeOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertNotEquals(containsOrgan, currentProfile.getOrgansDonating().contains(OrganEnum.HEART));
    }
    @Test
    public void testRemoveOrganDonated() throws OrganConflictException{
        controller.addOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        Boolean containsOrgan = currentProfile.getOrgansDonated().contains(OrganEnum.HEART);
        controller.removeOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertNotEquals(containsOrgan, currentProfile.getOrgansDonated().contains(OrganEnum.HEART));
    }
    @Test
    public void testRemoveOrganRequired() throws OrganConflictException{
        controller.addOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        Boolean containsOrgan = currentProfile.getOrgansRequired().contains(OrganEnum.HEART);
        controller.removeOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertNotEquals(containsOrgan, currentProfile.getOrgansRequired().contains(OrganEnum.HEART));
    }




}
