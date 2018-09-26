package odms.controller.data.database;

import odms.commons.model.enums.OrganEnum;
import odms.commons.model.locations.Hospital;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.procedure.MySqlProcedureDAO;
import server.model.database.profile.MySqlProfileDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ProcedureDAOTest extends CommonTestUtils {

    private MySqlProcedureDAO procedureDAO = new MySqlProcedureDAO();
    private MySqlProfileDAO sqlProfileDAO = new MySqlProfileDAO();

    private Profile testProfile0 = new Profile(
            "Joshua",
            "Wyllie",
            LocalDate.of(1997, 7, 18),
            "ABC1234"
    );
    private Procedure testProcedurePending = new Procedure(
            "Head Amputation",
            LocalDate.of(2020, 2, 22),
            "Head will be removed from neck. Fatal Procedure"
    );
    private Procedure testProcedureNotPending = new Procedure(
            "Head Amputation",
            LocalDate.of(2001, 2, 22),
            "Head will be removed from neck. Fatal Procedure"
    );

    /**
     * Sets the Database to the test database and Initialises the DBO
     */
    @Before
    public void setUp() throws SQLException {
        sqlProfileDAO.add(testProfile0);
        testProfile0 = sqlProfileDAO.get("ABC1234");
        procedureDAO.add(testProfile0.getId(), testProcedurePending);

    }

    @Test
    public void testAddProcedure() {
        procedureDAO.add(testProfile0.getId(), testProcedureNotPending);
        assertEquals(1, procedureDAO.getAll(testProfile0.getId(), false).size());
    }

    @Test
    public void testAddAffectedOrgan() {
        Procedure procedure = procedureDAO.getAll(testProfile0.getId(), true).get(0);
        procedureDAO.addAffectedOrgan(procedure, OrganEnum.LIVER);
        List<OrganEnum> affectedOrgans = procedureDAO.getAffectedOrgans(procedure.getId());
        assertTrue(affectedOrgans.contains(OrganEnum.LIVER));
    }

    @Test
    public void testRemoveAffectedOrgans() {
        Procedure testProcedure = procedureDAO.getAll(testProfile0.getId(), true).get(0);
        procedureDAO.addAffectedOrgan(testProcedure, OrganEnum.LIVER);
        procedureDAO.removeAffectedOrgan(testProcedure, OrganEnum.LIVER);
        int procedureId = testProcedure.getId();
        List<OrganEnum> affectedOrgans = procedureDAO.getAffectedOrgans(procedureId);
        assertEquals(0, affectedOrgans.size());
    }

    @Test
    public void testRemove() {
        procedureDAO.remove(procedureDAO.getAll(testProfile0.getId(), true).get(0));

        List<Procedure> allProcedures = procedureDAO.getAll(testProfile0.getId(), true);
        assertTrue(allProcedures.isEmpty());
    }

    @Test
    public void testUpdate() {
        Procedure testProcedure = procedureDAO.getAll(testProfile0.getId(), true).get(0);
        testProcedure.setSummary("gg no re");
        testProcedure.setHospital(new Hospital("Unspecified", 0.0, 0.0, "", -1));
        procedureDAO.update(testProcedure, true);
        assertEquals(testProcedure.getSummary(),
                procedureDAO.getAll(testProfile0.getId(), true).get(0).getSummary());
    }

    @After
    public void tearDown() throws SQLException {

        List<Procedure> procedures = procedureDAO.getAll(testProfile0.getId(), true);
        for (Procedure procedure : procedures) {
            procedureDAO.removeAffectedOrgan(procedure, OrganEnum.LIVER);
            procedureDAO.remove(procedure);
        }

        procedures = procedureDAO.getAll(testProfile0.getId(), false);
        for (Procedure procedure : procedures) {
            procedureDAO.removeAffectedOrgan(procedure, OrganEnum.LIVER);

            procedureDAO.remove(procedure);
        }

        sqlProfileDAO.remove(testProfile0);
    }
}
