package odms.controller.data.database;

import odms.commons.model.locations.Hospital;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.locations.HospitalDAO;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MySqlHospitalDAOTest extends CommonTestUtils {

    private Hospital testHospital1;
    private Hospital testHospital2;
    private Hospital testHospital3;

    private HospitalDAO hospitalDAO = DAOFactory.getHospitalDAO();

    @Before
    public void setup() throws SQLException {
        Hospital testHospitalOne = new Hospital(
                "testHospital1",
                30.00,
                30.00,
                null
        );
        Hospital testHospitalTwo = new Hospital(
                "testHospital2",
                null,
                null,
                "Fake Street"
        );
        hospitalDAO.add(testHospitalOne);
        testHospital1 = hospitalDAO.get(testHospitalOne.getName());
        hospitalDAO.add(testHospitalTwo);
        testHospital2 = hospitalDAO.get(testHospitalTwo.getName());

        // Not added to DB
        testHospital3 = new Hospital(
                "testHospital3",
                null,
                null,
                "64 Fake Street"
        );
    }

    @After
    public void tearDown() throws SQLException {
        hospitalDAO.remove(testHospital1.getId());
        hospitalDAO.remove(testHospital2.getId());
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
        hospitalDAO.add(testHospital3);
        Hospital hospital = hospitalDAO.get(testHospital3.getName());
        assertEquals(testHospital3.getName(), hospital.getName());
        hospitalDAO.remove(hospital.getId());
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

    @Test
    public void removeById() throws SQLException {
        Integer id = -1;
        hospitalDAO.add(testHospital3);
        List<Hospital> hospitals = hospitalDAO.getAll();
        int size = hospitals.size();
        for (Hospital hospital : hospitals) {
            if (hospital.getName().equals(testHospital3.getName())) {
                id = hospital.getId();
            }
        }
        hospitalDAO.remove(id);
        hospitals = hospitalDAO.getAll();
        assertEquals(size - 1, hospitals.size());
    }
}
