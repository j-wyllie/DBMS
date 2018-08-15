package odms.controller.data.User;

import odms.controller.database.MySqlOrganDAO;
import odms.model.enums.OrganEnum;
import odms.model.profile.OrganConflictException;
import odms.model.profile.Profile;
import odms.view.profile.ProfileEdit;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;


public class AvailableOrgans {

    public Profile currentProfile;
    private odms.controller.user.AvailableOrgans controller;

    @Before
    public void setup() {
        ArrayList<String> profileOneAttr = new ArrayList<>();
        profileOneAttr.add("given-names=\"John\"");
        profileOneAttr.add("last-names=\"Wayne\"");
        profileOneAttr.add("dob=\"17-01-1998\"");
        profileOneAttr.add("nhi=\"123456879\"");
        currentProfile = new Profile(profileOneAttr);
        currentProfile.setId(9999);
        controller = new odms.controller.user.AvailableOrgans();
    }

    @Test
    public void testSetOrganExpired() {
        currentProfile.getOrgansDonating().add(OrganEnum.HEART);
        currentProfile.setDateOfDeath(LocalDateTime.now());
        controller.setOrganExpired(OrganEnum.HEART, currentProfile);
        assert(currentProfile.getOrgansExpired().contains(OrganEnum.HEART) && !currentProfile.getOrgansDonating().contains(OrganEnum.HEART));
    }

    @Test
    public void testGetExpiryTime() {
        LocalDateTime now = LocalDateTime.now();
        currentProfile.setDateOfDeath(now);
        LocalDateTime time = controller.getExpiryTime(OrganEnum.HEART, currentProfile);
        assert(time.equals(now.plusHours(6)));
    }

    @Test
    public void testGetAllOrgansAvailable() {
        try {
            currentProfile.getOrgansDonating().add(OrganEnum.HEART);
            currentProfile.setDateOfDeath(LocalDateTime.now());
            MySqlOrganDAO dao = new MySqlOrganDAO();
            dao.addDonating(currentProfile, OrganEnum.HEART);
            List<Map.Entry<Profile, OrganEnum>> testList = controller.getAllOrgansAvailable();
            assert(testList.size() != 0);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
