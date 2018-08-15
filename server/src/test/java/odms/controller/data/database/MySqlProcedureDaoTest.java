package odms.controller.data.database;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.procedure.MySqlProcedureDAO;
import server.model.database.profile.MySqlProfileDAO;

public class MySqlProcedureDaoTest extends MySqlCommonTests {

    private MySqlProcedureDAO mySqlProcedureDAO;
    private MySqlProfileDAO mySqlProfileDAO;

    private Profile testProfile0 = new Profile("Joshua", "Wyllie", LocalDate.of(1997, 7, 18),
            "ABC1234");
    private Procedure testProcedurePending = new Procedure("Head Amputation",
            LocalDate.of(2020, 2, 22), "Head will be removed from neck. Fatal Procedure");
    private Procedure testProcedureNotPending = new Procedure("Head Amputation",
            LocalDate.of(2001, 2, 22), "Head will be removed from neck. Fatal Procedure");

    /**
     * Sets the Database to the test database and Initialises the DBO
     */
    @Before
    public void setUp() throws SQLException {
        mySqlProcedureDAO = new MySqlProcedureDAO();
        mySqlProfileDAO = new MySqlProfileDAO();

        mySqlProfileDAO.add(testProfile0);
        testProfile0 = mySqlProfileDAO.get("ABC1234");
        mySqlProcedureDAO.add(testProfile0, testProcedurePending);

    }

    @Test
    public void testAddProcedure() {
        mySqlProcedureDAO.add(testProfile0, testProcedureNotPending);
        assertEquals(1, mySqlProcedureDAO.getAll(testProfile0, false).size());
    }

    @Test
    public void testAddAffectedOrgan() {
        Procedure procedure = mySqlProcedureDAO.getAll(testProfile0, true).get(0);
        mySqlProcedureDAO.addAffectedOrgan(procedure, OrganEnum.LIVER);
        List<OrganEnum> affectedOrgans = mySqlProcedureDAO.getAffectedOrgans(procedure.getId());
        assertTrue(affectedOrgans.contains(OrganEnum.LIVER));
    }

    @Test
    public void testRemoveAffectedOrgans() {
        Procedure testProcedure = mySqlProcedureDAO.getAll(testProfile0, true).get(0);
        mySqlProcedureDAO.addAffectedOrgan(testProcedure, OrganEnum.LIVER);
        mySqlProcedureDAO.removeAffectedOrgan(testProcedure, OrganEnum.LIVER);
        int procedureId = testProcedure.getId();
        List<OrganEnum> affectedOrgans = mySqlProcedureDAO.getAffectedOrgans(procedureId);
        assertEquals(0, affectedOrgans.size());
    }

    @Test
    public void testRemove() {
        mySqlProcedureDAO.remove(mySqlProcedureDAO.getAll(testProfile0, true).get(0));

        List<Procedure> allProcedures = mySqlProcedureDAO.getAll(testProfile0, true);
        assertTrue(allProcedures.isEmpty());
    }

    @Test
    public void testUpdate() {
        Procedure testProcedure = mySqlProcedureDAO.getAll(testProfile0, true).get(0);
        testProcedure.setSummary("gg no re");
        mySqlProcedureDAO.update(testProcedure);
        assertEquals(testProcedure.getSummary(),
                mySqlProcedureDAO.getAll(testProfile0, true).get(0).getSummary());
    }

    @After
    public void tearDown() throws SQLException {

        List<Procedure> procedures = mySqlProcedureDAO.getAll(testProfile0, true);
        for (Procedure procedure : procedures) {
            mySqlProcedureDAO.removeAffectedOrgan(procedure, OrganEnum.LIVER);
            mySqlProcedureDAO.remove(procedure);
        }

        procedures = mySqlProcedureDAO.getAll(testProfile0, false);
        for (Procedure procedure : procedures) {
            mySqlProcedureDAO.removeAffectedOrgan(procedure, OrganEnum.LIVER);

            mySqlProcedureDAO.remove(procedure);
        }

        mySqlProfileDAO.remove(testProfile0);
    }
}
