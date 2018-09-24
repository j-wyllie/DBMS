package odms.controller.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.ExpiredOrgan;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.controller.database.organ.HttpOrganDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test class for OrganDisplay controller.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpOrganDAO.class)
public class OrganDisplayTest {

    private Profile currentProfile;
    private OrganDisplay controller;

    @Before
    public void setUp() {
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        controller = new OrganDisplay(new odms.view.profile.OrganDisplay());
        currentProfile.setId(99999);
    }

    @Test
    public void getDonatingOrgans() throws OrganConflictException {
        // Mock makeRequests method, returns ArrayList of Expired Organ objects
        List<ExpiredOrgan> expired = new ArrayList<>();
        expired.add(new ExpiredOrgan(OrganEnum.BONE, "", "", null));
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "getExpired"))
                .toReturn(expired);

        // Add donating organs to profile
        currentProfile.addOrganDonating(OrganEnum.LIVER);
        currentProfile.addOrganDonating(OrganEnum.BONE);
        Set<OrganEnum> organs = controller.getDonatingOrgans(currentProfile);

        // Bone should not be in the resulting set since it has been manually
        // expired (provided by mocked method).
        assertTrue(organs.contains(OrganEnum.LIVER));
        assertEquals(1, organs.size());
    }
}
