package odms.controller.data.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.OrganConflictException;
import odms.commons.model.profile.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.organ.MySqlOrganDAO;
import server.model.database.profile.MySqlProfileDAO;

public class MySqlOrganDaoTest extends MySqlCommonTests {

    private Profile testProfile1;
    private Profile testProfile2;
    private MySqlProfileDAO mySqlProfileDAO;
    private OrganEnum organ2;
    private MySqlOrganDAO mysqlOrganDao;
    private OrganEnum organ3;
    private OrganEnum organ4;
    private OrganEnum organ5;

    @Before
    public void setup() throws SQLException, OrganConflictException {

        testProfile1 = new Profile("Jack", "Haaay", LocalDate.of(1998, 2, 27), "ABC1234");
        testProfile2 = new Profile("Tim", "Hamb-lame", LocalDate.of(1998, 2, 27), "ABC2345");
        organ2 = OrganEnum.LIVER;
        organ3 = OrganEnum.HEART;
        organ4 = OrganEnum.INTESTINE;
        organ5 = OrganEnum.CONNECTIVE_TISSUE;

        mysqlOrganDao = new MySqlOrganDAO();
        mySqlProfileDAO = new MySqlProfileDAO();

        mySqlProfileDAO.add(testProfile1);
        testProfile1 = mySqlProfileDAO.get("ABC1234");

        mySqlProfileDAO.add(testProfile2);
        testProfile2 = mySqlProfileDAO.get("ABC2345");

        mysqlOrganDao.addDonating(testProfile2, organ2);
        mysqlOrganDao.addDonation(testProfile2, organ3);
        mysqlOrganDao.addReceived(testProfile2, organ4);
        mysqlOrganDao.addRequired(testProfile2, organ5);

    }


    @Test
    public void testGetDonating() {
        assertTrue(mysqlOrganDao.getDonating(testProfile2).contains(organ2));
    }

    @Test
    public void testGetDonations() {
        assertTrue(mysqlOrganDao.getDonations(testProfile2).contains(organ3));
    }

    @Test
    public void testGetReceived() {
        assertTrue(mysqlOrganDao.getReceived(testProfile2).contains(organ4));
    }

    @Test
    public void testGetRequired() {
        assertTrue(mysqlOrganDao.getRequired(testProfile2).contains(organ5));
    }

    @Test
    public void testAddDonating() throws OrganConflictException {
        mysqlOrganDao.addDonating(testProfile1, organ2);
        assertTrue(mysqlOrganDao.getDonating(testProfile1).contains(organ2));
    }

    @Test
    public void testAddDonations() {
        mysqlOrganDao.addDonation(testProfile1, organ2);
        assertTrue(mysqlOrganDao.getDonations(testProfile1).contains(organ2));
    }

    @Test
    public void testAddReceived() {
        mysqlOrganDao.addReceived(testProfile1, organ2);
        assertTrue(mysqlOrganDao.getReceived(testProfile1).contains(organ2));
    }

    @Test
    public void testAddRequired() {
        mysqlOrganDao.addRequired(testProfile1, organ2);
        assertTrue(mysqlOrganDao.getRequired(testProfile1).contains(organ2));
    }

    @Test(expected = OrganConflictException.class)
    public void testAddDonatingDuplication() throws OrganConflictException {
        mysqlOrganDao.addDonating(testProfile1, organ2);
        mysqlOrganDao.addDonating(testProfile1, organ2);
    }

    @Test
    public void testRemoveDonating() {
        mysqlOrganDao.removeDonating(testProfile2, organ2);
        assertFalse(mysqlOrganDao.getDonating(testProfile2).contains(organ2));
    }

    @Test
    public void testRemoveDonation() {
        mysqlOrganDao.removeDonation(testProfile2, organ3);
        assertFalse(mysqlOrganDao.getDonations(testProfile2).contains(organ3));
    }

    @Test
    public void testRemoveReceived() {
        mysqlOrganDao.removeReceived(testProfile2, organ4);
        assertFalse(mysqlOrganDao.getReceived(testProfile2).contains(organ4));
    }

    @Test
    public void testRemoveRequired() {
        mysqlOrganDao.removeRequired(testProfile2, organ5);
        assertFalse(mysqlOrganDao.getRequired(testProfile2).contains(organ5));
    }

    @After
    public void tearDown() throws SQLException {
        mysqlOrganDao.removeDonating(testProfile2, organ2);
        mysqlOrganDao.removeDonation(testProfile2, organ3);
        mysqlOrganDao.removeReceived(testProfile2, organ4);
        mysqlOrganDao.removeRequired(testProfile2, organ5);

        mySqlProfileDAO.remove(testProfile2);
        mySqlProfileDAO.remove(testProfile1);
    }
}
