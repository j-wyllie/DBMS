package odms.controller.data.database;

import static org.junit.Assert.assertEquals;

import java.util.List;
import odms.controller.database.MySqlCountryDAO;
import odms.model.enums.CountriesEnum;
import org.junit.Before;
import org.junit.Test;

public class MySqlCountryDAOTest extends MySqlCommonTests {
    private MySqlCountryDAO mySqlCountryDAO;

    @Before
    public void setup() {
        mySqlCountryDAO = new MySqlCountryDAO();
        mySqlCountryDAO.update(CountriesEnum.NZ, false);
    }

    @Test
    public void testGetAll() {
        List<String> countries = mySqlCountryDAO.getAll();
        // Changes this if our list of countries changes
        assertEquals(countries.size(), 243);
    }

    @Test
    public void testGetAllValid() {
        List<String> countries = mySqlCountryDAO.getAll(false);
        assertEquals(countries.size(), 1);
    }

    @Test
    public void testUpdate() {
        mySqlCountryDAO.update(CountriesEnum.NZ, true);
        List<String> countries = mySqlCountryDAO.getAll(false);
        assertEquals(countries.size(), 0);
    }
}
