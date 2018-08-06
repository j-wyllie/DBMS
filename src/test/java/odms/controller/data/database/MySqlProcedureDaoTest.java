package odms.controller.data.database;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.time.LocalDate;

import java.util.List;
import odms.controller.database.DatabaseConnection;
import odms.controller.database.MySqlProcedureDAO;
import odms.controller.database.MySqlProfileDAO;
import odms.model.enums.OrganEnum;
import odms.model.profile.Procedure;
import odms.model.profile.Profile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MySqlProcedureDaoTest {
    private MySqlProcedureDAO mySqlProcedureDAO;
    private MySqlProfileDAO mySqlProfileDAO;

    private Profile testProfile0 = new Profile("Joshua", "Wyllie", LocalDate.of(1997, 7, 18), "ABC1234");
    private Procedure testProcedure0 = new Procedure("Head Amputation", LocalDate.of(2020, 2, 22), "Head will be removed from neck. Fatal Procedure");

    /**
     * Sets the Database to the test database and Initialises the DBO
     */
    @Before
    public void setUp() throws SQLException {
        DatabaseConnection.setConfig("/config/db_test.config");
        mySqlProcedureDAO = new MySqlProcedureDAO();
        mySqlProfileDAO = new MySqlProfileDAO();

        mySqlProfileDAO.add(testProfile0);
        testProfile0 = mySqlProfileDAO.get("ABC1234");
    }

    /**
     * Tests adding and getting a procedure by id
     */
    @Test
    public void testAddProcedure() {
        mySqlProcedureDAO.add(testProfile0, testProcedure0);

        assertEquals(1, mySqlProcedureDAO.getAll(testProfile0, true).size());
    }

    /**
     * Tests adding an affected organ to a procedure
     */
    @Test
    public void testAddAffectedOrgan() {
        mySqlProcedureDAO.addAffectedOrgan(testProcedure0, OrganEnum.LIVER);
        int procedureId = testProcedure0.getId();
        List<OrganEnum> affectedOrgans = mySqlProcedureDAO.getAffectedOrgans(procedureId);
        Assert.assertTrue(affectedOrgans.contains(OrganEnum.LIVER));
    }

    /**
     * Tests removing an affected organ to a procedure
     */
    @Test
    public void testRemoveAffectedOrgans() {
        mySqlProcedureDAO.removeAffectedOrgan(testProcedure0, OrganEnum.LIVER);
        int procedureId = testProcedure0.getId();
        List<OrganEnum> affectedOrgans = mySqlProcedureDAO.getAffectedOrgans(procedureId);
        Assert.assertTrue(affectedOrgans.isEmpty());
    }

    /**
     * Tests removing a procedure
     */
    @Test
    public void testRemove() {
        mySqlProcedureDAO.remove(testProcedure0);

        List<Procedure> allProcedures = mySqlProcedureDAO.getAll(testProfile0, true);
        Assert.assertTrue(allProcedures.isEmpty());
    }

    /**
     * Sets the database back to the production database
     */
    @After
    public void cleanUp() {
        DatabaseConnection.setConfig("/src/config/db.config");
    }
}
