package odms.controller.data.database;

import odms.commons.model.profile.HLAType;
import odms.commons.model.profile.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.hlatype.HLATypeDAO;
import server.model.database.profile.ProfileDAO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MySQLHLATypeDAOTest {
    private Profile testProfile1;
    private Profile testProfile2;
    private HLAType hlaType1;
    private HLAType hlaType2;

    HLATypeDAO hlaTypeDao = DAOFactory.getHLATypeDAO();
    ProfileDAO profileDao = DAOFactory.getProfileDao();

    @Before
    public void setup() throws SQLException {
        hlaType1 = new HLAType(5,2,3,4,5,6,7,8,9,10,11,12);
        hlaType2 = new HLAType(1,2,3,4,5,6,7,8,9,10,11,12);
        testProfile1 = new Profile("Jack", "Haaay", LocalDate.of(1998, 2, 27), "ABC1234");
        testProfile2 = new Profile("Tim", "Hamb-lame", LocalDate.of(1998, 2, 27), "ABC2345");

        profileDao.add(testProfile1);
        testProfile1 = profileDao.get("ABC1234");

        profileDao.add(testProfile2);
        testProfile2 = profileDao.get("ABC2345");
    }

    @After
    public void tearDown() throws SQLException {
        hlaTypeDao.remove(testProfile1.getId());
        hlaTypeDao.remove(testProfile2.getId());
        profileDao.remove(testProfile1);
        profileDao.remove(testProfile2);
    }

    @Test
    public void testAddHLAType() {
        assertEquals(hlaTypeDao.get(testProfile2.getId()).getGroupX(), hlaType2.getGroupX());
        assertEquals(hlaTypeDao.get(testProfile2.getId()).getGroupY(), hlaType2.getGroupY());
        assertEquals(hlaTypeDao.get(testProfile2.getId()).getSecondaryAntigens(), hlaType2.getSecondaryAntigens());
    }

    @Test
    public void testRemoveHLAType() {
        hlaTypeDao.add(testProfile2.getId(), hlaType2);
        hlaTypeDao.remove(testProfile2.getId());
        assertEquals(hlaTypeDao.get(testProfile2.getId()), null);
    }

    @Test
    public void testUpdateHLAType() {
        hlaTypeDao.add(testProfile2.getId(), hlaType2);
        hlaTypeDao.update(hlaType1, testProfile2.getId());
        assertNotEquals(hlaTypeDao.get(testProfile2.getId()).getGroupX(), hlaType2.getGroupX());
    }

}
