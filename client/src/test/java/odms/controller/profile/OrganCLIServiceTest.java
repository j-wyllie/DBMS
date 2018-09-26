package odms.controller.profile;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.controller.database.organ.HttpOrganDAO;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class OrganCLIServiceTest {
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
        OrganCLIService.addOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansDonating().size(), 1);
    }

    @Test
    public void testValidAddOrganDonated() {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getDonations"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "addDonation"))
                .toReturn(null);
        OrganCLIService.addOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansDonated().size(), 1);
    }

    @Test
    public void testValidAddOrganRequired() {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getRequired"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "addRequired"))
                .toReturn(null);
        OrganCLIService.addOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansRequired().size(), 1);
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
        OrganCLIService.addOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansDonating().size(), 2);
    }

    @Test
    public void testMultipleAddOrganDonated() {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getDonations"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "addDonation"))
                .toReturn(null);
        testOrganStrings.add("lung");
        OrganCLIService.addOrgansDonated(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansDonated().size(), 2);
    }

    @Test
    public void testMultipleAddOrganRequired() {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getRequired"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "addRequired"))
                .toReturn(null);
        testOrganStrings.add("lung");
        OrganCLIService.addOrgansRequired(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertEquals(currentProfile.getOrgansRequired().size(), 2);
    }

    @Test
    public void testRemoveOrganDonating() throws OrganConflictException {
        Set<OrganEnum> set = new HashSet<>();
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getDonating"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getReceived"))
                .toReturn(set);
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "removeDonating"))
                .toReturn(null);
        OrganCLIService.addOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        Boolean containsOrgan = currentProfile.getOrgansDonating().contains(OrganEnum.HEART);
        OrganCLIService.removeOrgansDonating(OrganEnum.stringListToOrganSet(testOrganStrings), currentProfile);
        assertNotEquals(containsOrgan, currentProfile.getOrgansDonating().contains(OrganEnum.HEART));
    }
}
