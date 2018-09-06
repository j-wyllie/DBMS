package odms.controller.data.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.UserNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import server.model.database.organ.MySqlOrganDAO;
import server.model.database.profile.MySqlProfileDAO;

public class MySqlProfileDaoTest extends MySqlCommonTests {
    private MySqlProfileDAO mySqlProfileDAO;

    private Profile testProfileLong0 = new Profile(1, "DSF5422", "JackONZ", true, false,
    "Jack", "Hay", LocalDate.of(1997, 12, 29), null, "male",
    180d, 81d, "O-", true, null, 133, 80, "123 fake street", "Canterbury", "314324134",
    "jha56@uclive.ac.nz", "NZ", "not", "Noot", "Neet", "Reee", LocalDateTime.now(),
            LocalDateTime.now(), null, null, null
    );
    private Profile testProfile0 = new Profile("Joshua", "Wyllie", LocalDate.of(1997, 7, 18), "ABC1234");
    private Profile testProfile1 = new Profile("Jack", "Hay", LocalDate.of(1998, 2, 27), "CBA43211");

    /**
     * Sets the Database to the test database and Initialises the DBO
     */
    @Before
    public void setUp() throws InterruptedException {
        mySqlProfileDAO = new MySqlProfileDAO();
        Thread.sleep(1000);
    }

    @Test
    public void testAddGet() throws SQLException {
        mySqlProfileDAO.add(testProfileLong0);
        Profile outProfile = mySqlProfileDAO.get("DSF5422");
        assertEquals(testProfileLong0.getNhi(), outProfile.getNhi());
    }

    @Test
    public void testGetWithId() throws SQLException {
        mySqlProfileDAO.add(testProfileLong0);
        Profile outProfile = mySqlProfileDAO.get("DSF5422");
        assertEquals(testProfileLong0.getNhi(), mySqlProfileDAO.get(outProfile.getId()).getNhi());
    }

    @Test
    public void testGetAll() throws SQLException {
        mySqlProfileDAO.add(testProfile0);
        mySqlProfileDAO.add(testProfile1);

        List<Profile> allOutProfiles = mySqlProfileDAO.getAll();
        assertEquals(2, allOutProfiles.size());
    }

    @Test
    public void testRemove() throws SQLException {
        mySqlProfileDAO.add(testProfile0);
        Profile testProfile0 = mySqlProfileDAO.get("ABC1234");
        mySqlProfileDAO.remove(testProfile0);
        List<Profile> allProfiles = mySqlProfileDAO.getAll();
        assertEquals(0, allProfiles.size());
    }

    @Test
    public void testIsUniqueUsername() throws SQLException {
        mySqlProfileDAO.add(testProfileLong0);
        boolean isUnique = mySqlProfileDAO.isUniqueUsername(testProfileLong0.getUsername());
        assertEquals(false, isUnique);
    }

    @Test
    public void testupdate() throws SQLException {
        mySqlProfileDAO.add(testProfile0);
        testProfile0 = mySqlProfileDAO.get("ABC1234");
        //todo way to set time of death
        testProfile0.setDateOfDeath(LocalDateTime.now());
        mySqlProfileDAO.update(testProfile0);

        assertEquals(LocalDate.now(), LocalDate.from(mySqlProfileDAO.get("ABC1234").getDateOfDeath()));
    }

    @Test
    public void testSearch() throws SQLException, OrganConflictException {

        mySqlProfileDAO.add(testProfileLong0);
        Profile newProfile = mySqlProfileDAO.get("DSF5422");
        MySqlOrganDAO mySqlOrganDAO = new MySqlOrganDAO();
        mySqlOrganDAO.addDonating(newProfile, OrganEnum.LIVER);
        Set<OrganEnum> organs = new HashSet();
        organs.add(OrganEnum.LIVER);
        Profile receivedProfile = mySqlProfileDAO.search("Jack", 20, 20,
                "canterbury", "male", "donor", organs).get(0);
        assertEquals(newProfile.getUsername(), receivedProfile.getUsername());
    }

    @Test
    public void testSize() throws SQLException {
        mySqlProfileDAO.add(testProfile0);
        assertEquals(1, mySqlProfileDAO.getAll().size());
    }

    @Test
    public void testGetAllReceiving() throws SQLException {
        testProfile0.setReceiver(true);
        mySqlProfileDAO.add(testProfile0);
        Profile newProfile = mySqlProfileDAO.get("ABC1234");
        MySqlOrganDAO mySqlOrganDAO = new MySqlOrganDAO();
        mySqlOrganDAO.addRequired(newProfile, OrganEnum.LIVER);
        assertEquals(1, mySqlProfileDAO.getAllReceiving().size());
    }

    @Test
    public void testGetOrganReceivers() throws SQLException {
        testProfileLong0.setReceiver(true);
        mySqlProfileDAO.add(testProfileLong0);
        Profile newProfile = mySqlProfileDAO.get("DSF5422");
        MySqlOrganDAO mySqlOrganDAO = new MySqlOrganDAO();
        mySqlOrganDAO.addRequired(newProfile, OrganEnum.BONE);
        assertEquals(testProfileLong0.getFullName(), mySqlProfileDAO.getOrganReceivers(
                "Bone", "O-", 12, 42).get(0).getFullName());
    }

    @Test
    public void testHasPassword() throws SQLException, UserNotFoundException {
        mySqlProfileDAO.add(testProfile0);
        mySqlProfileDAO.savePassword(testProfile0.getNhi(), "password");
        assertTrue(mySqlProfileDAO.hasPassword(testProfile0.getNhi()));
    }

    @Test
    public void testDoesntHavePassword() throws SQLException {
        mySqlProfileDAO.add(testProfile0);
        assertFalse(mySqlProfileDAO.hasPassword(testProfile0.getNhi()));
    }

    @Ignore
    public void testCheckCredentialsCorrect() throws SQLException, UserNotFoundException {
        mySqlProfileDAO.add(testProfile0);
        mySqlProfileDAO.savePassword(testProfile0.getNhi(), "password");
        assertTrue(mySqlProfileDAO.checkCredentials(testProfile0.getNhi(), "password"));
    }

    @Ignore
    public void testCheckCredentialsIncorrect() throws SQLException, UserNotFoundException {
        mySqlProfileDAO.add(testProfile0);
        mySqlProfileDAO.savePassword(testProfile0.getNhi(), "password");
        assertFalse(mySqlProfileDAO.checkCredentials(testProfile0.getNhi(), "wrong"));
    }

    @After
    public void cleanup() throws SQLException {
        ArrayList<Profile> profiles = (ArrayList<Profile>) mySqlProfileDAO.getAll();
        for (Profile profile : profiles) {
            mySqlProfileDAO.remove(profile);
        }

    }
}
