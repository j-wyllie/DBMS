package odms.controller.data.database;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;
import odms.commons.model.enums.CountriesEnum;
import odms.server.CommonTestUtils;
import org.junit.Before;
import org.junit.Test;
import server.model.database.country.MySqlCountryDAO;

public class MySqlCountryDAOTest extends CommonTestUtils {
    private MySqlCountryDAO mySqlCountryDAO;

    @Before
    public void setup() throws SQLException {
        mySqlCountryDAO = new MySqlCountryDAO();
        mySqlCountryDAO.update(CountriesEnum.NZ, false);
    }

    @Test
    public void testGetAll() {
        List<String> countries = mySqlCountryDAO.getAll();
        // Changes this if our list of countries changes
        assertEquals(201, countries.size());
    }

    @Test
    public void testGetAllValid() {
        List<String> countries = mySqlCountryDAO.getAll(false);
        assertEquals(1, countries.size());
    }

    @Test
    public void testUpdate() throws SQLException {
        mySqlCountryDAO.update(CountriesEnum.NZ, true);
        List<String> countries = mySqlCountryDAO.getAll(false);
        assertEquals(0, countries.size());
    }

    @Test
    public void testPopulateCountriesTable() throws SQLException {
        mySqlCountryDAO.populateCountriesTable();
        assertEquals(201, mySqlCountryDAO.getAll().size());
    }
}
