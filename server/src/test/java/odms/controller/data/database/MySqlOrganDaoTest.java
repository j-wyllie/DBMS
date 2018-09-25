package odms.controller.data.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.organ.OrganDAO;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.UserDAO;

@Slf4j
public class MySqlOrganDaoTest extends MySqlCommonTests {
    private static ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private static UserDAO userDAO = DAOFactory.getUserDao();

    private static User testUser;

    private Profile testProfile0;
    private Profile testProfile1;

    private OrganDAO organDAO = DAOFactory.getOrganDao();

    private OrganEnum organ0 = OrganEnum.LIVER;
    private OrganEnum organ1 = OrganEnum.HEART;
    private OrganEnum organ2 = OrganEnum.INTESTINE;
    private OrganEnum organ3 = OrganEnum.CONNECTIVE_TISSUE;

    @BeforeClass
    public static void setupClass() throws SQLException, UserNotFoundException {
        testUser = new User(UserType.CLINICIAN, "Clinician", "Auckland");
        testUser.setUsername("Bob");
        testUser.setDefault(false);
        testUser.setWorkAddress(null);
        testUser.setPictureName(null);
        testUser.setPassword("test");
        userDAO.add(testUser);
        testUser = userDAO.get(testUser.getUsername());
    }

    @Before
    public void setup() throws OrganConflictException {
        testProfile0 = new Profile(
                "Jack",
                "Haaay",
                LocalDate.of(1998, 2, 27),
                "ABC2345"
        );
        testProfile1 = new Profile(
                "Tim",
                "Hamb-lame",
                LocalDate.of(1998, 2, 27),
                "ABC6789"
        );

        try {
            profileDAO.add(this.testProfile0);
            this.testProfile0 = profileDAO.get(testProfile0.getNhi());

            profileDAO.add(this.testProfile1);
            this.testProfile1 = profileDAO.get(testProfile1.getNhi());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        organDAO.addDonating(testProfile1, organ0);
        organDAO.addDonation(testProfile1, organ1);
        organDAO.addReceived(testProfile1, organ2);
        organDAO.addRequired(testProfile1, organ3);
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        userDAO.remove(testUser);
    }

    @After
    public void tearDown() throws SQLException {
        profileDAO.remove(testProfile1);
        profileDAO.remove(testProfile0);
    }

    @Test
    public void testAddDonating() throws OrganConflictException {
        organDAO.addDonating(testProfile0, organ0);
        assertTrue(organDAO.getDonating(testProfile0).contains(organ0));
    }

    @Test
    public void testAddDonations() {
        organDAO.addDonation(testProfile1, organ0);
        assertTrue(organDAO.getDonations(testProfile1).contains(organ0));
    }

    @Test
    public void testAddReceived() {
        organDAO.addReceived(testProfile1, organ0);
        organDAO.getReceived(testProfile1);
        assertTrue(organDAO.getReceived(testProfile1).contains(organ0));
    }

    @Test
    public void testAddRequired() {
        organDAO.addRequired(testProfile1, organ0);
        assertTrue(organDAO.getRequired(testProfile1).contains(organ0));
    }

    @Test(expected = OrganConflictException.class)
    public void testAddDonatingDuplication() throws OrganConflictException {
        organDAO.addDonating(testProfile0, organ0);
        organDAO.addDonating(testProfile0, organ0);
    }

    @Test
    public void testGetDonating() {
        assertTrue(organDAO.getDonating(testProfile1).contains(organ0));
    }

    @Test
    public void testGetDonations() {
        assertTrue(organDAO.getDonations(testProfile1).contains(organ1));
    }

    @Test
    public void testGetReceived() {
        assertTrue(organDAO.getReceived(testProfile1).contains(organ2));
    }

    @Test
    public void testGetRequired() {
        assertTrue(organDAO.getRequired(testProfile1).contains(organ3));
    }

    @Test
    public void testRemoveDonating() {
        organDAO.removeDonating(testProfile1, organ0);
        assertFalse(organDAO.getDonating(testProfile1).contains(organ0));
    }

    @Test
    public void testRemoveDonation() {
        organDAO.removeDonation(testProfile1, organ1);
        assertFalse(organDAO.getDonations(testProfile1).contains(organ1));
    }

    @Test
    public void testRemoveReceived() {
        organDAO.removeReceived(testProfile1, organ2);
        assertFalse(organDAO.getReceived(testProfile1).contains(organ2));
    }

    @Test
    public void testRemoveRequired() {
        organDAO.removeRequired(testProfile1, organ3);
        assertFalse(organDAO.getRequired(testProfile1).contains(organ3));
    }

    @Test
    public void testSetAndGetExpired() throws  SQLException, UserNotFoundException {
        assertTrue(organDAO.getExpired(testProfile1).isEmpty());
        organDAO.setExpired(testProfile1, organ0, 1, "test_expired", userDAO.get("Bob").getStaffID());
        assertFalse(organDAO.getExpired(testProfile1).isEmpty());
    }

    @Test
    public void testRevertExpired() throws  SQLException, UserNotFoundException{
        organDAO.setExpired(testProfile1, organ0, 1, "test_expired", userDAO.get("Bob").getStaffID());
        assertFalse(organDAO.getExpired(testProfile1).isEmpty());
        organDAO.revertExpired(testProfile1.getId(), organ0);
        assertTrue(organDAO.getExpired(testProfile1).isEmpty());
    }
}
