package odms.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import odms.enums.OrganEnum;
import odms.profile.Procedure;
import odms.profile.Profile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MySqlProcedureDAOtest {
    private MySqlProcedureDAO mySqlProcedureDAO;

    private Profile testProfile0 = new Profile("Joshua", "Wyllie", LocalDate.of(1997, 7, 18), "ABC1234");
    private Procedure testProcedure0 = new Procedure("Head Amputation", LocalDate.of(2020, 2, 22), "Head will be removed from neck. Fatal Procedure");

    /**
     * Sets the Database to the test database and Initialises the DBO
     */
    @Before
    public void setUp() {
        DatabaseConnection.setConfig("/config/db_test.config");
        mySqlProcedureDAO = new MySqlProcedureDAO();
        testProfile0.setId(243);
    }

    /**
     * Tests adding and getting a procedure by id
     */
    @Test
    public void testAddGet() {
        mySqlProcedureDAO.add(testProfile0, testProcedure0);

        List<Procedure> outProcedures = mySqlProcedureDAO.getAll(testProfile0, true);
        Assert.assertTrue(outProcedures.contains(testProcedure0));
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
        DatabaseConnection.setConfig("/config/db.config");
    }
}
