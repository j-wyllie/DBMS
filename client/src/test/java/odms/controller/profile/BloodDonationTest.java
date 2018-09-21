package odms.controller.profile;

import odms.commons.model.profile.Profile;
import odms.view.profile.BloodDonation;
import odms.view.profile.ProfileMedical;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BloodDonationTest {

    public BloodDonation view;
    public odms.controller.profile.BloodDonation controller;
    public Profile currentProfile;

    @Before
    public void setup() {
        List<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        view = new BloodDonation();
        currentProfile = new Profile(profileOneAttr);
        currentProfile.setBloodType("O+");
        controller = new odms.controller.profile.BloodDonation(view);
    }

    @Test
    public void testCorrectPointsForBloodType() {
        Assert.assertEquals(2, controller.setPoints(currentProfile));
    }

    @Test
    public void testCorrectPointsForRecentDonation() {
        currentProfile.setLastBloodDonation(LocalDateTime.now());
        Assert.assertEquals(3, controller.setPoints(currentProfile));
    }

    @Test
    public void testPointsUpdate() {
        currentProfile.setLastBloodDonation(LocalDateTime.now());
        controller.updatePoints(currentProfile, false);
        Assert.assertEquals(3, currentProfile.getBloodDonationPoints());
    }

    @Test
    public void testPointsUpdatePlasmaDonation() {
        currentProfile.setLastBloodDonation(LocalDateTime.now());
        controller.updatePoints(currentProfile, true);
        Assert.assertEquals(5, currentProfile.getBloodDonationPoints());
    }

}
