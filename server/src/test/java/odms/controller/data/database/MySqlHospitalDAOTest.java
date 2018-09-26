package odms.controller.data.database;

import odms.commons.model.locations.Hospital;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.locations.HospitalDAO;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MySqlHospitalDAOTest extends MySqlCommonTests {

    private Hospital testHospital1;
    private Hospital testHospital2;
    private HospitalDAO hospitalDAO;
    private Hospital testHospital3;

    @Before
    public void setup() throws SQLException {
        testHospital1 = new Hospital("testHospital1", 30.00, 30.00, null);
        testHospital2 = new Hospital("testHospital2", null, null, "Fake Street");
        testHospital3 = new Hospital("testHospital3", null, null, "64 Fake Street");
        hospitalDAO = DAOFactory.getHospitalDAO();
        hospitalDAO.add(testHospital1);
        hospitalDAO.add(testHospital2);
    }

    @Test
    public void getAll() throws SQLException {
        List<Hospital> hospitals = hospitalDAO.getAll();
        assertEquals(testHospital1.getName(), hospitals.get(0).getName());
        assertEquals(testHospital2.getName(), hospitals.get(1).getName());
        assertEquals(2, hospitals.size());
    }

    @Test
    public void get() throws SQLException {
        Hospital result = hospitalDAO.get(testHospital1.getName());
        assertEquals(testHospital1.getName(), result.getName());
    }

    @Test
    public void getById() throws SQLException {
        Hospital expected = hospitalDAO.get(testHospital1.getName());
        Hospital result = hospitalDAO.get(expected.getId());
        assertEquals(expected.getName(), result.getName());
    }

    @Test
    public void add() throws SQLException {
        try {
            hospitalDAO.add(testHospital3);
            Hospital hospital = hospitalDAO.get(testHospital3.getName());
            assertEquals(testHospital3.getName(), hospital.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        hospitalDAO.remove(testHospital3.getName());
    }

    @Test
    public void edit() throws SQLException {
        Hospital hospital = hospitalDAO.get(testHospital1.getName());
        hospital.setAddress("1 Fake Street");
        hospitalDAO.edit(hospital);
        Hospital hospital1 = hospitalDAO.get(testHospital1.getName());
        assertEquals(hospital.getAddress(), hospital1.getAddress());
    }

    @Test
    public void remove() throws SQLException {
        hospitalDAO.add(testHospital3);
        List<Hospital> hospitals = hospitalDAO.getAll();
        int size = hospitals.size();
        hospitalDAO.remove(testHospital3.getName());
        hospitals = hospitalDAO.getAll();
        assertEquals(size - 1, hospitals.size());
    }

    @After
    public void tearDown() throws SQLException {
        hospitalDAO.remove(testHospital2.getName());
        hospitalDAO.remove(testHospital1.getName());
    }
}