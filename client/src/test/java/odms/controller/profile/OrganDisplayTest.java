package odms.controller.profile;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.ExpiredOrgan;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.controller.database.organ.HttpOrganDAO;
import odms.controller.database.organ.OrganDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.configuration.ConfigurationType.PowerMock;

@RunWith(PowerMockRunner.class);
@PrepareForTest(OrganDAO.class);
public class OrganDisplayTest {

    private odms.view.profile.OrganDisplay view;
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
        currentProfile.setId(99999);
    }

    @Test
    public void getDonatingOrgans() throws OrganConflictException, SQLException {
        PowerMockito.

        currentProfile.addOrganDonating(OrganEnum.LIVER);
        currentProfile.addOrganDonating(OrganEnum.BONE);
        Set<OrganEnum> organs = controller.getDonatingOrgans(currentProfile);
        for (OrganEnum organ : organs) {
            System.out.println(organ.getNamePlain());
        }
        assertTrue(organs.contains(OrganEnum.LIVER));
        assertTrue(organs.contains(OrganEnum.BONE));
        assertEquals(1, organs.size());


    }
}