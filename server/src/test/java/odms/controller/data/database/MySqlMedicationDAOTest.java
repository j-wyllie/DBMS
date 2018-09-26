package odms.controller.data.database;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import odms.commons.model.medications.Drug;
import odms.commons.model.profile.Profile;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.medication.MySqlMedicationDAO;
import server.model.database.profile.MySqlProfileDAO;

public class MySqlMedicationDAOTest extends CommonTestUtils {
    private MySqlMedicationDAO mySqlMedicationDAO;
    private MySqlProfileDAO mySqlProfileDAO;

    private Profile testProfile0 = new Profile("Jack", "Hay", LocalDate.of(1997, 7, 18), "SEX1337");
    private Drug testDrug0 = new Drug(0, "LSD");

    private boolean current;

    @Before
    public void setup() throws SQLException {
        mySqlMedicationDAO = new MySqlMedicationDAO();
        mySqlProfileDAO = new MySqlProfileDAO();

        mySqlProfileDAO.add(testProfile0);
        testProfile0 = mySqlProfileDAO.get("SEX1337");

        current = true;
        mySqlMedicationDAO.add(testDrug0, testProfile0.getId(), current);
        testDrug0 = mySqlMedicationDAO.getAll(testProfile0.getId(), current).get(0);
    }

    @Test
    public void testGetAll() {
        List<Drug> drugs = mySqlMedicationDAO.getAll(testProfile0.getId(), current);
        assertEquals(1, drugs.size());
    }


    @Test
    public void testRemove() {
        mySqlMedicationDAO.remove(testDrug0);
        List<Drug> drugs = mySqlMedicationDAO.getAll(testProfile0.getId(), current);
        assertEquals(0, drugs.size());
    }

    @Test
    public void testUpdate() {
        current = false;
        mySqlMedicationDAO.update(testDrug0, current);
        List<Drug> drugs = mySqlMedicationDAO.getAll(testProfile0.getId(), current);
        assertEquals(1, drugs.size());
    }

    @After
    public void tearDown() throws SQLException {
        mySqlMedicationDAO.remove(testDrug0);
        mySqlProfileDAO.remove(testProfile0);
    }
}
