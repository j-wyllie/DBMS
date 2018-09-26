package odms.controller.data.database;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import server.model.database.hla.HLA;
import odms.commons.model.profile.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.hla.HLADAO;
import server.model.database.profile.ProfileDAO;

/**
 * Tests: HLA Database Access Object.
 */
public class MySQLHLADAOTest {

    private Profile testProfile1 = new Profile(
            "Jack",
            "Haaay",
            LocalDate.of(1998, 2, 27),
            "ABC1234"
    );
    private Profile testProfile2 = new Profile(
            "Tim",
            "Hamb-lame",
            LocalDate.of(1998, 2, 27),
            "ABC2345"
    );
    private HLA hlaGroupX = new HLA("AC", 10, true, false);
    private HLA hlaGroupY = new HLA("DP", 99, false, true);
    private HLA hlaNonPrimary = new HLA("BB", 45, false, false);

    private HLADAO hlaDao = DAOFactory.getHLADAO();
    private ProfileDAO profileDao = DAOFactory.getProfileDao();

    @Before
    public void setup() throws SQLException {
        profileDao.add(testProfile1);
        testProfile1 = profileDao.get("ABC1234");

        profileDao.add(testProfile2);
        testProfile2 = profileDao.get("ABC2345");

        hlaDao.add(testProfile1.getId(), hlaGroupX);
        hlaDao.add(testProfile1.getId(), hlaGroupY);
        hlaDao.add(testProfile1.getId(), hlaNonPrimary);
    }

    @After
    public void tearDown() throws SQLException {
        for (Integer profileId : Arrays.asList(testProfile1.getId(), testProfile2.getId())) {
            List<HLA> removeHla = getHlasForProfile(profileId);

            for (HLA hla : removeHla) {
                hlaDao.remove(profileId, hla);
            }
        }

        profileDao.remove(testProfile1);
        profileDao.remove(testProfile2);
    }

    @Test
    public void testGetGroupX() {
        List<HLA> results = hlaDao.getGroupX(testProfile1.getId());

        assertEquals(1, results.size());
        compareHla(hlaGroupX, results.get(0));
    }

    @Test
    public void testGetGroupY() {
        List<HLA> results = hlaDao.getGroupY(testProfile1.getId());

        assertEquals(1, results.size());
        compareHla(hlaGroupY, results.get(0));
    }

    @Test
    public void testGetNonPrimary() {
        List<HLA> results = hlaDao.getNonPrimary(testProfile1.getId());

        assertEquals(1, results.size());
        compareHla(hlaNonPrimary, results.get(0));
    }

    @Test
    public void testAdd() {
        int numberOfHlas = getHlasForProfile(testProfile1.getId()).size();

        hlaDao.add(testProfile1.getId(), hlaGroupX);
        assertEquals(numberOfHlas + 1, getHlasForProfile(testProfile1.getId()).size());
    }

    @Test
    public void testRemove() {
        HLA hlaX = new HLA("CC", 5, true, false);
        HLA hlaY = new HLA("DQ", 40, false, true);
        HLA hlaN = new HLA("GG", 99, false, false);

        hlaDao.add(testProfile2.getId(), hlaX);
        hlaDao.add(testProfile2.getId(), hlaY);
        hlaDao.add(testProfile2.getId(), hlaN);

        int numberOfHlas = getHlasForProfile(testProfile2.getId()).size();

        hlaDao.remove(testProfile2.getId(), hlaX);
        hlaDao.remove(testProfile2.getId(), hlaY);
        hlaDao.remove(testProfile2.getId(), hlaN);

        assertEquals(numberOfHlas - 3, getHlasForProfile(testProfile2.getId()).size());

    }

    @Test
    public void testEdit() {
        HLA hlaX = new HLA("CC", 5, true, false);
        HLA hlaX_2 = new HLA("CC", 5, true, false);
        HLA hlaY = new HLA("DQ", 40, false, true);
        HLA hlaN = new HLA("GG", 99, false, false);
        //Edit consists of removing current HLA and adding a new one.
        hlaDao.add(testProfile2.getId(), hlaX);
        hlaDao.remove(testProfile2.getId(), hlaX);
        hlaDao.add(testProfile2.getId(), hlaX_2);
        HLA dataCheck = hlaDao.getGroupX(testProfile2.getId()).get(0);

        assertEquals(hlaX_2.getNumericValue(),dataCheck.getNumericValue());
        assertEquals(hlaX_2.getAlphaValue(),dataCheck.getAlphaValue());
        assertEquals(hlaX_2.getGroupX(),dataCheck.getGroupX());
        assertEquals(hlaX_2.getGroupY(),dataCheck.getGroupY());
    }

    /**
     * Support function to deeply assert the returned HLA objects assert equally.
     *
     * @param hlaOne the expected HLA.
     * @param hlaTwo the selected HLA.
     */
    private void compareHla(HLA hlaOne, HLA hlaTwo) {
        assertEquals(hlaOne.getAlphaValue(), hlaTwo.getAlphaValue());
        assertEquals(hlaOne.getGroupX(), hlaTwo.getGroupX());
        assertEquals(hlaOne.getGroupY(), hlaTwo.getGroupY());
        assertEquals(hlaOne.getNumericValue(), hlaTwo.getNumericValue());
    }

    /**
     * Get all HLA's for a selected profile ID.
     *
     * @param profileId the profile ID to query against.
     * @return the HLA's for selected profile ID.
     */
    private List<HLA> getHlasForProfile(Integer profileId) {
        List<HLA> results = new ArrayList<>();
        results.addAll(hlaDao.getGroupX(profileId));
        results.addAll(hlaDao.getGroupY(profileId));
        results.addAll(hlaDao.getNonPrimary(profileId));

        return results;
    }
}
