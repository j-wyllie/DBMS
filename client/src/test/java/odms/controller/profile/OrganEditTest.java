package odms.controller.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.controller.database.organ.HttpOrganDAO;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpOrganDAO.class)
public class OrganEditTest {
    public odms.view.profile.OrganEdit view;
    public odms.controller.profile.OrganEdit controller;
    public Profile currentProfile;
    public List<String> testOrganStrings = new ArrayList<>();

    @Before
    public void setup() {
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        currentProfile.setId(99999);
        controller = new odms.controller.profile.OrganEdit();
        testOrganStrings.add("heart");
    }

    @Test
    public void testValidAddOrganDonating() throws OrganConflictException {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getDonating"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getReceived"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "addDonating"))
                .toReturn(null);
        controller.addOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(1, currentProfile.getOrgansDonating().size());
    }

    @Test
    public void testValidAddOrganDonated() {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getDonations"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "addDonation"))
                .toReturn(null);
        controller.addOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(1, currentProfile.getOrgansDonated().size());
    }

    @Test
    public void testValidAddOrganRequired() {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getRequired"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "addRequired"))
                .toReturn(null);
        controller.addOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(1, currentProfile.getOrgansRequired().size());
    }

    @Test
    public void testMultipleAddOrganDonating() throws OrganConflictException {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getDonating"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getReceived"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "addDonating"))
                .toReturn(null);
        testOrganStrings.add("lung");
        controller.addOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(2, currentProfile.getOrgansDonating().size());
    }

    @Test
    public void testMultipleAddOrganDonated() {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getDonations"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "addDonation"))
                .toReturn(null);
        testOrganStrings.add("lung");
        controller.addOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(2, currentProfile.getOrgansDonated().size());
    }

    @Test
    public void testMultipleAddOrganRequired() {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getRequired"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "addRequired"))
                .toReturn(null);
        testOrganStrings.add("lung");
        controller.addOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(2, currentProfile.getOrgansRequired().size());
    }

    @Test
    public void testRemoveOrganDonating() throws OrganConflictException{
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getDonating"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getReceived"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "removeDonating"))
                .toReturn(null);
        controller.addOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        Boolean containsOrgan = currentProfile.getOrgansDonating().contains(OrganEnum.HEART);
        controller.removeOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertNotEquals(containsOrgan, currentProfile.getOrgansDonating().contains(OrganEnum.HEART));
    }

    @Test
    public void testRemoveOrganDonated() {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getDonations"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "removeDonation"))
                .toReturn(null);
        controller.addOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        Boolean containsOrgan = currentProfile.getOrgansDonated().contains(OrganEnum.HEART);
        controller.removeOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertNotEquals(containsOrgan, currentProfile.getOrgansDonated().contains(OrganEnum.HEART));
    }

    @Test
    public void testRemoveOrganRequired() {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getRequired"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "removeRequired"))
                .toReturn(null);
        controller.addOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        Boolean containsOrgan = currentProfile.getOrgansRequired().contains(OrganEnum.HEART);
        controller.removeOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertNotEquals(containsOrgan, currentProfile.getOrgansRequired().contains(OrganEnum.HEART));
    }

}
