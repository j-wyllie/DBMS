package odms.controller.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest(odms.view.user.OrganMap.class)
public class OrganMapTest {

    private OrganMap controller = new OrganMap();
    private Profile donor;

    @Before
    public void setUp() throws OrganConflictException {
        donor = new Profile(99999);
        donor.setNhi("XXX9999");
        donor.addOrganDonating(OrganEnum.HEART);
        donor.setBloodType("B+");
        donor.setDateOfBirth(LocalDate.of(1998, 01, 01));
        donor.setEmail("nobody@doesnotexist.com");
        donor.setDateOfDeath(LocalDateTime.now());

        List<Profile> deadDonors = new ArrayList<>();
        deadDonors.add(donor);

        PowerMockito.stub(PowerMockito.method(
                odms.controller.database.profile.ProfileDAO.class, "getDead"))
                .toReturn(deadDonors);
    }

    @Test
    public void testGetDeadDonors() {
        controller.getDeadDonors();
    }

    @Test
    public void testGetDeadDonorsFiltered() {

    }

    @Test
    public void testGetReceivers() {

    }

    @Test
    public void testCreateNewPopover() {

    }

}
