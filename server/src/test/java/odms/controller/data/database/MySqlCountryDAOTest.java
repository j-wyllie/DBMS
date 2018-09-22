package odms.controller.data.database;

import static org.junit.Assert.assertEquals;

import java.util.List;
import odms.commons.model.enums.CountriesEnum;
import org.junit.Before;
import org.junit.Test;
import server.model.database.settings.MySqlSettingsDAO;

public class MySqlCountryDAOTest extends MySqlCommonTests {
    private MySqlSettingsDAO mySqlCountryDAO;

    @Before
    public void setup() {
        mySqlCountryDAO = new MySqlSettingsDAO();
        mySqlCountryDAO.updateCountries(CountriesEnum.NZ, false);
    }

    @Test
    public void testGetAll() {
        List<String> countries = mySqlCountryDAO.getAllCountries();
        // Changes this if our list of countries changes
        assertEquals(201, countries.size());
    }

    @Test
    public void testGetAllValid() {
        List<String> countries = mySqlCountryDAO.getAllCountries(false);
        assertEquals(1, countries.size());
    }

    @Test
    public void testUpdate() {
        mySqlCountryDAO.updateCountries(CountriesEnum.NZ, true);
        List<String> countries = mySqlCountryDAO.getAllCountries(false);
        assertEquals(0, countries.size());
    }

    @Test
    public void testPopulateCountriesTable() {
        mySqlCountryDAO.populateCountriesTable();
        assertEquals(201, mySqlCountryDAO.getAllCountries().size());
    }
}
