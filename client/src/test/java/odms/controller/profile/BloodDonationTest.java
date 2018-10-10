package odms.controller.profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.profile.Profile;
import odms.controller.database.profile.HttpProfileDAO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpProfileDAO.class)
public class BloodDonationTest {
    public odms.controller.profile.BloodDonation controller;
    private Profile currentProfile;

    @Before
    public void setup() {
        PowerMockito.stub(PowerMockito.method(HttpProfileDAO.class, "update"))
                .toReturn(null);

        List<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        currentProfile.setId(9999);
        currentProfile.setBloodType("O+");
        controller = new odms.controller.profile.BloodDonation();
    }

    @Test
    public void testCorrectPointsForBloodTypeOPos() {
        currentProfile.setBloodType("O+");
        Assert.assertEquals(2, controller.setPoints(currentProfile));
    }

    @Test
    public void testCorrectPointsForBloodTypeONeg() {
        currentProfile.setBloodType("O-");
        Assert.assertEquals(3, controller.setPoints(currentProfile));
    }

    @Test
    public void testCorrectPointsForBloodTypeAPos() {
        currentProfile.setBloodType("A+");
        Assert.assertEquals(2, controller.setPoints(currentProfile));
    }

    @Test
    public void testCorrectPointsForBloodTypeANeg() {
        currentProfile.setBloodType("A-");
        Assert.assertEquals(4, controller.setPoints(currentProfile));
    }

    @Test
    public void testCorrectPointsForBloodTypeBPos() {
        currentProfile.setBloodType("B+");
        Assert.assertEquals(3, controller.setPoints(currentProfile));
    }

    @Test
    public void testCorrectPointsForBloodTypeBNeg() {
        currentProfile.setBloodType("B-");
        Assert.assertEquals(5, controller.setPoints(currentProfile));
    }

    @Test
    public void testCorrectPointsForBloodTypeABPos() {
        currentProfile.setBloodType("AB+");
        Assert.assertEquals(4, controller.setPoints(currentProfile));
    }

    @Test
    public void testCorrectPointsForBloodTypeABNeg() {
        currentProfile.setBloodType("AB-");
        Assert.assertEquals(5, controller.setPoints(currentProfile));
    }

    @Test
    public void testCorrectPointsForRecentDonation() {
        currentProfile.setLastBloodDonation(LocalDateTime.now());
        Assert.assertEquals(3, controller.setPoints(currentProfile));
    }

    @Test
    public void testPointsUpdate() {
        PowerMockito.stub(PowerMockito.method(HttpProfileDAO.class, "updateBloodDonation"))
                .toReturn(null);
        currentProfile.setLastBloodDonation(LocalDateTime.now());
        controller.updatePoints(currentProfile, false);
        Assert.assertEquals(3, currentProfile.getBloodDonationPoints());
    }

    @Test
    public void testPointsUpdatePlasmaDonation() {
        PowerMockito.stub(PowerMockito.method(HttpProfileDAO.class, "updateBloodDonation"))
                .toReturn(null);
        currentProfile.setLastBloodDonation(LocalDateTime.now());
        controller.updatePoints(currentProfile, true);
        Assert.assertEquals(5, currentProfile.getBloodDonationPoints());
    }

    @After
    public void tearDown() {
        currentProfile.setBloodDonationPoints(0);
    }

}
