package odms.controller.profile;

import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.controller.database.organ.HttpOrganDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpOrganDAO.class)
@PowerMockIgnore("javax.management.*")
public class OrganRemovalTest {
    public odms.view.profile.OrganRemove view;
    public OrganRemoval controller;
    private Profile currentProfile;
    private List<String> testOrganStrings = new ArrayList<>();

    @Before
    public void setup() {
        PowerMockito.stub(PowerMockito.method(HttpOrganDAO.class, "delete"))
                .toReturn(null);

        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        currentProfile.setId(99999);
        controller = new OrganRemoval(view);
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
